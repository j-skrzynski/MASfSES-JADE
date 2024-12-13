package org.example.agents.stockexchange.behaviours;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import org.example.agents.stockexchange.StockExchangeAgent;
import org.example.datamodels.StockSymbol;
import org.example.datamodels.command.Command;
import org.example.datamodels.command.CommandParser;
import org.example.datamodels.order.AwaitingOrder;
import org.example.datamodels.order.OrderExpirationType;
import org.example.datamodels.order.OrderType;
import org.example.logic.stockexchange.order.awaitingorder.AwaitingExchangeOrder;
import org.example.logic.stockexchange.order.marketorder.NoLimitExchangeOrder;
import org.example.logic.stockexchange.order.marketorder.ExchangeOrder;
import org.example.logic.stockexchange.order.PlacableDisposition;
import org.example.logic.stockexchange.utils.*;

import com.google.gson.Gson;

import java.util.List;

public class OrderProcessingBehaviour extends CyclicBehaviour {
    private StockExchangeAgent agent;
    private Gson gson;

    public OrderProcessingBehaviour(StockExchangeAgent agent) {
        super(agent);
        this.agent = agent;
        this.gson = new Gson(); // JSON parser
    }

    @Override
    public void action() {
        ACLMessage msg = agent.receive();
        if (msg != null) {
            String content = msg.getContent();
            ACLMessage reply = msg.createReply();

            try {
                AID sender = msg.getSender();
                String response = handleRequest(content, sender);
                reply.setPerformative(ACLMessage.INFORM);
                reply.setContent(response);
            } catch (Exception e) {
                reply.setPerformative(ACLMessage.FAILURE);
                reply.setContent("Error: " + e.getMessage());
            }

            agent.send(reply);
        } else {
            block();
        }
    }

    private String handleRequest(String content, AID sender) throws Exception {
        // Parsowanie JSON-a
        CommandParser cp = new CommandParser();
        Command command = cp.parseCommand(content);

        String cmd = command.getCommand();
        List<Object> arguments = command.getArguments();
        String traderName = command.getTraderName();  // Nowe pole
        String brokerName = command.getBrokerName();  // Nowe pole
        String exchangeName = command.getExchangeName();  // Nowe pole

        switch (cmd) {
            case "ADD_STOCK":
                return handleAddStock(arguments);
            case "PLACE_ORDER":
                Object orderSpec = arguments.get(0);
                if(!(orderSpec instanceof AwaitingOrder)) {
                   throw new IllegalArgumentException("PLACE_ORDER requires an AwaitingOrder");
                }
                return processPlaceOrder((AwaitingOrder)orderSpec, sender, traderName, brokerName, exchangeName);
            case "ADVANCE_SESSION":
                return advanceSession();
            case "GET_TOP_BUY":
                return getTopBuy(arguments);
            case "GET_TOP_SELL":
                return getTopSell(arguments);
            default:
                throw new IllegalArgumentException("Unknown command: " + cmd);
        }
    }

    private String handleAddStock(List<Object> arguments) {
        if (arguments.size() < 4) {
            throw new IllegalArgumentException("ADD_STOCK requires at least 4 parameters.");
        }
        String name = (String) arguments.get(0);
        String shortName = (String) arguments.get(1);
        double ipoPrice = (Double) arguments.get(2);
        long shares = ((Number) arguments.get(3)).longValue();

        StockSymbol stockSymbol = new StockSymbol(name, shortName, ipoPrice, shares);
        agent.getStockExchange().addStock(stockSymbol);
        return "Stock added: " + name;
    }

    private String processPlaceOrder(AwaitingOrder orderSpec, AID sender, String traderName, String brokerName, String exchangeName) {

        String orderCommand = orderSpec.getOrderCommand();
        OrderType orderType = orderSpec.getOrder().getOrderType();
        String expirationSpecification = orderSpec.getOrder().getExpirationSecpification();
        String symbolShortName = orderSpec.getOrder().getSymbol().getShortName();
        long quantity = orderSpec.getOrder().getQuantity();

        StockSymbol symbol = agent.getStockExchange().getSymbolByShortName(symbolShortName);
        ExchangeDate expirationDate = processExpirationSpecification(expirationSpecification);

        PlacableDisposition disposition = null;
        switch (orderCommand) {
            case "LIMIT":
                double price = orderSpec.getOrder().getPrice();
                disposition = new ExchangeOrder(symbol, orderType, expirationDate, price, quantity,
                        new OrderSubmitter(traderName, brokerName, sender, exchangeName));  // Zmieniony konstruktor
                break;
            case "NOLIMIT":
                disposition = new NoLimitExchangeOrder(symbol, orderType, expirationDate, quantity,
                        new OrderSubmitter(traderName, brokerName, sender, exchangeName));  // Zmieniony konstruktor
                break;
            case "STOP":
                double activationPrice = orderSpec.getPrice();
                disposition = new AwaitingExchangeOrder(new NoLimitExchangeOrder(symbol, orderType, expirationDate, quantity,
                        new OrderSubmitter(traderName, brokerName, sender, exchangeName)), activationPrice);
                break;
            case "STOPLIMIT":
                price = orderSpec.getPrice();
                activationPrice = orderSpec.getOrder().getPrice();
                disposition = new AwaitingExchangeOrder(new ExchangeOrder(symbol, orderType, expirationDate, price, quantity,
                        new OrderSubmitter(traderName, brokerName, sender, exchangeName)), activationPrice);
                break;
        }

        agent.getStockExchange().placeOrder(symbol, disposition);
        return "OK";
    }

    private String advanceSession() {
        agent.getStockExchange().advanceExchangeDateBySession();
        return "Exchange session advanced.";
    }

    private String getTopBuy(List<Object> arguments) {
        if (arguments.size() != 1) {
            throw new IllegalArgumentException("GET_TOP_BUY requires a single parameter (stock short name).");
        }
        String shortName = (String) arguments.get(0);
        StockSymbol stockSymbol = agent.getStockExchange().getSymbolByShortName(shortName);
        List<ExchangeOrder> orders = agent.getStockExchange().getTopBuyOffers(stockSymbol);
        return "Top Buy Offers: " + orders.toString();
    }

    private String getTopSell(List<Object> arguments) {
        if (arguments.size() != 1) {
            throw new IllegalArgumentException("GET_TOP_SELL requires a single parameter (stock short name).");
        }
        String shortName = (String) arguments.get(0);
        StockSymbol stockSymbol = agent.getStockExchange().getSymbolByShortName(shortName);
        List<ExchangeOrder> orders = agent.getStockExchange().getTopSellOffers(stockSymbol);
        return "Top Sell Offers: " + orders.toString();
    }

    private ExchangeDate processExpirationSpecification(String expiration) {
        String[] parts = expiration.split("/");

        if (parts.length < 1) {
            throw new IllegalArgumentException("Expiration specification cannot be empty");
        }

        OrderExpirationType expirationType = OrderExpirationType.fromString(parts[0]);
        ExchangeDate currentSessionStart = agent.getStockExchange().getCurrentSessionStart();

        switch (expirationType) {
            case D:
                return ExpirationDateCalculator.getDDate(currentSessionStart);
            case WDD:
                if (parts.length < 2) {
                    throw new IllegalArgumentException("WDD type requires number of sessions specified.");
                }
                long sessionsCount = Long.parseLong(parts[1]);
                long sessionsTillYearEnd = agent.getStockExchange().getSessionsTillYearEnd();
                return ExpirationDateCalculator.getWDDDate(currentSessionStart, sessionsCount, sessionsTillYearEnd);
            case WDA:
                long sessionsToYearEnd = agent.getStockExchange().getSessionsTillYearEnd();
                return ExpirationDateCalculator.getWDADate(currentSessionStart, sessionsToYearEnd);
            case WDC:
                if (parts.length < 3) {
                    throw new IllegalArgumentException("WDC type requires number of sessions and milliseconds specified.");
                }
                long sessions = Long.parseLong(parts[1]);
                long milliseconds = Long.parseLong(parts[2]);
                return ExpirationDateCalculator.getWDCDate(currentSessionStart, sessions, milliseconds);
            default:
                throw new IllegalArgumentException("Unsupported expiration type: " + expirationType);
        }
    }
}

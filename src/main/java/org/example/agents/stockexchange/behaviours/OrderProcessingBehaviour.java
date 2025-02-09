package org.example.agents.stockexchange.behaviours;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
import org.example.logic.stockexchange.order.PlaceableDisposition;
import org.example.logic.stockexchange.order.awaitingorder.AwaitingExchangeOrder;
import org.example.logic.stockexchange.order.marketorder.ExchangeOrder;
import org.example.logic.stockexchange.order.marketorder.NoLimitExchangeOrder;
import org.example.logic.stockexchange.utils.ExchangeDate;
import org.example.logic.stockexchange.utils.ExpirationDateCalculator;
import org.example.logic.stockexchange.utils.OrderSubmitter;

import java.util.List;

public class OrderProcessingBehaviour extends CyclicBehaviour {
    private final StockExchangeAgent agent;
    private final Gson gson = new Gson();

    public OrderProcessingBehaviour(StockExchangeAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @Override
    public void action() {
        ACLMessage msg = agent.receive();
        if (msg != null && msg.getPerformative() != ACLMessage.FAILURE) {
            String content = msg.getContent();
            ACLMessage reply = msg.createReply();

            try {
                AID sender = msg.getSender();
                String response = handleRequest(content, sender);
                reply.setPerformative(ACLMessage.INFORM);
                reply.setOntology("Broker-StockExchange");
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

    private String handleRequest(String content, AID sender) {
        CommandParser cp = new CommandParser();
        Command command = cp.parseCommand(content);

        String cmd = command.getCommand();
        List<Object> arguments = command.getArguments();
        String traderName = command.getTraderName();
        String brokerName = command.getBrokerName();
        String brokerOrderId = command.getBrokerOrderId();

        switch (cmd) {
            case "ADD_STOCK":
                return handleAddStock(arguments);
            case "PLACE_ORDER":
                Object orderSpec = arguments.getFirst();
                if (!(orderSpec instanceof AwaitingOrder)) {
                    throw new IllegalArgumentException("PLACE_ORDER requires an AwaitingOrder");
                }
                return processPlaceOrder((AwaitingOrder) orderSpec, sender, traderName, brokerName, brokerOrderId);
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

    private String processPlaceOrder(
            AwaitingOrder orderSpec,
            AID sender,
            String traderName,
            String brokerName,
            String exchangeName
    ) {

        String orderCommand = orderSpec.getOrderCommand();
        OrderType orderType = orderSpec.order().getOrderType();
        String expirationSpecification = orderSpec.order().getExpirationSpecification();
        String symbolShortName = orderSpec.order().getSymbol().getShortName();
        long quantity = orderSpec.order().getQuantity();

        StockSymbol symbol = agent.getStockExchange().getSymbolByShortName(symbolShortName);
        ExchangeDate expirationDate = processExpirationSpecification(expirationSpecification);

        double price = orderSpec.order().getPrice();
        double activationPrice = orderSpec.price();

        PlaceableDisposition disposition = null;
        switch (orderCommand) {
            case "LIMIT":
                disposition = new ExchangeOrder(
                        symbol,
                        orderType,
                        expirationDate,
                        price,
                        quantity,
                        new OrderSubmitter(traderName, brokerName, sender, exchangeName)
                );  // Zmieniony konstruktor
                break;
            case "NOLIMIT":
                disposition = new NoLimitExchangeOrder(
                        symbol,
                        orderType,
                        expirationDate,
                        quantity,
                        new OrderSubmitter(traderName, brokerName, sender, exchangeName)
                );  // Zmieniony konstruktor
                break;
            case "STOP":
                disposition = new AwaitingExchangeOrder(
                        new NoLimitExchangeOrder(
                                symbol,
                                orderType,
                                expirationDate,
                                quantity,
                                new OrderSubmitter(traderName, brokerName, sender, exchangeName)
                        ),
                        activationPrice
                );
                break;
            case "STOPLIMIT":
                price = orderSpec.price();
                activationPrice = orderSpec.order().getPrice();
                disposition = new AwaitingExchangeOrder(
                        new ExchangeOrder(
                                symbol,
                                orderType,
                                expirationDate,
                                price,
                                quantity,
                                new OrderSubmitter(traderName, brokerName, sender, exchangeName)
                        ),
                        activationPrice
                );
                break;
        }

        agent.getStockExchange().placeOrder(symbol, disposition);

        JsonObject commandObject = new JsonObject();
        commandObject.addProperty("command", "INFORM");
        commandObject.addProperty("exchangeName", agent.getStockExchange().getName());
        commandObject.addProperty("traderName", traderName);
        commandObject.addProperty("brokerOrderId", brokerName);
        commandObject.add("arguments", new JsonArray());
        return gson.toJson(commandObject);
    }

    private String advanceSession() {
        agent.getStockExchange().advanceExchangeDateBySession();
        return "Exchange session advanced.";
    }

    private String getTopBuy(List<Object> arguments) {
        if (arguments.size() != 1) {
            throw new IllegalArgumentException("GET_TOP_BUY requires a single parameter (stock short name).");
        }
        String shortName = (String) arguments.getFirst();
        StockSymbol stockSymbol = agent.getStockExchange().getSymbolByShortName(shortName);
        List<ExchangeOrder> orders = agent.getStockExchange().getTopBuyOffers(stockSymbol);
        return gson.toJson(orders);
    }

    private String getTopSell(List<Object> arguments) {
        if (arguments.size() != 1) {
            throw new IllegalArgumentException("GET_TOP_SELL requires a single parameter (stock short name).");
        }
        String shortName = (String) arguments.getFirst();
        StockSymbol stockSymbol = agent.getStockExchange().getSymbolByShortName(shortName);
        List<ExchangeOrder> orders = agent.getStockExchange().getTopSellOffers(stockSymbol);
        return gson.toJson(orders);
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

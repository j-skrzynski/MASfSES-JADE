package org.example.agents.stockexchange.behaviours;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import org.example.agents.stockexchange.StockExchangeAgent;
import org.example.logic.stockexchange.order.AwaitingOrder;
import org.example.logic.stockexchange.order.NoLimitOrder;
import org.example.logic.stockexchange.order.Order;
import org.example.logic.stockexchange.order.PlacableDisposition;
import org.example.logic.stockexchange.utils.*;

import java.util.List;


public class OrderProcessingBehaviour extends CyclicBehaviour {
    private StockExchangeAgent agent;
    public OrderProcessingBehaviour(StockExchangeAgent agent) {
        super(agent);
        this.agent = agent;
    }
    @Override
    public void action() {
        ACLMessage msg = agent.receive();
        if (msg != null) {
            String content = msg.getContent();
            ACLMessage reply = msg.createReply();

            try {
                AID sender = msg.getSender();
                String response = handleRequest(content,sender);


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



    private String handleRequest(String content,AID sender) throws Exception {
        String[] comd = content.split("#");
        if (comd.length != 2) {
            throw new IllegalArgumentException("Invalid request. <trader>#<command> was expected. Hash must appear exactly once separating sender from the command");
        }
        String[] parts = comd[1].split(";");
        if (parts.length < 1) {
            throw new IllegalArgumentException("Command not provided. After # the command specification is expected. It is semicoln-separated. First element is command name");
        }
        String command = parts[0];

        switch (command) {
            case "ADD_STOCK":
                if(parts.length<5){
                    throw new IllegalArgumentException("ADD_STOCK requires at 5 parameters. Usage: <traderName>#ADD_STOCK;<long name>;<short name>;<ipo price>;<total number of shares>  In this request <traderName> mat be empty");
                }
                String long_name = parts[1];
                String short_name = parts[2];
                Double IPOPrice = Double.parseDouble(parts[3]);
                Long shares = Long.parseLong(parts[4]);

                return addStock(long_name,short_name,IPOPrice,shares);
            case "PLACE_ORDER":
                OrderSubmitter submitter = new OrderSubmitter(comd[0],sender);
                return processOrderCommand(parts,submitter);
//                String orderCommand = parts[1];
//                String orderType = parts[2];
//
//                return processOrderCommand(orderCommand,orderType, Arrays.copyOfRange(parts, 2, parts.length););
                //return placeOrder(parts[1], parts[2], Integer.parseInt(parts[3]), Double.parseDouble(parts[4]));
            case "ADVANCE_SESSION":
                return advanceSession();
            case "GET_TOP_BUY":
                if (parts.length != 2) {
                    throw new IllegalArgumentException("GET_TOP_BUY requires a single parameter - stock short name");
                }
                return getTopBuy(parts[1]);
            case "GET_TOP_SELL":
                if (parts.length != 2) {
                    throw new IllegalArgumentException("GET_TOP_SELL requires a single parameter - stock short name");
                }
                return getTopSell(parts[1]);
            default:
                throw new IllegalArgumentException("Unknown command: " + command);
        }
    }


    private String addStock(String name, String shortName, double ipoPrice, Long shares) {
        StockSymbol stockSymbol = new StockSymbol(name, shortName, ipoPrice, shares);
        agent.getStockExchange().addStock(stockSymbol);
        return "Stock added: " + name;
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
                // Ważne na dzień bieżący
                return OrderExpirationType.getDDate(currentSessionStart);

            case WDD:
                if (parts.length < 2) {
                    throw new IllegalArgumentException("WDD type requires number of sessions specified. Usage: WDD/<numberOfSessions>");
                }
                long sessionsCount = Long.parseLong(parts[1]);
                long sessionsTillYearEnd = agent.getStockExchange().getSessionsTillYearEnd();
                return OrderExpirationType.getWDDDate(currentSessionStart, sessionsCount, sessionsTillYearEnd);

            case WDA:
                long sessionsToYearEnd = agent.getStockExchange().getSessionsTillYearEnd();
                return OrderExpirationType.getWDADate(currentSessionStart, sessionsToYearEnd);

            case WDC:
                if (parts.length < 3) {
                    throw new IllegalArgumentException("WDC type requires number of sessions and milliseconds specified. Usage: WDC/<numberOfSessions>/<numberOfMilliseconds>");
                }
                long sessions = Long.parseLong(parts[1]);
                long milliseconds = Long.parseLong(parts[2]);
                return OrderExpirationType.getWDCDate(currentSessionStart, sessions, milliseconds);

//            case WNF:
//                // Implementacja logiki dla "Ważne na fixing"
//                return agent.getStockExchange().getNextFixingDate(currentSessionStart);
//
//            case WNZ:
//                // Implementacja logiki dla "Ważne na zamknięcie"
//                return agent.getStockExchange().getEndOfClosingPhaseDate(currentSessionStart);

            default:
                throw new IllegalArgumentException("Unsupported expiration type: " + expirationType);
        }

    }

    private String processOrderCommand(String[] parts,OrderSubmitter submitter) {
        if (parts.length < 6) {
            throw new IllegalArgumentException("Orders commands require at least 6 parameters. Usage: <traderName>#PLACE_ORDER;<orderName>;<orderType>;<expirationSpecification>;<symbolShortName>;<quantity>;...");
        }

        String orderCommand = parts[1];
        OrderType orderType = OrderType.fromString(parts[2]);
        String expirtationSpecicication = parts[3];

        String symbolShortName = parts[4];
        Long quantity = Long.parseLong(parts[5]);
        StockSymbol symbol = agent.getStockExchange().getSymbolByShortName(symbolShortName);
        ExchangeDate expirationDate = processExpirationSpecification(expirtationSpecicication);

        Double price;
        Double activationPrice;
        PlacableDisposition disposition=null;
        switch (orderCommand) {
            case "LIMIT":
                if (parts.length < 7) {
                    throw new IllegalArgumentException("Limit specification cannot be empty. Usage: <traderName>#PLACE_ORDER;LIMIT;<orderType>;<expirationSpecification>;<symbolShortName>;<quantity>;<price>");
                }
                price = Double.parseDouble(parts[6]);
                disposition = new Order(symbol,orderType,expirationDate,price,quantity,submitter);
                break;
            case "NOLIMIT":
                disposition = new NoLimitOrder(symbol,orderType,expirationDate,quantity,submitter);
                break;
            case "STOP":
                if (parts.length < 7) {
                    throw new IllegalArgumentException("Activation price specification cannot be empty. Usage: <traderName>#PLACE_ORDER;STOP;<orderType>;<expirationSpecification>;<symbolShortName>;<quantity>;<activationPrice>");
                }
                activationPrice = Double.parseDouble(parts[6]);
                disposition = new AwaitingOrder(new NoLimitOrder(symbol,orderType,expirationDate,quantity,submitter),activationPrice);
                break;
            case "STOPLIMIT":
                if (parts.length < 8) {
                    throw new IllegalArgumentException("Missing arguments. Usage: <traderName>#PLACE_ORDER;STOPLIMIT;<orderType>;<expirationSpecification>;<symbolShortName>;<quantity>;<price>;<activationPrice>");
                }
                price = Double.parseDouble(parts[6]);
                activationPrice = Double.parseDouble(parts[7]);
                disposition = new AwaitingOrder(new Order(symbol,orderType,expirationDate,price,quantity,submitter),activationPrice);
                break;
        }
        agent.getStockExchange().placeOrder(symbol,disposition);
        return "OK";
    }




//    private String placeOrder(String symbol, String type, int quantity, double price) {
//        StockSymbol stockSymbol = new StockSymbol(symbol, 0);
//        Order order = new Order(stockSymbol, type.equalsIgnoreCase("BUY") ? Order.OrderType.BUY : Order.OrderType.SELL);
//        order.setQuantity(quantity);
//        order.setPrice(price);
//
//        stockExchange.placeOrder(stockSymbol, order);
//        return "Order placed: " + type + " " + quantity + " @ " + price;
//    }

    private String advanceSession() {
        agent.getStockExchange().advanceExchangeDateBySession();
        return "Exchange session advanced.";
    }

    private String getTopBuy(String symbolShortName) {
        StockSymbol stockSymbol = agent.getStockExchange().getSymbolByShortName(symbolShortName);
        List<Order> orders = agent.getStockExchange().getTopBuyOffers(stockSymbol);
        return "Top Buy Offers: " + orders.toString();
    }

    private String getTopSell(String symbolShortName) {
        StockSymbol stockSymbol = agent.getStockExchange().getSymbolByShortName(symbolShortName);
        List<Order> orders = agent.getStockExchange().getTopSellOffers(stockSymbol);
        return "Top Sell Offers: " + orders.toString();
    }
}

package org.example.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import org.example.agents.stockexchange.TimeHandlingBehaviour;
import org.example.stockexchange.StockExchange;
import org.example.stockexchange.order.Order;
import org.example.stockexchange.settlements.TransactionSettlement;
import org.example.stockexchange.utils.ExchangeDate;
import org.example.stockexchange.utils.StockSymbol;

import java.util.List;

public class StockExchangeAgent extends Agent {

    private StockExchange stockExchange;
    public StockExchange getStockExchange() {
        return stockExchange;
    }
    @Override
    protected void setup() {
        // Initialize the stock exchange
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            String exchangeName = (String) args[0];
            stockExchange = new StockExchange(exchangeName, new ExchangeDate());
            System.out.println("StockExchangeAgent started: " + exchangeName);
        } else {
            stockExchange = new StockExchange("Default Exchange", new ExchangeDate());
            System.out.println("StockExchangeAgent started: Default Exchange");
        }

        addBehaviour(new TimeHandlingBehaviour(this, 1000, 180* 1000, 2000));


        // Add behavior to handle incoming requests
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    String content = msg.getContent();
                    ACLMessage reply = msg.createReply();

                    try {
                        String response = handleRequest(content);
                        reply.setPerformative(ACLMessage.INFORM);
                        reply.setContent(response);
                    } catch (Exception e) {
                        reply.setPerformative(ACLMessage.FAILURE);
                        reply.setContent("Error: " + e.getMessage());
                    }

                    send(reply);
                } else {
                    block();
                }
            }
        });

        // Add behavior to broadcast settlements
        addBehaviour(new TickerBehaviour(this, 5000) { // Every 5 seconds
            @Override
            protected void onTick() {
                broadcastSettlements();
            }
        });
    }

    /**
     * Handle incoming requests.
     */
    private String handleRequest(String content) throws Exception {
        String[] parts = content.split(" ");
        String command = parts[0];

        switch (command) {
            case "ADD_STOCK":
                return addStock(parts[1], Double.parseDouble(parts[2]));
            case "PLACE_ORDER":
                return placeOrder(parts[1], parts[2], Integer.parseInt(parts[3]), Double.parseDouble(parts[4]));
            case "ADVANCE_SESSION":
                return advanceSession();
            case "GET_TOP_BUY":
                return getTopBuy(parts[1]);
            case "GET_TOP_SELL":
                return getTopSell(parts[1]);
            default:
                throw new IllegalArgumentException("Unknown command: " + command);
        }
    }



    /**
     * Broadcast available settlements to all agents in the system.
     */
    private void broadcastSettlements() {
        for (StockSymbol symbol : stockExchange.getAvailableSymbols()) {
            TransactionSettlement settlement = stockExchange.popNextSettlement(symbol);

            while (settlement != null) {
                ACLMessage broadcast = new ACLMessage(ACLMessage.INFORM);
                broadcast.setContent("Settlement: " + settlement.toString());

                // Broadcast to all agents
                for (AID aid : getAllAgentAIDs()) {
                    broadcast.addReceiver(aid);
                }

                send(broadcast);

                // Fetch the next settlement for this symbol
                settlement = stockExchange.popNextSettlement(symbol);
            }
        }
    }

    /**
     * Mock method to get all AIDs in the system (replace with real discovery if needed).
     */
    private AID[] getAllAgentAIDs() {
        // In a real system, this would query the DF (Directory Facilitator) for agents.
        // Here we use static names for simplicity.
        return new AID[]{
                new AID("TraderAgent1", AID.ISLOCALNAME),
                new AID("TraderAgent2", AID.ISLOCALNAME),
                new AID("LoggerAgent", AID.ISLOCALNAME)
        };
    }
}

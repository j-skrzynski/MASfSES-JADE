package org.example.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import org.example.agents.stockexchange.OrderProcessingBehaviour;
import org.example.agents.stockexchange.SettlementSendingBehaviour;
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
            stockExchange = new StockExchange(exchangeName, new ExchangeDate(),3*60*1000L,252L);
            System.out.println("StockExchangeAgent started: " + exchangeName);
        } else {
            stockExchange = new StockExchange("Default Exchange", new ExchangeDate(),3*60*1000L,252L);
            System.out.println("StockExchangeAgent started: Default Exchange");
        }

        addBehaviour(new TimeHandlingBehaviour(this, 1000, stockExchange.getMillisecondsPerSession(), 2000));
        addBehaviour(new OrderProcessingBehaviour(this));
        addBehaviour(new SettlementSendingBehaviour(this,1000));
    }

    /**
     * Broadcast available settlements to all agents in the system.
     */
//    private void broadcastSettlements() {
////        for (StockSymbol symbol : stockExchange.getAvailableSymbols()) {
////            TransactioanSettlement settlement = stockExchange.popNextSettlement(symbol);
////
////            while (settlement != null) {
////                ACLMessage broadcast = new ACLMessage(ACLMessage.INFORM);
////                broadcast.setContent("Settlement: " + settlement.toString());
////
////                // Broadcast to all agents
////                for (AID aid : getAllAgentAIDs()) {
////                    broadcast.addReceiver(aid);
////                }
////
////                send(broadcast);
////
////                // Fetch the next settlement for this symbol
////                settlement = stockExchange.popNextSettlement(symbol);
////            }
////        }
//    }

}

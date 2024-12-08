package org.example.agents.stockexchange;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import org.example.agents.StockExchangeAgent;
import org.example.stockexchange.settlements.TransactionSettlement;
import org.example.stockexchange.utils.StockSymbol;

public class SettlementSendingBehaviour extends TickerBehaviour {
    private StockExchangeAgent agent;
    public SettlementSendingBehaviour(StockExchangeAgent a, long period) {
        super(a, period);
        agent = a;
    }

    @Override
    protected void onTick() {
        broadcastSettlements();
    }

    private void broadcastSettlements() {
        for (StockSymbol symbol : agent.getStockExchange().getAllStocksInExchange()) {
            TransactionSettlement settlement = agent.getStockExchange().popNextSettlement(symbol);

            while (settlement != null) {
                ACLMessage message = new ACLMessage(ACLMessage.INFORM);
                message.setContent(settlement.toJson());
                message.addReceiver(settlement.getAddressee().getBroker());
                agent.send(message);

                // Pobierz następne rozliczenie dla tego symbolu
                settlement = agent.getStockExchange().popNextSettlement(symbol);
            }
        }
    }
}


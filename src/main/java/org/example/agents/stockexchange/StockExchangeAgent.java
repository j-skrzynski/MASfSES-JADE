package org.example.agents.stockexchange;

import jade.core.Agent;
import org.example.agents.stockexchange.behaviours.CancellationSendingBehaviour;
import org.example.agents.stockexchange.behaviours.OrderProcessingBehaviour;
import org.example.agents.stockexchange.behaviours.SettlementSendingBehaviour;
import org.example.agents.stockexchange.behaviours.TimeHandlingBehaviour;
import org.example.datamodels.StockSymbol;
import org.example.logic.stockexchange.StockExchange;
import org.example.logic.stockexchange.utils.EnvRecord;
import org.example.logic.stockexchange.utils.ExchangeDate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Queue;

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
            stockExchange = new StockExchange(exchangeName, new ExchangeDate(),2*1000L,252L);
            System.out.println("StockExchangeAgent started: " + exchangeName);
            if (args.length > 1) {
                Collection<StockSymbol> symbols = (Collection<StockSymbol>) args[1];
                for (StockSymbol symbol : symbols) {
                    this.getStockExchange().addStock(symbol);
                }
            }
            if (args.length > 2) {
                HashMap<String, Queue<EnvRecord>> queues = (HashMap<String, Queue<EnvRecord>>) args[2];
                stockExchange.getBaseline().setBaseline(queues);
                stockExchange.loadArtificialData();
            }
        } else {
            stockExchange = new StockExchange(
                    "Default Exchange",
                    new ExchangeDate(),
                    3 * 60 * 1000L,
                    252L
            );
            System.out.println("StockExchangeAgent started: Default Exchange");
        }

        addBehaviour(new TimeHandlingBehaviour(this, 200, stockExchange.getMillisecondsPerSession(), 200));
        addBehaviour(new OrderProcessingBehaviour(this));
        addBehaviour(new SettlementSendingBehaviour(this,200));
        addBehaviour(new CancellationSendingBehaviour(this,200));
    }
}

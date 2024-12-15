package org.example.agents.stockexchange.behaviours;

import jade.core.behaviours.TickerBehaviour;
import org.example.agents.stockexchange.StockExchangeAgent;

public class TimeHandlingBehaviour extends TickerBehaviour {
    private StockExchangeAgent agent;
    private final Long milliseconds;
    private final Long millisecondsPerSession;
    private final Long expirationUpdateInterval;

    public TimeHandlingBehaviour(StockExchangeAgent a, long period, long millisecondsPerSession, long expirationUpdateInterval) {
        super(a, period);
        agent = a;
        milliseconds = period;
        this.millisecondsPerSession = millisecondsPerSession;
        this.expirationUpdateInterval = expirationUpdateInterval;
    }

    @Override
    protected void onTick() {
        agent.getStockExchange().addMillisecondsSinceStart(milliseconds);
        if(agent.getStockExchange().getMillisecondsSinceStart() > millisecondsPerSession){
            agent.getStockExchange().advanceExchangeDateBySession();
        }
        if(agent.getStockExchange().getMillisecondsSinceStart() > 0 && agent.getStockExchange().getMillisecondsSinceStart() % expirationUpdateInterval == 0){
            agent.getStockExchange().expirationUpdate();
        }
    }
}

package org.example.agents.investor.behaviours;

import jade.core.behaviours.TickerBehaviour;
import org.example.agents.investor.InvestorAgent;
import org.example.agents.investor.InvestorPriceRecordLabel;
import org.example.global.StockPriceDictionary;

import java.util.List;
import java.util.Set;

public class PriceCheckerBehaviour extends TickerBehaviour {

    private InvestorAgent agent;

    public PriceCheckerBehaviour(InvestorAgent agent,Long period) {
        super(agent,period);
        this.agent = agent;
    }

    @Override
    protected void onTick() {
        Set< InvestorPriceRecordLabel > stocks = agent.getObservedStocks();
        for (InvestorPriceRecordLabel stock : stocks) {
            Double price = StockPriceDictionary.getPrice(stock.shortName(),stock.stockExchangeName());
            agent.updateStoredPrice(stock,price);
            System.out.println(stock.shortName()+":"+price+"**************************");
        }
    }
}

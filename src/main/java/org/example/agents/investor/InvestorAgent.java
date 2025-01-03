package org.example.agents.investor;

import jade.core.Agent;
import org.example.agents.investor.behaviours.PriceCheckerBehaviour;

import java.util.*;

public class InvestorAgent extends Agent {
    /*
    todo
    - behaviour for asking for the price
    - behaviour for deciding on broker offer
    -
     */
    protected Set<InvestorPriceRecordLabel> observedStocks;
    protected HashMap<InvestorPriceRecordLabel, Double> bestBuyPrice;
    protected HashMap<InvestorPriceRecordLabel, Double> bestSellPrice;
    protected HashMap<InvestorPriceRecordLabel, Double> lastPrice;


    public InvestorAgent() {
        observedStocks = new HashSet<>();
        bestBuyPrice = new HashMap<>();
        bestSellPrice = new HashMap<>();
        lastPrice = new HashMap<>();

    }

    private void registerObservedStock(InvestorPriceRecordLabel label){
        observedStocks.add(label);
        bestBuyPrice.put(label, null);
        bestSellPrice.put(label, null);
        lastPrice.put(label, null);
    }

    @Override
    protected void setup() {
        super.setup();
        Object[] args = getArguments();
        Collection<InvestorPriceRecordLabel> stocksToObserve = (Collection<InvestorPriceRecordLabel>) args[0];
        for (InvestorPriceRecordLabel stock : stocksToObserve) {
            registerObservedStock(stock);
        }
        this.addBehaviour(new PriceCheckerBehaviour(this, 5000L)); // checks prices every second
    }


    public Set<InvestorPriceRecordLabel> getObservedStocks() {
        return observedStocks;
    }
    public void updateBuyPrice(InvestorPriceRecordLabel label, Double price){
        bestBuyPrice.put(label, price);
    }
    public void updateSellPrice(InvestorPriceRecordLabel label, Double price){
        bestSellPrice.put(label, price);
    }
    public void updateLastPrice(InvestorPriceRecordLabel label, Double price){
        lastPrice.put(label, price);
    }
}

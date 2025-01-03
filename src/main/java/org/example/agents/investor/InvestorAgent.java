package org.example.agents.investor;

import jade.core.Agent;
import org.example.agents.investor.behaviours.PriceCheckerBehaviour;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class InvestorAgent extends Agent {
    /*
    todo
    - behaviour for asking for the price
    - behaviour for deciding on broker offer
    -
     */
    protected HashMap<InvestorPriceRecordLabel, Double> observedStocks;

    public InvestorAgent() {
        observedStocks = new HashMap<>();

    }

    @Override
    protected void setup() {
        super.setup();
        Object[] args = getArguments();
        Collection<InvestorPriceRecordLabel> stocksToObserve = (Collection<InvestorPriceRecordLabel>) args[0];
        for (InvestorPriceRecordLabel stock : stocksToObserve) {
            observedStocks.put(stock,null);
        }
        this.addBehaviour(new PriceCheckerBehaviour(this, 1000L)); // checks prices every second
    }


    public Set<InvestorPriceRecordLabel> getObservedStocks() {
        return observedStocks.keySet();
    }
    public void updateStoredPrice(InvestorPriceRecordLabel label, Double price){
        observedStocks.put(label, price);
    }
}

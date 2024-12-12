package org.example.logic.stockexchange.utils;

import jade.core.AID;

public class OrderSubmitter {
    private String submitterName;
    private String submitterBroker;
    private AID broker;
    public String getLogName(){
        return submitterName+" (broker: "+submitterBroker+")";
    }

    public OrderSubmitter(String submitterName, String submitterBroker, AID broker){
        this.submitterName = submitterName;
        this.submitterBroker = submitterBroker;
        this.broker = broker;
    }

    public OrderSubmitter(String submitterName, AID broker){
        this(submitterName,broker.getName(),broker);
    }

    public AID getBroker() {
        return broker;
    }

    public String getSubmitterName() {
        return submitterName;
    }
}

package org.example.stockexchange.utils;

public class OrderSubmitter {
    private String submitterName;
    private String submitterBroker;
    public String getLogName(){
        return submitterName+" (broker: "+submitterBroker+")";
    }
}

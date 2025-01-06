package org.example.logic.stockexchange.utils;

import jade.core.AID;

public class OrderSubmitter {
    private final String submitterName;
    private final String submitterBroker;
    private final AID broker;
    private final String brokerOrderId;

    public OrderSubmitter(String submitterName, String submitterBroker, AID broker, String brokerOrderId) {
        this.submitterName = submitterName;
        this.submitterBroker = submitterBroker;
        this.broker = broker;
        this.brokerOrderId = brokerOrderId;
    }

    public OrderSubmitter(String submitterName, AID broker, String brokerOrderId) {
        this(submitterName, broker.getName(), broker, brokerOrderId);
    }

    public String getLogName() {
        return submitterName + " (broker: " + submitterBroker + ")";
    }

    public AID getBroker() {
        return broker;
    }

    public String getSubmitterName() {
        return submitterName;
    }

    public String getBrokerOrderId() {
        return brokerOrderId;
    }
}

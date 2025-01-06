package org.example.commandCreator.broker;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public abstract class BrokerCommand {
    private final Gson gson = new Gson();
    protected List<Object> arguments;
    private String command = "";
    private String exchangeName = "";
    private String traderName = "";
    private String brokerOrderId = "";

    public BrokerCommand() {
        arguments = new ArrayList<>();
    }

    public String getCommand() {
        return command;
    }

    protected void setCommand(String command) {
        this.command = command;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public BrokerCommand setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
        return this;
    }

    public String getTraderName() {
        return traderName;
    }

    public BrokerCommand setTraderName(String traderName) {
        this.traderName = traderName;
        return this;
    }

    public String getBrokerOrderId() {
        return brokerOrderId;
    }

    public BrokerCommand setBrokerOrderId(String brokerOrderId) {
        this.brokerOrderId = brokerOrderId;
        return this;
    }

    public List<Object> getArguments() {
        return arguments;
    }

    public String getJsonCommand() {
        return gson.toJson(this);
    }
}

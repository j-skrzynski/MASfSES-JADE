package org.example.commandCreator.broker;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public abstract class BrokerCommand {

    private String command = "";
    private String exchangeName ="";
    private String traderName = "";
    private String brokerOrderId = "";
    protected List<Object> arguments;

    public BrokerCommand() {
        arguments = new ArrayList<Object>();
    }

    protected BrokerCommand setCommand(String command) {
        this.command = command;
        return this;
    }
    public BrokerCommand setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
        return this;
    }
    public BrokerCommand setTraderName(String traderName) {
        this.traderName = traderName;
        return this;
    }
    public BrokerCommand setBrokerOrderId(String brokerOrderId) {
        this.brokerOrderId = brokerOrderId;
        return this;
    }

    public String getCommand() {
        return command;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public String getTraderName() {
        return traderName;
    }

    public String getBrokerOrderId() {
        return brokerOrderId;
    }

    public List<Object> getArguments() {
        return arguments;
    }

    public String getJsonCommand() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}

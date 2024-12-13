package org.example.datamodels.command;

import java.util.List;

public class Command {
    private final String command;
    private final String stockExchangeName;
    private final List<Object> arguments;
    private final String traderName;
    private final String brokerName;
    private final String exchangeName;

    public String getCommand() {
        return command;
    }

    public String getStockExchangeName() {
        return stockExchangeName;
    }

    public List<Object> getArguments() {
        return arguments;
    }

    public String getTraderName() {
        return traderName;
    }

    public String getBrokerName() {
        return brokerName;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public Command(String command, String stockExchangeName, List<Object> arguments, String traderName, String brokerName, String exchangeName) {
        this.command = command;
        this.stockExchangeName = stockExchangeName;
        this.arguments = arguments;
        this.traderName = traderName;
        this.brokerName = brokerName;
        this.exchangeName = exchangeName;
    }
}
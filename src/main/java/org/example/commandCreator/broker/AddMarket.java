package org.example.commandCreator.broker;

public class AddMarket extends BrokerCommand {
    public AddMarket(String marketName) {
        this.setCommand("ADD_MARKET");
        this.arguments.add(marketName);
    }
}

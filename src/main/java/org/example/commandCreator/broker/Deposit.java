package org.example.commandCreator.broker;

public class Deposit extends BrokerCommand {
    public Deposit(Double amount) {
        this.setCommand("DEPOSIT");
        this.arguments.add(amount);
    }
}

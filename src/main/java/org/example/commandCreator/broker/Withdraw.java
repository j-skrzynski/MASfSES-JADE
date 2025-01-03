package org.example.commandCreator.broker;

public class Withdraw extends BrokerCommand {
    public Withdraw(Double amount) {
        this.setCommand("WITHDRAW");
        this.arguments.add(amount);
    }
}

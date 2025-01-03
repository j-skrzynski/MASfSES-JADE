package org.example.commandCreator.broker;

public class GetBalance extends BrokerCommand{
    public GetBalance() {
        this.setCommand("GET_BALANCE");
    }
}

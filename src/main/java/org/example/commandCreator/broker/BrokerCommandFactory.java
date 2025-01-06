package org.example.commandCreator.broker;

import org.example.datamodels.order.OrderType;

public class BrokerCommandFactory {
    private String exchangeName;
    private String traderName;

    public BrokerCommandFactory(String exchangeName, String traderName) {
        this.exchangeName = exchangeName;
        this.traderName = traderName;
    }

    public BrokerCommand addMarket(String marketName){
        return new AddMarket(marketName).setExchangeName(exchangeName);
    }
    public BrokerCommand deposit(double amount){
        return new Deposit(amount).setTraderName(traderName);
    }
    public BrokerCommand withdraw(double amount){
        return new Withdraw(amount).setExchangeName(exchangeName).setTraderName(traderName);
    }
    public BrokerCommand getBalance(){
        return new GetBalance().setTraderName(traderName);
    }
    public BrokerCommand getPortfolio(){
        return new GetPortfolio().setTraderName(traderName);
    }
    public BrokerCommand register(){
        return new Register().setTraderName(traderName);
    }
    public PlaceOrder marketOrder(String shortName, OrderType type, Double price, Long quantity){
        return (PlaceOrder) PlaceOrder.market_order(shortName, type, price, quantity).setExchangeName(exchangeName).setTraderName(traderName);
    }
    public PlaceOrder limitlessOrder(String shortName, OrderType type, Long quantity){
        return (PlaceOrder) PlaceOrder.limitless_order(shortName, type, quantity).setExchangeName(exchangeName).setTraderName(traderName);
    }
    public PlaceOrder awaitingOrder(String shortName, OrderType type, Double price, Long quantity, Double activationPrice){
        return (PlaceOrder) PlaceOrder.awaiting_market_order(shortName,type,price,quantity,activationPrice).setExchangeName(exchangeName).setTraderName(traderName);
    }
    public PlaceOrder awaitingLimitlessOrder(String shortName, OrderType type, Long quantity, Double activationPrice){
        return (PlaceOrder) PlaceOrder.awaiting_limitless_market_order(shortName,type,quantity,activationPrice).setExchangeName(exchangeName).setTraderName(traderName);
    }
}

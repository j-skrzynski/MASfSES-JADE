package org.example.logic.broker;

import org.example.datamodels.StockSymbol;
import org.example.datamodels.order.OrderType;
import org.example.global.StockDictionary;

public class InvestorOrderRecord {
    /*
     * Tu nie rozrózniamy czy jest awaiting czy nie bo w zasadzier to nas nie interesi tutaj, w środku awaiting jest i
     * tak zwykłe zlecenie które może się uwolnic albo nie
     * */

    private final String orderId;
    private final StockSymbol symbol;
    private final boolean limit;
    private final OrderType action;
    private Long amountOfStockToBeBought;
    private Long amountOfStockToBeSold;
    private Double moneyLocked;

    public InvestorOrderRecord(
            String orderId,
            StockSymbol symbol,
            Long amountOfStockToBeBought,
            Long amountOfStockToBeSold,
            Double moneyLocked,
            boolean limit,
            OrderType action
    ) {
        this.orderId = orderId;
        this.symbol = symbol;
        this.amountOfStockToBeBought = amountOfStockToBeBought;
        this.amountOfStockToBeSold = amountOfStockToBeSold;
        this.moneyLocked = moneyLocked;
        this.limit = limit;
        this.action = action;
    }

    public InvestorOrderRecord(
            InvestorRequest investorRequest,
            String orderId,
            Double moneyToLock
    ) {
        this.orderId = orderId;
        this.symbol = StockDictionary.getStockIdByShortName(investorRequest.getShortName());
        if (investorRequest.getAction() == OrderType.BUY) {
            this.amountOfStockToBeBought = investorRequest.getAmount();
            this.amountOfStockToBeSold = 0L;
        } else {
            this.amountOfStockToBeSold = investorRequest.getAmount();
            this.amountOfStockToBeBought = 0L;
        }
        this.moneyLocked = moneyToLock;
        this.limit = !investorRequest.isLimitless();
        this.action = investorRequest.getAction();
    }

    public String getOrderId() {
        return orderId;
    }

    public Long getAmountOfStockToBeBought() {
        return amountOfStockToBeBought;
    }

    public Long getAmountOfStockToBeSold() {
        return amountOfStockToBeSold;
    }

    public Double getMoneyLocked() {
        return moneyLocked;
    }

    public boolean isLimit() {
        return limit;
    }

    public OrderType getAction() {
        return action;
    }

    public void payedMoney(Double money) {
        this.moneyLocked -= money;
    }

    public void soldStock(Long amount) {
        this.amountOfStockToBeSold -= amount;
    }

    public void boughtStock(Long amount) {
        this.amountOfStockToBeBought += amount;
    }

    public StockSymbol getStockSymbol() {
        return symbol;
    }
}

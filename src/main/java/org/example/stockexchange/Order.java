package org.example.stockexchange;

import org.example.stockexchange.utils.*;

public class Order {

    // SHOULD COVER PKC AND LIMIT
    private StockSymbol symbol;
    private int quantity;
    private Double price;   // null - no limit so pkc; value is the limit
    private ExpirationDate expirationDate;
    private OrderType orderType;
    private OrderSubmitter submitter;

    public Order(StockSymbol symbol, OrderType orderType) {
        this.symbol = symbol;
        this.orderType = orderType;
    }

    public StockSymbol getSymbol() {
        return symbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public Double getPrice() {
        return price;
    }

    public ExpirationDate getExpirationDate() {
        return expirationDate;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public OrderSubmitter getSubmitter() {
        return submitter;
    }

    public void reduceQuantity(int tradedQuantity) {
        quantity -= tradedQuantity;
    }

    public boolean hasPriceLimit(){
        return true;
    }

    public boolean isExpired(CurrentDate date){

    }
}


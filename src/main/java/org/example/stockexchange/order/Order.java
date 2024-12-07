package org.example.stockexchange.order;

import org.example.stockexchange.utils.*;

public class Order {

    // SHOULD COVER PKC AND LIMIT
    private StockSymbol symbol;
    private int quantity;
    private Double price;   // null - no limit so pkc; value is the limit
    private ExchangeDate expirationDate;
    private OrderType orderType;
    private OrderSubmitter submitter;
    private ExchangeOrderingID seqId = null;

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

    public ExchangeDate getExpirationDate() {
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

    public boolean isExpired(ExchangeDate date){
        return expirationDate.isBeforeOrEqual(date);
    }

    public void setSeqId(ExchangeOrderingID seqId) {
        this.seqId = seqId;
    }

    public ExchangeOrderingID getSeqId() {
        return seqId;
    }
}


package org.example.logic.stockexchange.order;

import org.example.logic.stockexchange.utils.*;
import org.example.logic.stockexchange.utils.*;

public class Order implements PlacableDisposition{

    // SHOULD COVER PKC AND LIMIT
    private StockSymbol symbol;
    private OrderType orderType;
    private Long quantity;
    private Double price;   // null - no limit so pkc; value is the limit
    private ExchangeDate expirationDate;
    private OrderSubmitter submitter;
    private ExchangeOrderingID seqId = null;

    public Order(StockSymbol symbol, OrderType orderType, ExchangeDate expirationDate, Double price, Long quantity,OrderSubmitter submitter) {
        this.symbol = symbol;
        this.orderType = orderType;
        this.expirationDate = expirationDate;
        this.price = price;
        this.quantity = quantity;
        this.submitter = submitter;
    }

    public StockSymbol getSymbol() {
        return symbol;
    }

    public Long getQuantity() {
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

    public void reduceQuantity(Long tradedQuantity) {
        quantity -= tradedQuantity;
    }

    public boolean hasPriceLimit(){
        return true;
    }

    public boolean isExpired(ExchangeDate date){
        return getExpirationDate().isBeforeOrEqual(date);
    }

    public void setSeqId(ExchangeOrderingID seqId) {
        this.seqId = seqId;
    }

    public ExchangeOrderingID getSeqId() {
        return seqId;
    }

    @Override
    public boolean isAwaiting() {
        return false;
    }
}


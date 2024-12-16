package org.example.logic.broker;

import org.example.datamodels.order.AwaitingOrder;
import org.example.datamodels.order.OrderType;

public class InvestorRequest {
    private Long amount;
    private String shortName;
    private OrderType action;
    private Double price;
    private boolean limitless;



    public InvestorRequest(Long amount, String shortName, OrderType action, Double price, boolean limitless, String stockExchangeName) {
        this.amount = amount;
        this.shortName = shortName;
        this.action = action;
        this.price = price;
        this.limitless = limitless;
        this.stockExchangeName = stockExchangeName;
    }

    public InvestorRequest(AwaitingOrder ao,String stockExchangeName) {
        this(
                ao.getOrder().getQuantity(),
                ao.getOrder().getSymbol().getShortName(),
                ao.getOrder().getOrderType(),
                ao.getOrder().getPrice(),
                ao.getOrder().hasPriceLimit()==false,
                stockExchangeName
        );
    }


    public String getStockExchangeName() {
        return stockExchangeName;
    }

    private String stockExchangeName;

    public String getShortName() {
        return shortName;
    }

    public OrderType getAction() {
        return action;
    }

    public Long getAmount() {
        return amount;
    }

    public boolean isLimitless(){
        return limitless;
    }

    public Double getPrice() {
        return price;
    }
}

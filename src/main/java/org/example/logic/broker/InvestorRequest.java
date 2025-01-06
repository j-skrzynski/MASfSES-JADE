package org.example.logic.broker;

import org.example.datamodels.order.AwaitingOrder;
import org.example.datamodels.order.OrderType;

public class InvestorRequest {
    private final Long amount;
    private final String shortName;
    private final OrderType action;
    private final Double price;
    private final boolean limitless;
    private final String stockExchangeName;

    public InvestorRequest(
            Long amount,
            String shortName,
            OrderType action,
            Double price,
            boolean limitless,
            String stockExchangeName
    ) {
        this.amount = amount;
        this.shortName = shortName;
        this.action = action;
        this.price = price;
        this.limitless = limitless;
        this.stockExchangeName = stockExchangeName;
    }


    public InvestorRequest(AwaitingOrder ao, String stockExchangeName) {
        this(
                ao.order().getQuantity(),
                ao.order().getSymbol().getShortName(),
                ao.order().getOrderType(),
                ao.order().getPrice(),
                !ao.order().hasPriceLimit(),
                stockExchangeName
        );
    }

    public String getStockExchangeName() {
        return stockExchangeName;
    }

    public String getShortName() {
        return shortName;
    }

    public OrderType getAction() {
        return action;
    }

    public Long getAmount() {
        return amount;
    }

    public boolean isLimitless() {
        return limitless;
    }

    public Double getPrice() {
        return price;
    }
}

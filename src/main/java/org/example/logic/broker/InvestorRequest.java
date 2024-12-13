package org.example.logic.broker;

public class InvestorRequest {
    private Long amount;
    private String shortName;
    private OrderAction action;
    private Double price;
    private boolean limitless;

    public InvestorRequest(Long amount, String shortName, OrderAction action, Double price, boolean limitless, String stockExchangeName) {
        this.amount = amount;
        this.shortName = shortName;
        this.action = action;
        this.price = price;
        this.limitless = limitless;
        this.stockExchangeName = stockExchangeName;
    }


    public String getStockExchangeName() {
        return stockExchangeName;
    }

    private String stockExchangeName;

    public String getShortName() {
        return shortName;
    }

    public OrderAction getAction() {
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

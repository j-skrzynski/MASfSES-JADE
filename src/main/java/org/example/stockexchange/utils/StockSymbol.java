package org.example.stockexchange.utils;

public class StockSymbol {

    private String longName;
    private String shortName;
    private Double IPOPrice;
    private Long shares;
    public StockSymbol(String longName, String shortName, Double IPOPrice, Long shares) {
        this.longName = longName;
        this.shortName = shortName;
        this.IPOPrice = IPOPrice;
        this.shares = shares;
    }
    public Double getIPOPrice(){
        return IPOPrice;
    }

    public String getLongName() {
        return longName;
    }

    public String getShortName() {
        return shortName;
    }

    public Long getShares() {
        return shares;
    }
}

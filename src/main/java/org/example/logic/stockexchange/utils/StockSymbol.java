package org.example.logic.stockexchange.utils;

public class StockSymbol {

    private final String longName;
    private final String shortName;
    private final Double IPOPrice;
    private Long shares; // this value might be changed in the future as we may need to perform so called "split operations or merge on shares
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

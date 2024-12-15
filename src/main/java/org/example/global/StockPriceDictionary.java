package org.example.global;

import java.util.HashMap;

public class StockPriceDictionary {

    private static HashMap<String,HashMap<String,Double>> stockPrice; // exchange name / shock short / value

    public static Double getPrice(String shortName, String stockExchangeName) {
        if (!stockPrice.containsKey(stockExchangeName)) {
            throw new RuntimeException("Stock exchange " + stockExchangeName + " not found");
        }
        HashMap<String,Double> pricesInMarket = stockPrice.get(stockExchangeName);

        if(!pricesInMarket.containsKey(shortName)) {
            return StockDictionary.getStockIdByShortName(shortName).getIPOPrice();
        }
        return pricesInMarket.get(shortName);
    }


}

package org.example.global;

import java.util.HashMap;

public class StockPriceDictionary {

    private static final HashMap<String,HashMap<String,Double>> stockPrice = new HashMap<>(); // exchange name / shock short / value

    public static void addStockMarket(String marketName){
        stockPrice.put(marketName, new HashMap<>());
    }

    public static void addPrice(String shortName, String stockExchangeName, Double price){
        stockPrice.get(stockExchangeName).put(shortName, price);
    }
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

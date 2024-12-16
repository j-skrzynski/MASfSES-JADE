package org.example.global;

import org.example.datamodels.StockSymbol;

import java.util.HashMap;

public class StockDictionary {
    private static HashMap<String, StockSymbol> stockSymbols = new HashMap<>();
    public static void registerStockSymbol(String shortName, String longName, Double IPOPrice, Long shares){
        registerStockSymbol(new StockSymbol(longName,shortName,IPOPrice,shares));
    }
    public static void registerStockSymbol(StockSymbol symbol){
        stockSymbols.put(symbol.getShortName(), symbol);
    }
    public static StockSymbol getStockIdByShortName(String shortName){
        if(!stockSymbols.containsKey(shortName)){
            throw new IllegalArgumentException("Stock symbol does not exist");
        }
        return stockSymbols.get(shortName);
    }
}

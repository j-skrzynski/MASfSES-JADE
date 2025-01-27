package org.example.logic.stockexchange.utils;

import org.example.datamodels.StockSymbol;
import org.example.global.StockPriceDictionary;
import org.glassfish.pfl.basic.contain.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

public class PriceTracker {
    private static final Logger logger = Logger.getLogger(PriceTracker.class.getName());

    static {
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.INFO); // Log messages at INFO level or higher
        logger.addHandler(consoleHandler);
        try {
            FileHandler fileHandler = new FileHandler("transactions.log", true); // Append to the log file
            fileHandler.setFormatter(new SimpleFormatter()); // Add a simple text formatter
            logger.addHandler(fileHandler);
        } catch (Exception e) {
            logger.severe("Failed to set up file handler: " + e.getMessage());
        }

    }

    private final StockSymbol symbol;
    private final List<Pair<Double, Long>> history;
    private final String exchangeName;
    private Double lastPrice;

    public PriceTracker(StockSymbol symbol, String exchangeName) {
        this.symbol = symbol;
        this.history = new ArrayList<>();
        this.exchangeName = exchangeName;
    }

    private void logTransaction(Double price, Long quantity, String buyer, String seller, Long sessionNumber, Long seconds){
        logger.info("["+exchangeName+"]["+symbol.getShortName()+"] "+seller+" sold "+Long.toString(quantity)+" to "+buyer+" unit_price "+price.toString() + " session "+Long.toString(sessionNumber) +"@"+Long.toString(seconds));
    }

    public void submitData(Double price, Long quantity,OrderSubmitter buyer, OrderSubmitter seller, Long sessionNumber, Long seconds){
        history.add(new Pair<>(price,quantity));
        lastPrice = price;
        logTransaction(price,quantity,buyer.getLogName(),seller.getLogName(),sessionNumber,seconds);
        StockPriceDictionary.addPrice(symbol.getShortName(), exchangeName,price);
    }

    public void submitArtificialData(Double price){
        history.add(new Pair<>(price,-1L));
        lastPrice = price;
        StockPriceDictionary.addPrice(symbol.getShortName(), exchangeName,price);
    }

    public Double getLastPrice(){
        return lastPrice;
    }

    public List<Pair<Double, Long>> getHistory() {
        return history;
    }
}

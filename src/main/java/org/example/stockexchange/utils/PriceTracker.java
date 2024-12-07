package org.example.stockexchange.utils;

import org.glassfish.pfl.basic.contain.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

public class PriceTracker {
    private static final Logger logger = Logger.getLogger(PriceTracker.class.getName());
    static{
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


    private StockSymbol symbol;
    private List<Pair<Double,Integer>> history;
    private Double lastPrice;
    private String exchangeName;

    public PriceTracker(StockSymbol symbol,String exchangeName) {
        this.symbol = symbol;
        this.history = new ArrayList<Pair<Double,Integer>>();
        this.exchangeName = exchangeName;
    }

    private void logTransaction(Double price, int quantity, String buyer, String seller){
        logger.info("["+exchangeName+"]["+symbol.getShortName()+"] "+buyer+" sold "+Integer.toString(quantity)+" to "+buyer+" unit_price "+price.toString() );
    }

    public void submitData(Double price, int quantity,OrderSubmitter buyer, OrderSubmitter seller){
        history.add(new Pair<>(price,quantity));
        lastPrice = price;
        logTransaction(price,quantity,buyer.getLogName(),seller.getLogName());
    }
    public Double getLastPrice(){
        return lastPrice;
    }
}

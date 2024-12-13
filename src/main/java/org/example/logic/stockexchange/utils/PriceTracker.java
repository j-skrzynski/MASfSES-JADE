package org.example.logic.stockexchange.utils;

import org.example.datamodels.StockSymbol;
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


    private final StockSymbol symbol;
    private final List<Pair<Double,Long>> history;
    private Double lastPrice;
    private final String exchangeName;

    public PriceTracker(StockSymbol symbol,String exchangeName) {
        this.symbol = symbol;
        this.history = new ArrayList<Pair<Double,Long>>();
        this.exchangeName = exchangeName;
    }

    private void logTransaction(Double price, Long quantity, String buyer, String seller){
        logger.info("["+exchangeName+"]["+symbol.getShortName()+"] "+seller+" sold "+Long.toString(quantity)+" to "+buyer+" unit_price "+price.toString() );
    }

    public void submitData(Double price, Long quantity,OrderSubmitter buyer, OrderSubmitter seller){
        history.add(new Pair<>(price,quantity));
        lastPrice = price;
        logTransaction(price,quantity,buyer.getLogName(),seller.getLogName());
    }
    public Double getLastPrice(){
        return lastPrice;
    }
}

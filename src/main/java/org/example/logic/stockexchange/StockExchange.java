package org.example.logic.stockexchange;

import jade.core.AID;
import org.example.datamodels.StockSymbol;
import org.example.datamodels.order.OrderType;
import org.example.global.StockDictionary;
import org.example.global.StockPriceDictionary;
import org.example.logic.stockexchange.order.PlaceableDisposition;
import org.example.logic.stockexchange.order.marketorder.ExchangeOrder;
import org.example.logic.stockexchange.settlements.TransactionSettlement;
import org.example.logic.stockexchange.utils.ArtificialBaseline;
import org.example.logic.stockexchange.utils.EnvRecord;
import org.example.logic.stockexchange.utils.ExchangeDate;
import org.example.logic.stockexchange.utils.OrderSubmitter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StockExchange {
    private final String name;
    private final Map<StockSymbol, OrderSheet> orderSheets;
    private final Long millisecondsPerSession;
    private final Long sessionsPerYear;
    private final ArtificialBaseline baseline;
    private ExchangeDate currentSessionStart;
    private Long millisecondsSinceStart;


    public StockExchange(
            String name,
            ExchangeDate lastSessionClosingDate,
            Long millisecondsPerSession,
            Long sessionsPerYear
    ) {
        this.name = name;
        this.orderSheets = new HashMap<>();
        this.currentSessionStart = lastSessionClosingDate;
        this.millisecondsPerSession = millisecondsPerSession;
        this.sessionsPerYear = sessionsPerYear;
        this.millisecondsSinceStart = 0L;
        StockPriceDictionary.addStockMarket(name);
        this.baseline = new ArtificialBaseline();
    }

    public StockExchange(String name, Long millisecondsPerSession, Long sessionsPerYear) {
        this(name, new ExchangeDate(), millisecondsPerSession, sessionsPerYear);
    }

    public ArtificialBaseline getBaseline() {
        return baseline;
    }

    public Set<StockSymbol> getAllStocksInExchange() {
        return orderSheets.keySet();
    }

    public StockSymbol getSymbolByShortName(String shortName) {
        for (StockSymbol symbol : orderSheets.keySet()) {
            if (symbol.getShortName().equals(shortName)) {
                return symbol;
            }
        }
        throw new IllegalArgumentException("No such symbol: " + shortName);
    }

    /**
     * Adds a new stock symbol to the exchange.
     */
    public void addStock(StockSymbol symbol) {
        if (!orderSheets.containsKey(symbol)) {
            orderSheets.put(symbol, new OrderSheet(symbol, name));
            StockDictionary.registerStockSymbol(symbol);
            this.loadArtificialDataForSheet(orderSheets.get(symbol));
        } else {
            throw new IllegalArgumentException("Stock already exists in the exchange.");
        }
    }

    /**
     * Places an order for a given stock symbol.
     */
    public void placeOrder(StockSymbol symbol, PlaceableDisposition order) {
        OrderSheet orderSheet = orderSheets.get(symbol);
        if (orderSheet == null) {
            throw new IllegalArgumentException("Stock symbol does not exist in the exchange.");
        }
        orderSheet.placeDisposition(order);
    }

    /**
     * Fetches the next available transaction settlement for a specific stock.
     */
    public TransactionSettlement getNextSettlement(StockSymbol symbol) {
        OrderSheet orderSheet = orderSheets.get(symbol);
        if (orderSheet != null && orderSheet.isTransactionSettlementAvailable()) {
            return orderSheet.getNextSettlement();
        }
        return null;
    }

    /**
     * Fetches and removes the next available transaction settlement for a specific stock.
     */
    public TransactionSettlement popNextSettlement(StockSymbol symbol) {
        OrderSheet orderSheet = orderSheets.get(symbol);
        if (orderSheet != null) {
            return orderSheet.popNextSettlement();
        }
        return null;
    }

    /**
     * Fetches and removes the next available transaction cancellation for a specific stock.
     */
    public OrderSubmitter popNextCancellationNotification(StockSymbol symbol) {
        OrderSheet orderSheet = orderSheets.get(symbol);
        if (orderSheet != null) {
            return orderSheet.popNextCancellation();
        }
        return null;
    }

    /**
     * Performs expiration updates for all order sheets in the exchange.
     */
    public void expirationUpdate() {
        for (OrderSheet orderSheet : orderSheets.values()) {
            orderSheet.expirationUpdate(currentSessionStart);
        }
    }

    /**
     * Gets the name of the exchange.
     */
    public String getName() {
        return name;
    }

    /**
     * Checks if the exchange contains a specific stock symbol.
     */
    public boolean containsStock(StockSymbol symbol) {
        return orderSheets.containsKey(symbol);
    }

    public void advanceExchangeDateBySession(){
        this.baseline.advance();
        this.currentSessionStart = currentSessionStart.getNexSessionDate();
        this.millisecondsSinceStart = 0L;
        this.expirationUpdate();
        this.loadArtificialData();
        for (OrderSheet orderSheet : orderSheets.values()) {
            orderSheet.setSessionNumber(currentSessionStart.getSessionId());
            orderSheet.setSeconds(currentSessionStart.getMilliseconds());
        }

        System.out.println("New session: "+currentSessionStart.getNexSessionDate().getSessionId().toString());
    }

    public void loadArtificialData() {
        for (OrderSheet sheet : orderSheets.values()) {
            this.loadArtificialDataForSheet(sheet);
        }
    }

    public void loadArtificialDataForSheet(OrderSheet sheet){
        boolean flag = true;
        String shortName = sheet.getSymbol().getShortName();
        EnvRecord rec = this.baseline.getNextEnvRec(shortName);
        if(rec != null) {
            if (flag){
                sheet.placeDisposition(new ExchangeOrder(sheet.getSymbol(), OrderType.SELL, currentSessionStart.getNexSessionDate(), rec.price()*1.005, rec.quantity(), new OrderSubmitter("Env", new AID("imaginaryBroker", false), "")));
                sheet.placeDisposition(new ExchangeOrder(sheet.getSymbol(), OrderType.BUY, currentSessionStart.getNexSessionDate(), rec.price()*0.995, rec.quantity(), new OrderSubmitter("Env", new AID("imaginaryBroker", false), "")));
                sheet.getPriceTracker().submitArtificialData(rec.price());
                System.out.println("env: sell "+rec.price()*1.005 + " buy "+ rec.price()*0.995);
            }
            else
                sheet.placeDisposition(new ExchangeOrder(sheet.getSymbol(), OrderType.SELL, currentSessionStart.getNexSessionDate(), rec.price(), rec.quantity(), new OrderSubmitter("Env", new AID("imaginaryBroker", false), "")));
        }
    }

    /**
     * Retrieves the top 5 buy offers for a specific stock symbol.
     */
    public List<ExchangeOrder> getTopBuyOffers(StockSymbol symbol) {
        OrderSheet orderSheet = getOrderSheet(symbol);
        return orderSheet.getTopBuyOffers();
    }

    /**
     * Retrieves the top 5 sell offers for a specific stock symbol.
     */
    public List<ExchangeOrder> getTopSellOffers(StockSymbol symbol) {
        OrderSheet orderSheet = getOrderSheet(symbol);
        return orderSheet.getTopSellOffers();
    }

    /**
     * Retrieves the order sheet for the given stock symbol, or throws an exception if it doesn't exist.
     */
    private OrderSheet getOrderSheet(StockSymbol symbol) {
        OrderSheet orderSheet = orderSheets.get(symbol);
        if (orderSheet == null) {
            throw new IllegalArgumentException("Stock symbol does not exist in the exchange.");
        }
        return orderSheet;
    }

    public void addMillisecondsSinceStart(Long millisecondsSinceStart) {
        this.millisecondsSinceStart += millisecondsSinceStart;
        for (OrderSheet orderSheet : orderSheets.values()) {
            orderSheet.setSeconds(this.millisecondsSinceStart);
        }
    }

    public Long getMillisecondsSinceStart() {
        return millisecondsSinceStart;
    }

    public ExchangeDate getCurrentSessionStart() {
        return new ExchangeDate(currentSessionStart);
    }

    public Long getSessionsTillYearEnd() {
        return sessionsPerYear - (currentSessionStart.getSessionId() % sessionsPerYear);
    }

    public Long getMillisecondsPerSession() {
        return millisecondsPerSession;
    }

    public Long getSessionsPerYear() {
        return sessionsPerYear;
    }
}

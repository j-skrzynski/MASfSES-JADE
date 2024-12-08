package org.example.stockexchange;

import org.example.stockexchange.order.*;
import org.example.stockexchange.settlements.BuyerSettlement;
import org.example.stockexchange.settlements.SellerSettlement;
import org.example.stockexchange.settlements.SettlementCreator;
import org.example.stockexchange.settlements.TransactionSettlement;
import org.example.stockexchange.utils.*;
import org.glassfish.pfl.basic.contain.Pair;

import java.util.*;
import java.util.logging.*;

public class OrderSheet {

    private static final Logger logger = Logger.getLogger(OrderSheet.class.getName());
    static{
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.INFO); // Log messages at INFO level or higher
        logger.addHandler(consoleHandler);
        try {
            FileHandler fileHandler = new FileHandler("orders.log", true); // Append to the log file
            fileHandler.setFormatter(new SimpleFormatter()); // Add a simple text formatter
            logger.addHandler(fileHandler);
        } catch (Exception e) {
            logger.severe("Failed to set up file handler: " + e.getMessage());
        }

    }
    private Queue<Order> buyOrders;
    private Queue<Order> sellOrders;

    private Queue<Order> noLimitSell;
    private Queue<Order> noLimitBuy;

    private Queue<AwaitingOrder> awaitingActivationBuy;
    private Queue<AwaitingOrder> awaitingActivationSell;

    private PriceTracker priceTracker;
    private StockSymbol symbol;

    private Queue<TransactionSettlement> settlementsToSend;

    private String exchangeName;

    private ExchangeOrderingID lastId;

    public OrderSheet(StockSymbol symbol, String exchangeName) {
        buyOrders = new PriorityQueue<>(new OrderComparatorDescending()); // da najwięcej --- da najmniej  > descending
        sellOrders = new PriorityQueue<>(new OrderComparatorAscending());// najtańsze --- najdroższe  > ascending

        noLimitSell = new LinkedList<>();
        noLimitBuy = new LinkedList<>();

        awaitingActivationBuy = new PriorityQueue<>(new AwaitingOrderComparatorAscending());
        awaitingActivationSell = new PriorityQueue<>(new AwaitingOrderComparatorDescending());

        priceTracker = new PriceTracker(symbol,exchangeName);
        this.symbol = symbol;

        settlementsToSend = new LinkedList<>();
        lastId = ExchangeOrderingID.getZero();
    }

    private void saveTransaction(BuyerSettlement buyerSettlement, SellerSettlement sellerSettlement) {
        settlementsToSend.add(sellerSettlement);
        settlementsToSend.add(buyerSettlement);
        if(buyerSettlement.getQuantity() != sellerSettlement.getQuantity() || !buyerSettlement.getUnitPrice().equals(sellerSettlement.getUnitPrice())){
            throw new RuntimeException("Buyer/Seller settlement does not match");
        }
        this.priceTracker.submitData(buyerSettlement.getUnitPrice(), buyerSettlement.getQuantity(),buyerSettlement.getAddressee(),sellerSettlement.getAddressee());
    }

    private Double getReferencePrice(){
        if (priceTracker.getLastPrice() != null){
            return priceTracker.getLastPrice();
        }
        else{
            return symbol.getIPOPrice();
        }
    }

    private void placeSell(Order o){
        lastId = lastId.next();
        o.setSeqId(lastId);
        Double lastPriceFixing = null;
        logger.info("Incomming SELL "+o.toString());
        //w pierwszej kolejności wykonujemy zlecenia PKC
        //Problemem jest że nie wiem jak obliczyć cenę, przyjmę więc max cenę oferty i maksymalne zlecenie
        while(!noLimitBuy.isEmpty() && o.getQuantity()>0){
            Order topBuyOrder = noLimitBuy.peek();
            Double transactionUnitPrice = o.getPrice();
            if(!o.hasPriceLimit()){
                transactionUnitPrice = getReferencePrice();//Math.min(transactionUnitPrice, buyOrders.peek().getPrice());
            }
            Long tradedQuantity = Math.min(o.getQuantity(), topBuyOrder.getQuantity());
            logger.info("Incomming SELL "+o.toString()+" matched with "+topBuyOrder.toString()+" in against NOLimit phase. Sold " + tradedQuantity + "@" + transactionUnitPrice);
            o.reduceQuantity(tradedQuantity);
            topBuyOrder.reduceQuantity(tradedQuantity);
            if (topBuyOrder.getQuantity() == 0) {
                noLimitBuy.poll();
            }
            Pair<BuyerSettlement, SellerSettlement> settlements = SettlementCreator.createSettlement(topBuyOrder, o, tradedQuantity, transactionUnitPrice);
            // Zapisz transakcje
            this.saveTransaction(settlements.first(), settlements.second());
            lastPriceFixing=transactionUnitPrice;

        }
        logger.info("Incomming SELL "+o.toString()+" passed NOLimit phase");
        //Jeśli są jakiekolwiek buy pasujące
        while(!buyOrders.isEmpty() && o.getQuantity()>0 && buyOrders.peek().getPrice()>=o.getPrice()){
            // Dopuki są chętni do zakupu, mamy co sprzedawać i kupujący oferują więcej/= niż my chcemy dostać
            //wykonać za co najmniej naszą cenę
            Order topBuyOrder = buyOrders.peek();
            Long tradedQuantity = Math.min(o.getQuantity(), topBuyOrder.getQuantity());
            Double unitPrice = topBuyOrder.getPrice(); // być może min z tej ceny i ostatniej rynkowej jakny sam pkc był
            logger.info("Incomming SELL "+o.toString()+" matched with "+topBuyOrder.toString()+" in against Limit phase. Sold " + tradedQuantity + "@" + unitPrice);
            // Wykonanie transakcji
            o.reduceQuantity(tradedQuantity);
            topBuyOrder.reduceQuantity(tradedQuantity);



            Pair<BuyerSettlement, SellerSettlement> settlements = SettlementCreator.createSettlement(topBuyOrder, o, tradedQuantity, unitPrice);

            // Zapisz transakcje
            this.saveTransaction(settlements.first(), settlements.second());

            // Usuń w pełni zrealizowane zlecenie kupna
            if (topBuyOrder.getQuantity() == 0) {
                buyOrders.poll();
            }
            lastPriceFixing=unitPrice;
        }
        logger.info("Incomming SELL "+o.toString()+" passed Limit phase");
        if(o.getQuantity()>0){
            logger.info("Incomming SELL "+o.toString()+" remains not fully executed - saving phase");
            if(!o.hasPriceLimit()){
                noLimitSell.add(o);
            }else {
                sellOrders.add(o);
            }
        }
        else{
            logger.info("Incomming SELL "+o.toString()+" was fully executed");
        }
        updateAwaitingOrders(lastPriceFixing);
    }

    private void placeBuy(Order o) {
        lastId = lastId.next();
        o.setSeqId(lastId);
        logger.info("Incomming BUY "+o.toString());
        Double lastPriceFixing = null;
        while(!noLimitSell.isEmpty() && o.getQuantity()>0){
            Order topSellOrder = noLimitSell.peek();
            Double transactionUnitPrice = o.getPrice();
            if(!o.hasPriceLimit()){
                transactionUnitPrice = getReferencePrice();//transactionUnitPrice = Math.min(transactionUnitPrice, buyOrders.peek().getPrice());
            }
            Long tradedQuantity = Math.min(o.getQuantity(), topSellOrder.getQuantity());
            logger.info("Incomming BUY "+o.toString()+" matched with "+topSellOrder.toString()+" in against NOLimit phase. Sold " + tradedQuantity + "@" + transactionUnitPrice);

            o.reduceQuantity(tradedQuantity);
            topSellOrder.reduceQuantity(tradedQuantity);
            if (topSellOrder.getQuantity() == 0) {
                noLimitSell.poll();
            }
            Pair<BuyerSettlement, SellerSettlement> settlements = SettlementCreator.createSettlement(o,topSellOrder,tradedQuantity, transactionUnitPrice);
            // Zapisz transakcje
            this.saveTransaction(settlements.first(), settlements.second());
            lastPriceFixing = transactionUnitPrice;
        }
        logger.info("Incomming BUY "+o.toString()+" passed NOLimit phase");
        while (!sellOrders.isEmpty() && o.getQuantity() > 0 && sellOrders.peek().getPrice() <= o.getPrice()) {
            Order topSellOrder = sellOrders.peek();
            Long tradedQuantity = Math.min(o.getQuantity(), topSellOrder.getQuantity());
            Double unitPrice = topSellOrder.getPrice();
            logger.info("Incomming BUY "+o.toString()+" matched with "+topSellOrder.toString()+" in against Limit phase. Sold " + tradedQuantity + "@" + unitPrice);

            o.reduceQuantity(tradedQuantity);
            topSellOrder.reduceQuantity(tradedQuantity);



            Pair<BuyerSettlement, SellerSettlement> settlements = SettlementCreator.createSettlement(o, topSellOrder, tradedQuantity, unitPrice);

            // Zapisz transakcje
            this.saveTransaction(settlements.first(), settlements.second());

            // Usuń w pełni zrealizowane zlecenie sprzedaży
            if (topSellOrder.getQuantity() == 0) {
                sellOrders.poll();
            }
            lastPriceFixing = unitPrice;
        }
        logger.info("Incomming BUY "+o.toString()+" passed Limit phase");
        if (o.getQuantity() > 0) {
            logger.info("Incomming BUY "+o.toString()+" remains not fully executed - saving phase");
            if(!o.hasPriceLimit()){
                noLimitBuy.add(o);
            }else {
                buyOrders.add(o);
            }
        }
        else{
            logger.info("Incomming BUY "+o.toString()+" was fully executed");
        }
        updateAwaitingOrders(lastPriceFixing);
    }

    private void placeOrder(Order o){
        if (o.getOrderType() == OrderType.BUY) {
            placeBuy(o);
        } else if (o.getOrderType() == OrderType.SELL) {
            placeSell(o);
        }
    }

    private void placeAwaitingOrder(AwaitingOrder o) {
        if (o.getActivatedOrder().getOrderType() == OrderType.BUY) {
            awaitingActivationBuy.add(o);
        }
        else if (o.getActivatedOrder().getOrderType() == OrderType.SELL) {
            awaitingActivationSell.add(o);
        }
    }


    private void updateAwaitingOrders(Double lastPrice){
        while(!awaitingActivationBuy.isEmpty() && awaitingActivationBuy.peek().getActivationPrice()>=lastPrice){
            placeBuy(awaitingActivationBuy.poll().getActivatedOrder());
        }
        while(!awaitingActivationSell.isEmpty() && awaitingActivationSell.peek().getActivationPrice()<=lastPrice){
            placeSell(awaitingActivationSell.poll().getActivatedOrder());
        }
    }

    private void expire(ExchangeDate date){
        buyOrders.removeIf(order -> order.isExpired(date));
        sellOrders.removeIf(order -> order.isExpired(date));
        noLimitBuy.removeIf(order -> order.isExpired(date));
        noLimitSell.removeIf(order -> order.isExpired(date));
        awaitingActivationBuy.removeIf(awaitingOrder -> awaitingOrder.getActivatedOrder().isExpired(date));
        awaitingActivationSell.removeIf(awaitingOrder -> awaitingOrder.getActivatedOrder().isExpired(date));
    }

    public void placeDisposition(PlacableDisposition disposition){
        if (!disposition.isAwaiting()){
            placeOrder((Order) disposition);
        }
        else{
            placeAwaitingOrder((AwaitingOrder) disposition);
        }
    }

    public TransactionSettlement getNextSettlement(){
        return settlementsToSend.peek();
    }
    public TransactionSettlement popNextSettlement(){
        return settlementsToSend.poll();
    }
    public boolean isTransactionSettlementAvailable(){
        return !settlementsToSend.isEmpty();
    }

    public void expirationUpdate(ExchangeDate date){
        expire(date);
    }

    /**
     * Retrieves the top 5 buy offers in ascending order of priority (best offers).
     * Does not remove these offers from the queue.
     */
    public List<Order> getTopBuyOffers() {
        return getTopOrders(buyOrders, 5);
    }

    /**
     * Retrieves the top 5 sell offers in ascending order of priority (best offers).
     * Does not remove these offers from the queue.
     */
    public List<Order> getTopSellOffers() {
        return getTopOrders(sellOrders, 5);
    }

    /**
     * Retrieves up to 'n' best orders from the provided queue without modifying it.
     */
    private List<Order> getTopOrders(Queue<Order> orders, int n) {
        List<Order> topOrders = new ArrayList<>();
        int count = 0;

        for (Order order : orders) {
            topOrders.add(order);
            if (++count >= n) break; // Limit to 'n' orders
        }
        return topOrders;
    }
}

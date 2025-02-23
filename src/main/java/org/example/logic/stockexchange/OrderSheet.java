package org.example.logic.stockexchange;

import org.example.datamodels.StockSymbol;
import org.example.datamodels.order.OrderType;
import org.example.logic.stockexchange.order.PlaceableDisposition;
import org.example.logic.stockexchange.order.awaitingorder.AwaitingExchangeOrder;
import org.example.logic.stockexchange.order.awaitingorder.AwaitingExchangeOrderComparatorAscending;
import org.example.logic.stockexchange.order.awaitingorder.AwaitingExchangeOrderComparatorDescending;
import org.example.logic.stockexchange.order.marketorder.ExchangeOrder;
import org.example.logic.stockexchange.order.marketorder.OrderComparatorAscending;
import org.example.logic.stockexchange.order.marketorder.OrderComparatorDescending;
import org.example.logic.stockexchange.settlements.BuyerSettlement;
import org.example.logic.stockexchange.settlements.SellerSettlement;
import org.example.logic.stockexchange.settlements.SettlementCreator;
import org.example.logic.stockexchange.settlements.TransactionSettlement;
import org.example.util.ListenableQueue;
import org.example.util.QueueEventListener;
import org.example.visualization.AgentWindow;
import org.example.visualization.AgentWindowManager;
import org.example.visualization.viewmodels.StockExchangeViewModel;
import org.example.logic.stockexchange.utils.ExchangeDate;
import org.example.logic.stockexchange.utils.ExchangeOrderingID;
import org.example.logic.stockexchange.utils.OrderSubmitter;
import org.example.logic.stockexchange.utils.PriceTracker;
import org.glassfish.pfl.basic.contain.Pair;

import java.util.*;
import java.util.logging.*;

public class OrderSheet {
    private static final Logger logger = Logger.getLogger(OrderSheet.class.getName());

    static{
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.OFF); // Log messages at INFO level or higher
        logger.addHandler(consoleHandler);
        logger.setUseParentHandlers(false);

        try {
            FileHandler fileHandler = new FileHandler("orders.log", true); // Append to the log file
            fileHandler.setFormatter(new SimpleFormatter()); // Add a simple text formatter
            logger.addHandler(fileHandler);
        } catch (Exception e) {
            logger.severe("Failed to set up file handler: " + e.getMessage());
        }

    }

    private final ListenableQueue<ExchangeOrder> buyOrders;
    private final ListenableQueue<ExchangeOrder> sellOrders;
    private final ListenableQueue<ExchangeOrder> noLimitSell;
    private final ListenableQueue<ExchangeOrder> noLimitBuy;
    private final ListenableQueue<AwaitingExchangeOrder> awaitingActivationBuy;
    private final ListenableQueue<AwaitingExchangeOrder> awaitingActivationSell;
    private final ListenableQueue<TransactionSettlement> settlementsToSend;
    private final ListenableQueue<OrderSubmitter> canceledOrders;

    private final PriceTracker priceTracker;
    private final StockSymbol symbol;

    private final AgentWindow exchangeWindow;

    private ExchangeOrderingID lastId;

    private Long sessionNumber;
    private Long seconds;

    private record DefaultVisualizationListener<E>(OrderSheet orderSheet) implements QueueEventListener<E> {
        @Override
        public void onAdd(E element) {
            orderSheet.updateExchangeWindow();
        }

        @Override
        public void onRemove(E element) {
            orderSheet.updateExchangeWindow();
        }
    }

    public OrderSheet(StockSymbol symbol, String exchangeName) {
        buyOrders = new ListenableQueue<>(new PriorityQueue<>(new OrderComparatorDescending()))
                .addListener(new DefaultVisualizationListener<>(this));

        sellOrders = new ListenableQueue<>(new PriorityQueue<>(new OrderComparatorAscending()))
                .addListener(new DefaultVisualizationListener<>(this));

        noLimitSell = new ListenableQueue<>(new LinkedList<ExchangeOrder>())
                .addListener(new DefaultVisualizationListener<>(this));

        noLimitBuy = new ListenableQueue<>(new LinkedList<ExchangeOrder>())
                .addListener(new DefaultVisualizationListener<>(this));

        awaitingActivationBuy = new ListenableQueue<>(new PriorityQueue<>(new AwaitingExchangeOrderComparatorAscending()))
                .addListener(new DefaultVisualizationListener<>(this));

        awaitingActivationSell = new ListenableQueue<>(new PriorityQueue<>(new AwaitingExchangeOrderComparatorDescending()))
                .addListener(new DefaultVisualizationListener<>(this));

        settlementsToSend = new ListenableQueue<>(new LinkedList<TransactionSettlement>())
                .addListener(new DefaultVisualizationListener<>(this));

        canceledOrders = new ListenableQueue<>(new LinkedList<OrderSubmitter>())
                .addListener(new DefaultVisualizationListener<>(this));

        priceTracker = new PriceTracker(symbol, exchangeName);
        this.symbol = symbol;

        exchangeWindow = AgentWindowManager.getInstance().getAgentWindows().stream()
                .filter(aw -> Objects.equals(aw.getName(), exchangeName))
                .findFirst()
                .orElse(null);

        lastId = ExchangeOrderingID.getZero();
    }

    public PriceTracker getPriceTracker() {
        return priceTracker;
    }
    public StockSymbol getSymbol() {
        return symbol;
    }

    public AgentWindow getExchangeWindow() {
        return exchangeWindow;
    }

    public void setSessionNumber(Long sessionNumber) {
        this.sessionNumber = sessionNumber;
    }

    public void setSeconds(Long seconds) {
        this.seconds = seconds;
    }

    private void saveTransaction(BuyerSettlement buyerSettlement, SellerSettlement sellerSettlement) {
        settlementsToSend.add(sellerSettlement);
        settlementsToSend.add(buyerSettlement);
        if (!Objects.equals(buyerSettlement.getQuantity(), sellerSettlement.getQuantity()) ||
                !buyerSettlement.getUnitPrice().equals(sellerSettlement.getUnitPrice())) {
            throw new RuntimeException("Buyer/Seller settlement does not match");
        }
        this.priceTracker.submitData(
                buyerSettlement.getUnitPrice(),
                buyerSettlement.getQuantity(),
                buyerSettlement.getAddressee(),
                sellerSettlement.getAddressee(),
                this.sessionNumber,
                this.seconds);

        updateExchangeWindow();
    }

    private Double getReferencePrice() {
        if (priceTracker.getLastPrice() != null) {
            return priceTracker.getLastPrice();
        } else {
            return symbol.getIPOPrice();
        }
    }

    private void placeSell(ExchangeOrder o) {
        lastId = lastId.next();
        o.setSeqId(lastId);
        Double lastPriceFixing = null;
        logger.info("Incomming SELL " + o);
        //w pierwszej kolejności wykonujemy zlecenia PKC
        //Problemem jest że nie wiem jak obliczyć cenę, przyjmę więc max cenę oferty i maksymalne zlecenie
        while (!noLimitBuy.isEmpty() && o.getQuantity() > 0) {
            ExchangeOrder topBuyOrder = noLimitBuy.peek();
            Double transactionUnitPrice = o.getPrice();
            if (!o.hasPriceLimit()) {
                transactionUnitPrice = getReferencePrice();//Math.min(transactionUnitPrice, buyOrders.peek().getPrice());
            }
            Long tradedQuantity = Math.min(o.getQuantity(), Objects.requireNonNull(topBuyOrder).getQuantity());
            logger.info("Incomming SELL " + o + " matched with " + topBuyOrder + 
                    " in against NOLimit phase. Sold " + tradedQuantity + "@" + transactionUnitPrice);
            
            o.reduceQuantity(tradedQuantity);
            topBuyOrder.reduceQuantity(tradedQuantity);
            
            if (topBuyOrder.getQuantity() == 0) {
                noLimitBuy.poll();
            }
            
            Pair<BuyerSettlement, SellerSettlement> settlements = SettlementCreator.createSettlement(
                    topBuyOrder, 
                    o, 
                    tradedQuantity, 
                    transactionUnitPrice
            );
            
            // Zapisz transakcje
            this.saveTransaction(settlements.first(), settlements.second());
            lastPriceFixing = transactionUnitPrice;

        }
        logger.info("Incomming SELL " + o + " passed NOLimit phase");
        //Jeśli są jakiekolwiek buy pasujące
        while (!buyOrders.isEmpty() && o.getQuantity() > 0 && 
                Objects.requireNonNull(buyOrders.peek()).getPrice() >= o.getPrice()) {
            // Dopuki są chętni do zakupu, mamy co sprzedawać i kupujący oferują więcej/= niż my chcemy dostać
            //wykonać za co najmniej naszą cenę
            ExchangeOrder topBuyOrder = buyOrders.peek();
            Long tradedQuantity = Math.min(o.getQuantity(), Objects.requireNonNull(topBuyOrder).getQuantity());
            Double unitPrice = topBuyOrder.getPrice(); // być może min z tej ceny i ostatniej rynkowej jakny sam pkc był
            logger.info("Incomming SELL " + o + " matched with " + topBuyOrder + 
                    " in against Limit phase. Sold " + tradedQuantity + "@" + unitPrice);
            // Wykonanie transakcji
            
            o.reduceQuantity(tradedQuantity);
            topBuyOrder.reduceQuantity(tradedQuantity);
            Pair<BuyerSettlement, SellerSettlement> settlements = SettlementCreator.createSettlement(
                    topBuyOrder, 
                    o, 
                    tradedQuantity, 
                    unitPrice
            );

            // Zapisz transakcje
            this.saveTransaction(settlements.first(), settlements.second());

            // Usuń w pełni zrealizowane zlecenie kupna
            if (topBuyOrder.getQuantity() == 0) {
                buyOrders.poll();
            }
            
            lastPriceFixing = unitPrice;
        }
        logger.info("Incomming SELL " + o + " passed Limit phase");
        if (o.getQuantity() > 0) {
            logger.info("Incomming SELL " + o + " remains not fully executed - saving phase");
            if (!o.hasPriceLimit()) {
                noLimitSell.add(o);
            } else {
                sellOrders.add(o);
            }
        } else {
            logger.info("Incomming SELL " + o + " was fully executed");
        }
        updateAwaitingOrders(lastPriceFixing);
    }

    private void placeBuy(ExchangeOrder o) {
        lastId = lastId.next();
        o.setSeqId(lastId);
        logger.info("Incomming BUY " + o);
        Double lastPriceFixing = null;
        while (!noLimitSell.isEmpty() && o.getQuantity() > 0) {
            ExchangeOrder topSellOrder = noLimitSell.peek();
            Double transactionUnitPrice = o.getPrice();
            if (!o.hasPriceLimit()) {
                transactionUnitPrice = getReferencePrice();//transactionUnitPrice = Math.min(transactionUnitPrice, buyOrders.peek().getPrice());
            }
            Long tradedQuantity = Math.min(o.getQuantity(), Objects.requireNonNull(topSellOrder).getQuantity());
            logger.info("Incomming BUY " + o + " matched with " + topSellOrder + " in against NOLimit phase. Sold " + tradedQuantity + "@" + transactionUnitPrice);

            o.reduceQuantity(tradedQuantity);
            topSellOrder.reduceQuantity(tradedQuantity);
            
            if (topSellOrder.getQuantity() == 0) {
                noLimitSell.poll();
            }
            
            Pair<BuyerSettlement, SellerSettlement> settlements = SettlementCreator.createSettlement(
                    o, 
                    topSellOrder, 
                    tradedQuantity, 
                    transactionUnitPrice
            );
            
            // Zapisz transakcje
            this.saveTransaction(settlements.first(), settlements.second());
            lastPriceFixing = transactionUnitPrice;
        }
        logger.info("Incomming BUY " + o + " passed NOLimit phase");
        while (!sellOrders.isEmpty() && o.getQuantity() > 0 &&
                Objects.requireNonNull(sellOrders.peek()).getPrice() <= o.getPrice()) {
            ExchangeOrder topSellOrder = sellOrders.peek();
            Long tradedQuantity = Math.min(o.getQuantity(), Objects.requireNonNull(topSellOrder).getQuantity());
            Double unitPrice = topSellOrder.getPrice();
            logger.info("Incomming BUY " + o + " matched with " + topSellOrder + " in against Limit phase. Sold " + tradedQuantity + "@" + unitPrice);

            o.reduceQuantity(tradedQuantity);
            topSellOrder.reduceQuantity(tradedQuantity);
            Pair<BuyerSettlement, SellerSettlement> settlements = SettlementCreator.createSettlement(
                    o, 
                    topSellOrder, 
                    tradedQuantity, 
                    unitPrice
            );

            // Zapisz transakcje
            this.saveTransaction(settlements.first(), settlements.second());

            // Usuń w pełni zrealizowane zlecenie sprzedaży
            if (topSellOrder.getQuantity() == 0) {
                sellOrders.poll();
            }
            lastPriceFixing = unitPrice;
        }
        logger.info("Incomming BUY " + o + " passed Limit phase");
        if (o.getQuantity() > 0) {
            logger.info("Incomming BUY " + o + " remains not fully executed - saving phase");
            if (!o.hasPriceLimit()) {
                noLimitBuy.add(o);
            } else {
                buyOrders.add(o);
            }
        } else {
            logger.info("Incomming BUY " + o + " was fully executed");
        }
        updateAwaitingOrders(lastPriceFixing);
    }

    private void placeOrder(ExchangeOrder o) {
        if (o.getOrderType() == OrderType.BUY) {
            placeBuy(o);
        } else if (o.getOrderType() == OrderType.SELL) {
            placeSell(o);
        }
    }

    private void placeAwaitingOrder(AwaitingExchangeOrder o) {
        if (o.getActivatedOrder().getOrderType() == OrderType.BUY) {
            awaitingActivationBuy.add(o);
        } else if (o.getActivatedOrder().getOrderType() == OrderType.SELL) {
            awaitingActivationSell.add(o);
        }
    }


    private void updateAwaitingOrders(Double lastPrice) {
        while (!awaitingActivationBuy.isEmpty() && awaitingActivationBuy.peek().getActivationPrice() >= lastPrice) {
            placeBuy(Objects.requireNonNull(awaitingActivationBuy.poll()).getActivatedOrder());
        }
        while (!awaitingActivationSell.isEmpty() && awaitingActivationSell.peek().getActivationPrice() <= lastPrice) {
            placeSell(Objects.requireNonNull(awaitingActivationSell.poll()).getActivatedOrder());
        }
    }

    private void expire(ExchangeDate date) {
//        buyOrders.removeIf(order -> order.isExpired(date));
//        sellOrders.removeIf(order -> order.isExpired(date));
//        noLimitBuy.removeIf(order -> order.isExpired(date));
//        noLimitSell.removeIf(order -> order.isExpired(date));
//        awaitingActivationBuy.removeIf(awaitingExchangeOrder -> awaitingExchangeOrder.getActivatedOrder().isExpired(date));
//        awaitingActivationSell.removeIf(awaitingExchangeOrder -> awaitingExchangeOrder.getActivatedOrder().isExpired(date));

        // Obsługa buyOrders
        buyOrders.removeIf(order -> {
            if (order.isExpired(date)) {
                canceledOrders.add(order.getSubmitter());
                return true; // Usunięcie
            }
            return false; // Pozostawienie w liście
        });

        // Obsługa sellOrders
        sellOrders.removeIf(order -> {
            if (order.isExpired(date)) {
                canceledOrders.add(order.getSubmitter());
                return true; // Usunięcie
            }
            return false; // Pozostawienie w liście
        });

        // Obsługa noLimitBuy
        noLimitBuy.removeIf(order -> {
            if (order.isExpired(date)) {
                canceledOrders.add(order.getSubmitter());
                return true; // Usunięcie
            }
            return false; // Pozostawienie w liście
        });

        // Obsługa noLimitSell
        noLimitSell.removeIf(order -> {
            if (order.isExpired(date)) {
                canceledOrders.add(order.getSubmitter());
                return true; // Usunięcie
            }
            return false; // Pozostawienie w liście
        });

        // Obsługa awaitingActivationBuy
        awaitingActivationBuy.removeIf(awaitingExchangeOrder -> {
            var activatedOrder = awaitingExchangeOrder.getActivatedOrder();
            if (activatedOrder.isExpired(date)) {
                canceledOrders.add(awaitingExchangeOrder.getActivatedOrder().getSubmitter());
                return true; // Usunięcie
            }
            return false; // Pozostawienie w liście
        });

        // Obsługa awaitingActivationSell
        awaitingActivationSell.removeIf(awaitingExchangeOrder -> {
            var activatedOrder = awaitingExchangeOrder.getActivatedOrder();
            if (activatedOrder.isExpired(date)) {
                canceledOrders.add(awaitingExchangeOrder.getActivatedOrder().getSubmitter());
                return true; // Usunięcie
            }
            return false; // Pozostawienie w liście
        });

    }

    public void placeDisposition(PlaceableDisposition disposition) {
        if (!disposition.isAwaiting()) {
            placeOrder((ExchangeOrder) disposition);
        } else {
            placeAwaitingOrder((AwaitingExchangeOrder) disposition);
        }
    }

    public TransactionSettlement getNextSettlement() {
        return settlementsToSend.peek();
    }

    public TransactionSettlement popNextSettlement() {
        return settlementsToSend.poll();
    }

    public boolean isTransactionSettlementAvailable() {
        return !settlementsToSend.isEmpty();
    }

    public void expirationUpdate(ExchangeDate date) {
        expire(date);
    }

    /**
     * Retrieves the top 5 buy offers in ascending order of priority (best offers).
     * Does not remove these offers from the queue.
     */
    public List<ExchangeOrder> getTopBuyOffers() {
        return getTopOrders(buyOrders, 5);
    }

    /**
     * Retrieves the top 5 sell offers in ascending order of priority (best offers).
     * Does not remove these offers from the queue.
     */
    public List<ExchangeOrder> getTopSellOffers() {
        return getTopOrders(sellOrders, 5);
    }

    /**
     * Retrieves up to 'n' best orders from the provided queue without modifying it.
     */
    private List<ExchangeOrder> getTopOrders(Queue<ExchangeOrder> orders, int n) {
        List<ExchangeOrder> topOrders = new ArrayList<>();
        int count = 0;

        for (ExchangeOrder order : orders) {
            topOrders.add(order);
            if (++count >= n) break; // Limit to 'n' orders
        }
        return topOrders;
    }

    public OrderSubmitter popNextCancellation() {
        return canceledOrders.poll();
    }

    public void updateExchangeWindow() {
        if (exchangeWindow != null) {
            exchangeWindow.updateAndDraw(getViewModel());
        }
    }

    private StockExchangeViewModel getViewModel() {
        return new StockExchangeViewModel(new LinkedList<>(buyOrders),
                new LinkedList<>(sellOrders),
                new LinkedList<>(noLimitBuy),
                new LinkedList<>(noLimitSell),
                new LinkedList<>(awaitingActivationBuy),
                new LinkedList<>(awaitingActivationSell),
                new LinkedList<>(settlementsToSend),
                new LinkedList<>(canceledOrders),
                new LinkedList<>(priceTracker.getHistory()));
    }
}

package org.example.stockexchange;

import org.example.stockexchange.settlements.BuyerSettlement;
import org.example.stockexchange.settlements.SellerSettlement;
import org.example.stockexchange.settlements.SettlementCreator;
import org.example.stockexchange.settlements.TransactionSettlement;
import org.example.stockexchange.utils.CurrentDate;
import org.example.stockexchange.utils.OrderType;
import org.example.stockexchange.utils.PriceTracker;
import org.example.stockexchange.utils.StockSymbol;
import org.glassfish.pfl.basic.contain.Pair;

import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class OrderSheet {

    private Queue<Order> buyOrders;
    private Queue<Order> sellOrders;

    private Queue<Order> noLimitSell;
    private Queue<Order> noLimitBuy;

    private Queue<AwaitingOrder> awaitingActivationBuy;
    private Queue<AwaitingOrder> awaitingActivationSell;

    private PriceTracker priceTracker;
    private StockSymbol symbol;

    private Queue<TransactionSettlement> settlementsToSend;

    public OrderSheet(StockSymbol symbol) {
        buyOrders = new PriorityQueue<>(new OrderComparator());
        sellOrders = new PriorityQueue<>(new OrderComparator().reversed());

        noLimitSell = new LinkedList<>();
        noLimitBuy = new LinkedList<>();

        awaitingActivationBuy = new PriorityQueue<>(new AwaitingOrderComparator());
        awaitingActivationSell = new PriorityQueue<>(new AwaitingOrderComparator());

        priceTracker = new PriceTracker(symbol);
        this.symbol = symbol;

        settlementsToSend = new LinkedList<>();
    }

    private void saveTransaction(BuyerSettlement buyerSettlement, SellerSettlement sellerSettlement) {
        settlementsToSend.add(sellerSettlement);
        settlementsToSend.add(buyerSettlement);
        if(buyerSettlement.getQuantity() != sellerSettlement.getQuantity() || buyerSettlement.getUnitPrice() != sellerSettlement.getUnitPrice()) {
            throw new RuntimeException("Buyer/Seller settlement does not match");
        }
        this.priceTracker.submitData(buyerSettlement.getUnitPrice(), buyerSettlement.getQuantity());
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
        Double lastPriceFixing = null;
        //w pierwszej kolejności wykonujemy zlecenia PKC
        //Problemem jest że nie wiem jak obliczyć cenę, przyjmę więc max cenę oferty i maksymalne zlecenie
        while(!noLimitBuy.isEmpty() && o.getQuantity()>0){
            Order topBuyOrder = noLimitBuy.peek();
            Double transactionUnitPrice = o.getPrice();
            if(!o.hasPriceLimit()){
                transactionUnitPrice = getReferencePrice();//Math.min(transactionUnitPrice, buyOrders.peek().getPrice());
            }
            int tradedQuantity = Math.min(o.getQuantity(), topBuyOrder.getQuantity());
            o.reduceQuantity(tradedQuantity);
            topBuyOrder.reduceQuantity(tradedQuantity);
            Pair<BuyerSettlement, SellerSettlement> settlements = SettlementCreator.createSettlement(topBuyOrder, o, transactionUnitPrice);
            // Zapisz transakcje
            this.saveTransaction(settlements.first(), settlements.second());
            lastPriceFixing=transactionUnitPrice;
        }

        //Jeśli są jakiekolwiek buy pasujące
        while(!buyOrders.isEmpty() && o.getQuantity()>0 && buyOrders.peek().getPrice()>=o.getPrice()){
            // Dopuki są chętni do zakupu, mamy co sprzedawać i kupujący oferują więcej/= niż my chcemy dostać
            //wykonać za co najmniej naszą cenę
            Order topBuyOrder = buyOrders.peek();
            int tradedQuantity = Math.min(o.getQuantity(), topBuyOrder.getQuantity());

            // Wykonanie transakcji
            o.reduceQuantity(tradedQuantity);
            topBuyOrder.reduceQuantity(tradedQuantity);

            Double unitPrice = topBuyOrder.getPrice(); // być może min z tej ceny i ostatniej rynkowej jakny sam pkc był

            Pair<BuyerSettlement, SellerSettlement> settlements = SettlementCreator.createSettlement(topBuyOrder, o, unitPrice);

            // Zapisz transakcje
            this.saveTransaction(settlements.first(), settlements.second());

            // Usuń w pełni zrealizowane zlecenie kupna
            if (topBuyOrder.getQuantity() == 0) {
                buyOrders.poll();
            }
            lastPriceFixing=unitPrice;
        }
        if(o.getQuantity()>0){
            if(!o.hasPriceLimit()){
                noLimitSell.add(o);
            }else {
                sellOrders.add(o);
            }
        }
        updateAwaitingOrders(lastPriceFixing);
    }

    private void placeBuy(Order o) {
        Double lastPriceFixing = null;
        while(!noLimitBuy.isEmpty() && o.getQuantity()>0){
            Order topBuyOrder = noLimitBuy.peek();
            Double transactionUnitPrice = o.getPrice();
            if(!o.hasPriceLimit()){
                transactionUnitPrice = getReferencePrice();//transactionUnitPrice = Math.min(transactionUnitPrice, buyOrders.peek().getPrice());
            }
            int tradedQuantity = Math.min(o.getQuantity(), topBuyOrder.getQuantity());
            o.reduceQuantity(tradedQuantity);
            topBuyOrder.reduceQuantity(tradedQuantity);
            Pair<BuyerSettlement, SellerSettlement> settlements = SettlementCreator.createSettlement(topBuyOrder, o, transactionUnitPrice);
            // Zapisz transakcje
            this.saveTransaction(settlements.first(), settlements.second());
            lastPriceFixing = transactionUnitPrice;
        }
        while (!sellOrders.isEmpty() && o.getQuantity() > 0 && sellOrders.peek().getPrice() <= o.getPrice()) {
            Order topSellOrder = sellOrders.peek();
            int tradedQuantity = Math.min(o.getQuantity(), topSellOrder.getQuantity());

            o.reduceQuantity(tradedQuantity);
            topSellOrder.reduceQuantity(tradedQuantity);

            Double unitPrice = topSellOrder.getPrice();

            Pair<BuyerSettlement, SellerSettlement> settlements = SettlementCreator.createSettlement(o, topSellOrder, unitPrice);

            // Zapisz transakcje
            this.saveTransaction(settlements.first(), settlements.second());

            // Usuń w pełni zrealizowane zlecenie sprzedaży
            if (topSellOrder.getQuantity() == 0) {
                sellOrders.poll();
            }
            lastPriceFixing = unitPrice;
        }

        if (o.getQuantity() > 0) {
            if(!o.hasPriceLimit()){
                noLimitBuy.add(o);
            }else {
                buyOrders.add(o);
            }
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

    private void updateAwaitingOrders(Double lastPrice){
        while(!awaitingActivationBuy.isEmpty() && awaitingActivationBuy.peek().getActivationPrice()>=lastPrice){
            placeBuy(awaitingActivationBuy.poll().getActivatedOrder());
        }
        while(!awaitingActivationSell.isEmpty() && awaitingActivationSell.peek().getActivationPrice()<=lastPrice){
            placeSell(awaitingActivationSell.poll().getActivatedOrder());
        }
    }

    private void expire(CurrentDate date){
        buyOrders.removeIf(order -> order.isExpired(date));
        sellOrders.removeIf(order -> order.isExpired(date));
        noLimitBuy.removeIf(order -> order.isExpired(date));
        noLimitSell.removeIf(order -> order.isExpired(date));
        awaitingActivationBuy.removeIf(awaitingOrder -> awaitingOrder.getActivatedOrder().isExpired(date));
        awaitingActivationSell.removeIf(awaitingOrder -> awaitingOrder.getActivatedOrder().isExpired(date));
    }
}

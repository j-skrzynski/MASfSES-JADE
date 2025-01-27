package org.example.visualization.viewmodels;

import org.example.logic.stockexchange.order.awaitingorder.AwaitingExchangeOrder;
import org.example.logic.stockexchange.order.marketorder.ExchangeOrder;
import org.example.logic.stockexchange.settlements.TransactionSettlement;
import org.example.logic.stockexchange.utils.OrderSubmitter;
import org.glassfish.pfl.basic.contain.Pair;

import java.util.*;

public class StockExchangeViewModel implements Comparable<StockExchangeViewModel> {
    private final Queue<ExchangeOrder> buyOrders;
    private final Queue<ExchangeOrder> sellOrders;
    private final Queue<ExchangeOrder> noLimitSell;
    private final Queue<ExchangeOrder> noLimitBuy;
    private final Queue<AwaitingExchangeOrder> awaitingActivationBuy;
    private final Queue<AwaitingExchangeOrder> awaitingActivationSell;
    private final Queue<TransactionSettlement> settlementsToSend;
    private final Queue<OrderSubmitter> canceledOrders;
    private final List<Pair<Double, Long>> history;

    public StockExchangeViewModel(Queue<ExchangeOrder> buyOrders,
                                  Queue<ExchangeOrder> sellOrders,
                                  Queue<ExchangeOrder> noLimitBuy,
                                  Queue<ExchangeOrder> noLimitSell,
                                  Queue<AwaitingExchangeOrder> awaitingActivationBuy,
                                  Queue<AwaitingExchangeOrder> awaitingActivationSell,
                                  Queue<TransactionSettlement> settlementsToSend,
                                  Queue<OrderSubmitter> canceledOrders,
                                  List<Pair<Double, Long>> history) {
        this.buyOrders = buyOrders;
        this.sellOrders = sellOrders;
        this.noLimitBuy = noLimitBuy;
        this.noLimitSell = noLimitSell;
        this.awaitingActivationBuy = awaitingActivationBuy;
        this.awaitingActivationSell = awaitingActivationSell;
        this.settlementsToSend = settlementsToSend;
        this.canceledOrders = canceledOrders;
        this.history = history;
    }

    public StockExchangeViewModel() {
        this(new LinkedList<>() {},
                new LinkedList<>() {},
                new LinkedList<>() {},
                new LinkedList<>() {},
                new LinkedList<>() {},
                new LinkedList<>() {},
                new LinkedList<>() {},
                new LinkedList<>() {},
                new ArrayList<>() {});
    }

    public Queue<ExchangeOrder> getBuyOrders() {
        return buyOrders;
    }

    public Queue<ExchangeOrder> getSellOrders() {
        return sellOrders;
    }

    public Queue<ExchangeOrder> getNoLimitSell() {
        return noLimitSell;
    }

    public Queue<ExchangeOrder> getNoLimitBuy() {
        return noLimitBuy;
    }

    public Queue<AwaitingExchangeOrder> getAwaitingActivationBuy() {
        return awaitingActivationBuy;
    }

    public Queue<AwaitingExchangeOrder> getAwaitingActivationSell() {
        return awaitingActivationSell;
    }

    public Queue<TransactionSettlement> getSettlementsToSend() {
        return settlementsToSend;
    }

    public Queue<OrderSubmitter> getCanceledOrders() {
        return canceledOrders;
    }

    public List<Pair<Double, Long>> getHistory() {
        return history;
    }

    @Override
    public int compareTo(StockExchangeViewModel otherModel) {
        if (otherModel == null) {
            return -1;
        }

        if (buyOrders.equals(otherModel.getBuyOrders()) &&
                sellOrders.equals(otherModel.getSellOrders()) &&
                noLimitSell.equals(otherModel.getNoLimitSell()) &&
                noLimitBuy.equals(otherModel.getNoLimitBuy()) &&
                awaitingActivationBuy.equals(otherModel.getAwaitingActivationBuy()) &&
                awaitingActivationSell.equals(otherModel.getAwaitingActivationSell()) &&
                settlementsToSend.equals(otherModel.getSettlementsToSend()) &&
                canceledOrders.size() == otherModel.getCanceledOrders().size() &&
                history.size() == otherModel.getHistory().size()) {
            return 0;
        }

        return 1;
    }
}

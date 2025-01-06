package org.example.logic.stockexchange.order.awaitingorder;

import org.example.logic.stockexchange.order.PlaceableDisposition;
import org.example.logic.stockexchange.order.marketorder.ExchangeOrder;

public class AwaitingExchangeOrder implements PlaceableDisposition {


    /// This order is going to be released after activation
    private final ExchangeOrder order;
    private final Double activationPrice;

    public AwaitingExchangeOrder(ExchangeOrder order, Double activationPrice) {
        super();
        this.order = order;
        this.activationPrice = activationPrice;
    }

    public ExchangeOrder getActivatedOrder() {
        return order;
    }

    public Double getActivationPrice() {
        return activationPrice;
    }

    @Override
    public boolean isAwaiting() {
        return true;
    }
}

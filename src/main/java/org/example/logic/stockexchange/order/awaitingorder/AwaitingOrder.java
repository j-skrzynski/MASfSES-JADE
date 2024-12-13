package org.example.logic.stockexchange.order.awaitingorder;

import org.example.logic.stockexchange.order.PlacableDisposition;
import org.example.logic.stockexchange.order.marketorder.ExchangeOrder;

public class AwaitingOrder implements PlacableDisposition {



    /// This order is going to be released after activation
    private ExchangeOrder order;
    private Double activationPrice;

    public AwaitingOrder(ExchangeOrder order, Double activationPrice) {
        super();
        this.order = order;
        this.activationPrice = activationPrice;
    }

    public ExchangeOrder getActivatedOrder(){
        return order;
    }

    public Double getActivationPrice(){
        return activationPrice;
    }

    @Override
    public boolean isAwaiting() {
        return true;
    }
}

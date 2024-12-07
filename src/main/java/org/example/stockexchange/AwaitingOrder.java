package org.example.stockexchange;

public class AwaitingOrder {

    /// This order is going to be released after activation
    private Order order;

    public boolean isActivatedBy(){
        return true;
    }

    public Order getActivatedOrder(){
        return null;
    }

    public Double getActivationPrice(){}
}

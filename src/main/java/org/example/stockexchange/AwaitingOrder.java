package org.example.stockexchange;

public class AwaitingOrder {

    /// This order is going to be released after activation
    private Order order;
    private Double activationPrice;
//    public boolean isActivatedBy(){
//        return true;
//    }

    public Order getActivatedOrder(){
        return order;
    }

    public Double getActivationPrice(){
        return activationPrice;
    }
}

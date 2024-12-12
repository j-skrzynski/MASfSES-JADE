package org.example.logic.stockexchange.order;

public class AwaitingOrder implements PlacableDisposition{



    /// This order is going to be released after activation
    private Order order;
    private Double activationPrice;

    public AwaitingOrder(Order order, Double activationPrice) {
        super();
        this.order = order;
        this.activationPrice = activationPrice;
    }

    public Order getActivatedOrder(){
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

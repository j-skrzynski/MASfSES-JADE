package org.example.datamodels.order;

public class AwaitingOrder {
    private Order order;
    private boolean awaiting;
    private Double price;

    public AwaitingOrder(Order order, boolean awaiting, Double price) {
        this.order = order;
        this.awaiting = awaiting;
        this.price = price;
    }
}

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

    public Order getOrder() {
        return order;
    }

    public boolean isAwaiting() {
        return awaiting;
    }

    public Double getPrice() {
        return price;
    }

    public String getOrderCommand(){
        if (awaiting){
            if(order.hasPriceLimit()){
                return "STOPLIMIT";
            }
            return "STOP";
        }
        return order.getOrderCommand();
    }
}

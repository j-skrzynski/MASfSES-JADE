package org.example.datamodels.order;

public record AwaitingOrder(Order order, boolean awaiting, Double price) {

    public String getOrderCommand() {
        if (awaiting) {
            if (order.hasPriceLimit()) {
                return "STOPLIMIT";
            }
            return "STOP";
        }
        return order.getOrderCommand();
    }
}

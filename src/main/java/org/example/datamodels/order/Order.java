package org.example.datamodels.order;

import org.example.datamodels.StockSymbol;

public class Order implements Comparable<Order> {

    // SHOULD COVER PKC AND LIMIT
    protected StockSymbol symbol;
    protected OrderType orderType;
    protected Long quantity;
    protected Double price;
    protected String expirationSpecification;
    private boolean hasLimit;

    public Order(
            StockSymbol symbol,
            OrderType orderType,
            Double price,
            Long quantity
    ) {
        this.symbol = symbol;
        this.orderType = orderType;
        this.quantity = quantity;

        if (price == null || price.compareTo(0.0) == 0) {    //zlecenie nolimit
            this.price = getInfValue(orderType);
            this.hasLimit = false;
        } else {
            this.hasLimit = true;
            this.price = price;
        }
    }

    private static Double getInfValue(OrderType orderType) {
        return switch (orderType) {
            case BUY -> Double.MAX_VALUE;
            case SELL -> Double.MIN_VALUE;
            default -> throw new IllegalArgumentException("Invalid order type");
        };

    }

    public static int compare(Order o1, Order o2) {
        return o1.compareTo(o2);
    }

    public void setHasLimit(boolean hasLimit) {
        this.hasLimit = hasLimit;
    }

    public boolean hasLimit() {
        return hasLimit;
    }

    public StockSymbol getSymbol() {
        return symbol;
    }

    public Long getQuantity() {
        return quantity;
    }

    public Double getPrice() {
        return price;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void reduceQuantity(Long tradedQuantity) {
        quantity -= tradedQuantity;
    }

    public boolean hasPriceLimit() {
        return hasLimit;
    }

    @Override
    public int compareTo(Order o) {
        return Double.compare(getPrice(), o.getPrice());
    }

    public String getExpirationSpecification() {
        return expirationSpecification;
    }

    public void setExpirationSpecification(String expirationSpecification) {
        this.expirationSpecification = expirationSpecification;
    }

    public String getOrderCommand() {
        if (hasLimit) {
            return "LIMIT";
        }
        return "NOLIMIT";
    }
}

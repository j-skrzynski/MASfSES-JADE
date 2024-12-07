package org.example.stockexchange.orders;

import org.example.stockexchange.StockSymbol;

public abstract class Order implements Comparable<Order> {
    protected StockSymbol stock;
    protected int quantity;
    protected OrderValidity validity;
    protected Double price;
    protected OrderType type;

    public Order(StockSymbol symbol, OrderType type, OrderValidity validity) {
        this.type = type;
        this.stock = symbol;
        this.validity = validity;
    }


    /**
     * Checks if this order matches another and if they can be cross-executed
     * @param order Order to check against
     * @return True on match
     */
    public abstract boolean isMatch(Order order);

    public OrderType getType() {return type;}
    public Double getPrice() {return price;}

    public abstract void notify(Double price);

    @Override
    public int compareTo(Order o) {
        if(type == OrderType.BUY && o.type == OrderType.BUY) {
            // od najwyższej oferty do najniższej -> descending
            return Double.compare(o.price,price);
        }else if(type == OrderType.SELL && o.type == OrderType.SELL) {
            // od najtańszej do najdroższej -> ascending
            return Double.compare(price, o.price);
        }
        throw new RuntimeException("Mixed types of orders");
    }
}


package org.example.stockexchange;

import org.example.stockexchange.orders.Order;

import java.util.List;

public abstract class OrderSpreadsheet {

    protected List<Order> buy_orders;
    protected List<Order> sell_orders;


    public abstract void submitOrder(Order order);
}

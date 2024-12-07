package org.example.stockexchange.orders.gpw;

import org.example.stockexchange.StockSymbol;
import org.example.stockexchange.orders.Order;
import org.example.stockexchange.orders.OrderType;
import org.example.stockexchange.orders.OrderValidity;

public class GPWOrderLIMIT extends Order {
    public GPWOrderLIMIT(StockSymbol symbol, OrderType type, OrderValidity validity, Double priceLimit) {
        super(symbol, type, validity);
        this.price = priceLimit;
    }

    @Override
    public boolean isMatch(Order order) {
        if (order.getType() != this.getType()){
            if (this.type == OrderType.BUY) {
                //to kupuje tamto sprzedaje
                return order.getPrice() <= this.price;
            } else if (this.type == OrderType.SELL) {
                return order.getPrice() >= this.price;
            }
        }
        return false;
    }
}

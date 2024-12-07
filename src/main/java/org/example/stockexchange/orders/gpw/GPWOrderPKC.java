package org.example.stockexchange.orders.gpw;

import org.example.stockexchange.StockSymbol;
import org.example.stockexchange.orders.Order;
import org.example.stockexchange.orders.OrderType;
import org.example.stockexchange.orders.OrderValidity;


/**
 * Class intended to represent the PKC order on GPW
 * Allowing any price
 */
public class GPWOrderPKC extends Order {
    /**
     * @param type Type of the order
     */
    public GPWOrderPKC(StockSymbol symbol, OrderType type, OrderValidity validity) {
        super(symbol, type, validity);
        if (type == OrderType.BUY) {
            this.price = Double.MAX_VALUE;
        }
        else if (type == OrderType.SELL) {
            this.price = Double.MIN_VALUE;
        }
    }

    @Override
    public boolean isMatch(Order order) {
        return this.type != order.getType();
    }
}

package org.example.stockexchange;

import org.example.stockexchange.utils.OrderType;
import org.example.stockexchange.utils.StockSymbol;

public class NoLimitOrder extends Order {

    public NoLimitOrder(StockSymbol symbol, OrderType orderType) {
        super(symbol, orderType);
    }

    @Override
    public boolean hasPriceLimit(){
        return false;
    }
}

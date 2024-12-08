package org.example.stockexchange.order;

import org.example.stockexchange.utils.ExchangeDate;
import org.example.stockexchange.utils.OrderType;
import org.example.stockexchange.utils.StockSymbol;

public class NoLimitOrder extends Order {

    public NoLimitOrder(StockSymbol symbol, OrderType orderType, ExchangeDate expirationDate, int quantity) {
        super(symbol, orderType,expirationDate,null,quantity);
    }

    @Override
    public boolean hasPriceLimit(){
        return false;
    }
}

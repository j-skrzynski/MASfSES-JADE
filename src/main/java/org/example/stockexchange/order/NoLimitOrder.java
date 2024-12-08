package org.example.stockexchange.order;

import org.example.stockexchange.utils.ExchangeDate;
import org.example.stockexchange.utils.OrderSubmitter;
import org.example.stockexchange.utils.OrderType;
import org.example.stockexchange.utils.StockSymbol;

public class NoLimitOrder extends Order {

    public NoLimitOrder(StockSymbol symbol, OrderType orderType, ExchangeDate expirationDate, int quantity, OrderSubmitter submitter) {
            super(symbol, orderType,expirationDate,getInfValue(orderType),quantity,submitter);
    }

    @Override
    public boolean hasPriceLimit(){
        return false;
    }

    private static Double getInfValue(OrderType orderType){
        switch(orderType){
            case BUY:
                return Double.MAX_VALUE;
            case SELL:
                return Double.MIN_VALUE;
            default:
                throw new IllegalArgumentException("Invalid order type");
        }

    }
}

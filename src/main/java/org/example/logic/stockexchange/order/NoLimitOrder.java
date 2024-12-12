package org.example.logic.stockexchange.order;

import org.example.logic.stockexchange.utils.ExchangeDate;
import org.example.logic.stockexchange.utils.OrderSubmitter;
import org.example.logic.stockexchange.utils.OrderType;
import org.example.logic.stockexchange.utils.StockSymbol;

public class NoLimitOrder extends Order {

    public NoLimitOrder(StockSymbol symbol, OrderType orderType, ExchangeDate expirationDate, Long quantity, OrderSubmitter submitter) {
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

package org.example.logic.stockexchange.order.marketorder;

import org.example.logic.stockexchange.utils.ExchangeDate;
import org.example.logic.stockexchange.utils.OrderSubmitter;
import org.example.datamodels.order.OrderType;
import org.example.datamodels.StockSymbol;

public class NoLimitExchangeOrder extends ExchangeOrder {
    public NoLimitExchangeOrder(StockSymbol symbol, OrderType orderType, ExchangeDate expirationDate, Long quantity, OrderSubmitter submitter) {
            super(symbol, orderType,expirationDate,null,quantity,submitter);
    }
}

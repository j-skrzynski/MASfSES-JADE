package org.example.logic.stockexchange.order.marketorder;

import org.example.datamodels.StockSymbol;
import org.example.datamodels.order.Order;
import org.example.datamodels.order.OrderType;
import org.example.logic.stockexchange.order.PlacableDisposition;
import org.example.logic.stockexchange.utils.*;

public class ExchangeOrder extends Order implements PlacableDisposition {

    // SHOULD COVER PKC AND LIMIT
    private final ExchangeDate expirationDate;
    private final OrderSubmitter submitter;
    private ExchangeOrderingID seqId = null;

    public ExchangeOrder(StockSymbol symbol, OrderType orderType, ExchangeDate expirationDate, Double price, Long quantity, OrderSubmitter submitter) {
        super(symbol,orderType,price,quantity);
        this.expirationDate = expirationDate;
        this.submitter = submitter;
    }

    public ExchangeDate getExpirationDate() {
        return expirationDate;
    }

    public OrderSubmitter getSubmitter() {
        return submitter;
    }

    public boolean isExpired(ExchangeDate date){
        return getExpirationDate().isBeforeOrEqual(date);
    }

    public void setSeqId(ExchangeOrderingID seqId) {
        this.seqId = seqId;
    }

    public ExchangeOrderingID getSeqId() {
        return seqId;
    }

    @Override
    public boolean isAwaiting() {
        return false;
    }
}


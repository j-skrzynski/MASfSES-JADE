package org.example.logic.stockexchange.order.marketorder;

import org.example.datamodels.StockSymbol;
import org.example.datamodels.order.Order;
import org.example.datamodels.order.OrderType;
import org.example.logic.stockexchange.order.PlaceableDisposition;
import org.example.logic.stockexchange.utils.ExchangeDate;
import org.example.logic.stockexchange.utils.ExchangeOrderingID;
import org.example.logic.stockexchange.utils.OrderSubmitter;

public class ExchangeOrder extends Order implements PlaceableDisposition {

    // SHOULD COVER PKC AND LIMIT
    private final ExchangeDate expirationDate;
    private final OrderSubmitter submitter;
    private ExchangeOrderingID seqId = null;

    public ExchangeOrder(
            StockSymbol symbol,
            OrderType orderType,
            ExchangeDate expirationDate,
            Double price,
            Long quantity,
            OrderSubmitter submitter
    ) {
        super(symbol, orderType, price, quantity);
        this.expirationDate = expirationDate;
        this.submitter = submitter;
    }

    public ExchangeDate getExpirationDate() {
        return expirationDate;
    }

    public OrderSubmitter getSubmitter() {
        return submitter;
    }

    public boolean isExpired(ExchangeDate date) {
        return getExpirationDate().isBeforeOrEqual(date);
    }

    public ExchangeOrderingID getSeqId() {
        return seqId;
    }

    public void setSeqId(ExchangeOrderingID seqId) {
        this.seqId = seqId;
    }

    @Override
    public boolean isAwaiting() {
        return false;
    }
}


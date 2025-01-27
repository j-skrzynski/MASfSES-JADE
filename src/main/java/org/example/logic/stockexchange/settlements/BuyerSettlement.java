package org.example.logic.stockexchange.settlements;

import org.example.datamodels.StockSymbol;
import org.example.logic.stockexchange.utils.OrderSubmitter;

public class BuyerSettlement extends TransactionSettlement {
    public BuyerSettlement(
            OrderSubmitter addressee,
            StockSymbol symbol,
            Long stockQuantity,
            Double unitPrice
    ) {
        super(
                addressee,
                unitPrice * stockQuantity,
                0.0,
                symbol,
                0L,
                stockQuantity,
                unitPrice,
                stockQuantity
        );
    }
}

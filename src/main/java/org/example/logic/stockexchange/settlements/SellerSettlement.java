package org.example.logic.stockexchange.settlements;

import org.example.datamodels.StockSymbol;
import org.example.logic.stockexchange.utils.OrderSubmitter;

public class SellerSettlement extends TransactionSettlement {
    public SellerSettlement(
            OrderSubmitter addressee,
            StockSymbol symbol,
            Long stockQuantity,
            Double unitPrice
    ) {
        super(
                addressee,
                0.0,
                unitPrice * stockQuantity,
                symbol,
                stockQuantity,
                0L,
                unitPrice,
                stockQuantity
        );
    }
}

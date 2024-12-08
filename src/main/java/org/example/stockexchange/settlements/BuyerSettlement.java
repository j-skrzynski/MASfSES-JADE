package org.example.stockexchange.settlements;

import org.example.stockexchange.utils.OrderSubmitter;
import org.example.stockexchange.utils.StockSymbol;

public class BuyerSettlement extends TransactionSettlement{
    public BuyerSettlement(OrderSubmitter addressee, StockSymbol symbol, Long stockQuantity, Double unitPrice) {
        super(addressee, unitPrice*stockQuantity, 0.0, symbol, 0L, stockQuantity, unitPrice,stockQuantity);
    }
}

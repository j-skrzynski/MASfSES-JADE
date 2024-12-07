package org.example.stockexchange.settlements;

import org.example.stockexchange.utils.OrderSubmitter;
import org.example.stockexchange.utils.StockSymbol;

public class BuyerSettlement extends TransactionSettlement{
    public BuyerSettlement(OrderSubmitter addressee, StockSymbol symbol, int stockQuantity, Double unitPrice) {
        super(addressee, unitPrice*stockQuantity, 0.0, symbol, 0, stockQuantity, unitPrice,stockQuantity);
    }
}

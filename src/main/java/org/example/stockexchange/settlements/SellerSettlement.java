package org.example.stockexchange.settlements;

import org.example.stockexchange.utils.OrderSubmitter;
import org.example.stockexchange.utils.StockSymbol;

public class SellerSettlement extends TransactionSettlement{
    public SellerSettlement(OrderSubmitter addressee, StockSymbol symbol, Long stockQuantity, Double unitPrice) {
        super(addressee, 0.0, unitPrice*stockQuantity, symbol, stockQuantity, 0L, unitPrice,stockQuantity);
    }
}

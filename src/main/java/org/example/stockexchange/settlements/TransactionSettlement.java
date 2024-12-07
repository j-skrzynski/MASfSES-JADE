package org.example.stockexchange.settlements;

import org.example.stockexchange.utils.OrderSubmitter;
import org.example.stockexchange.utils.StockSymbol;

/**
 * Every time transaction is finished this class should be emited for seller and buyer
 */
public class TransactionSettlement {
    private OrderSubmitter addressee;
    private Double toPay;
    private Double toWithdraw;
    private StockSymbol symbol;
    private int soldStock;
    private int boughtStock;

    private Double unitPrice;
    private int quantity;

    public TransactionSettlement(OrderSubmitter addressee, Double toPay, Double toWithdraw, StockSymbol symbol, int soldStock, int boughtStock, Double unitPrice, int quantity) {
        this.addressee = addressee;
        this.toPay = toPay;
        this.toWithdraw = toWithdraw;
        this.symbol = symbol;
        this.soldStock = soldStock;
        this.boughtStock = boughtStock;
    }

    public OrderSubmitter getAddressee() {
        return addressee;
    }

    public Double getToPay() {
        return toPay;
    }

    public Double getToWithdraw() {
        return toWithdraw;
    }

    public StockSymbol getSymbol() {
        return symbol;
    }

    public int getSoldStock() {
        return soldStock;
    }

    public int getBoughtStock() {
        return boughtStock;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }
}
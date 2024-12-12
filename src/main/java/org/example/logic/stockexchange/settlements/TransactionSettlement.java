package org.example.logic.stockexchange.settlements;

import com.google.gson.Gson;
import org.example.logic.stockexchange.utils.OrderSubmitter;
import org.example.logic.stockexchange.utils.StockSymbol;

/**
 * Every time transaction is finished this class should be emited for seller and buyer
 */
public class TransactionSettlement {
    private final OrderSubmitter addressee;
    private final Double toPay;
    private final Double toWithdraw;
    private final StockSymbol symbol;
    private final Long soldStock;
    private final Long boughtStock;

    private final Double unitPrice;
    private final Long quantity;

    public TransactionSettlement(OrderSubmitter addressee, Double toPay, Double toWithdraw, StockSymbol symbol, Long soldStock, Long boughtStock, Double unitPrice, Long quantity) {
        this.addressee = addressee;
        this.toPay = toPay;
        this.toWithdraw = toWithdraw;
        this.symbol = symbol;
        this.soldStock = soldStock;
        this.boughtStock = boughtStock;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
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

    public Long getSoldStock() {
        return soldStock;
    }

    public Long getBoughtStock() {
        return boughtStock;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public Long getQuantity() {
        return quantity;
    }
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}

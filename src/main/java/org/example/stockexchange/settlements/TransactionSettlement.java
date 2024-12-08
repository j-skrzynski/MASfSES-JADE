package org.example.stockexchange.settlements;

import com.google.gson.Gson;
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
    private Long soldStock;
    private Long boughtStock;

    private Double unitPrice;
    private Long quantity;

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

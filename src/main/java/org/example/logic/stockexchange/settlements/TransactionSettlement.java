package org.example.logic.stockexchange.settlements;

import com.google.gson.Gson;
import org.example.datamodels.StockSymbol;
import org.example.datamodels.TransactionResult;
import org.example.logic.stockexchange.utils.OrderSubmitter;

import java.util.Objects;

/**
 * Every time transaction is finished this class should be emitted for seller and buyer
 */
public class TransactionSettlement {
    private final Gson gson = new Gson();
    private final OrderSubmitter addressee;
    private final Double toPay;
    private final Double toWithdraw;
    private final StockSymbol symbol;
    private final Long soldStock;
    private final Long boughtStock;

    private final Double unitPrice;
    private final Long quantity;

    public TransactionSettlement(
            OrderSubmitter addressee,
            Double toPay,
            Double toWithdraw,
            StockSymbol symbol,
            Long soldStock,
            Long boughtStock,
            Double unitPrice,
            Long quantity
    ) {
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
        return gson.toJson(this);
    }

    public String getTransactionResult() {
        TransactionResult tr = new TransactionResult(
                this.toPay,
                this.toWithdraw,
                this.soldStock,
                this.boughtStock,
                this.symbol.getShortName(),
                this.addressee.getBrokerOrderId()
        );
        return gson.toJson(tr);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TransactionSettlement ts)) {
            return false;
        }

        return Objects.equals(toPay, ts.getToPay()) &&
                Objects.equals(toWithdraw, ts.getToWithdraw()) &&
                Objects.equals(soldStock, ts.getSoldStock()) &&
                Objects.equals(boughtStock, ts.getBoughtStock()) &&
                Objects.equals(unitPrice, ts.getUnitPrice()) &&
                Objects.equals(quantity, ts.getQuantity());
    }
}

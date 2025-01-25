package org.example.datamodels;

import java.util.Objects;

public class WalletRecord {
    private final StockSymbol stock;
    private Long amount;

    public WalletRecord(StockSymbol stock, Long amount) {
        this.stock = stock;
        this.amount = amount;
    }

    public StockSymbol getStock() {
        return stock;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public void add(Long amount) {
        this.amount += amount;
    }

    public void sub(Long amount) {
        this.amount -= amount;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof WalletRecord walletRecord)) {
            return false;
        }

        return Objects.equals(amount, walletRecord.getAmount()) &&
                Objects.equals(stock, walletRecord.getStock());
    }

    public String toString() {
        return "WalletRecord{" + "stock=" + stock + ", amount=" + amount + '}';
    }
}

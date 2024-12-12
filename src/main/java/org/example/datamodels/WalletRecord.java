package org.example.datamodels;

public class WalletRecord {
    private final StockId stock;
    private Long amount;

    public WalletRecord(StockId stock, Long amount) {
        this.stock = stock;
        this.amount = amount;
    }
    public StockId getStock() {
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
}

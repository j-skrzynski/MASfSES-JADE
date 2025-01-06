package org.example.datamodels;

public record TransactionResult(
        Double toPay,
        Double toWithdraw,
        Long soldStock,
        Long boughtStock,
        String shortName,
        String brokerOrderId
) {
    public TransactionResult(
            Double toPay,
            Double toWithdraw,
            Long soldStock,
            Long boughtStock,
            String shortName,
            String brokerOrderId
    ) {
        this.toPay = toPay;
        this.toWithdraw = toWithdraw;
        this.soldStock = soldStock;
        this.boughtStock = boughtStock;
        this.shortName = shortName;
        this.brokerOrderId = brokerOrderId;
    }
}

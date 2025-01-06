package org.example.logic.broker;


import org.example.datamodels.StockSymbol;
import org.example.datamodels.TransactionResult;
import org.example.datamodels.WalletRecord;
import org.example.datamodels.order.OrderType;
import org.example.global.StockPriceDictionary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.example.global.StockDictionary.getStockIdByShortName;

public class InvestorAccount {
    private final HashMap<String, WalletRecord> stocks;
    private final HashMap<String, InvestorOrderRecord> currentOrders;
    private Double balance;

    public InvestorAccount() {
        stocks = new HashMap<>();
        balance = 0.0;
        currentOrders = new HashMap<>();
    }

    private Long getCurrentStockBalance(String shortName) {
        if (stocks.containsKey(shortName)) {
            return stocks.get(shortName).getAmount();
        }
        return 0L;
    }

    private Double calculateRequiredMoney(InvestorRequest req) {
        if (req.getAction() == OrderType.BUY) {
            if (req.isLimitless()) {
                Double lastUnitPriceOfStock = StockPriceDictionary.getPrice(
                        req.getShortName(),
                        req.getStockExchangeName()
                );
                return lastUnitPriceOfStock * req.getAmount() * 1.15;
            } else {
                return req.getPrice() * req.getAmount();
            }
        }
        return 0.0;
    }

    private void addStockToWallet(StockSymbol stockId, Long amount) {
        //iff greater than 0
        if (amount > 0) {
            if (stocks.containsKey(stockId.getShortName())) {
                stocks.get(stockId.getShortName()).add(amount);
            } else {
                stocks.put(stockId.getShortName(), new WalletRecord(stockId, amount));
            }
        }
    }


    public String placeOrder(InvestorRequest req/* tutaj coś co bardziej jest życzeniem*/) {
        String orderId = UUID.randomUUID().toString();


        StockSymbol stock = getStockIdByShortName(req.getShortName());
        long requestedStockAmount = req.getAction() == OrderType.SELL ? req.getAmount() : 0;
        Long currentStockAmount = getCurrentStockBalance(stock.getShortName());
        Double requiredMoney = calculateRequiredMoney(req); //if no limit then calculate another way
        Double currentMoney = this.balance;

        if (requiredMoney > currentMoney) {
            throw new RuntimeException("Required money is greater than current money");
        }
        if (requestedStockAmount > currentStockAmount) {
            throw new RuntimeException("Required stock amount is greater than current stock amount");
        }

        if (requestedStockAmount > 0) {
            this.stocks.get(stock.getShortName()).sub(requestedStockAmount);
        }

        InvestorOrderRecord rec = new InvestorOrderRecord(req, orderId, requiredMoney); /*konwersja z życzenia i dodajemy OrderId*/

        this.balance -= requiredMoney;

        currentOrders.put(orderId, rec);
        return orderId;
    }

    public void processTransactionResult(TransactionResult tr) {
        String orderId = tr.brokerOrderId();
        if (!currentOrders.containsKey(orderId)) {
            throw new RuntimeException("Order not found");
        }
        InvestorOrderRecord orderRecord = currentOrders.get(orderId);
        orderRecord.payedMoney(tr.toPay());
        orderRecord.soldStock(tr.soldStock());
        orderRecord.boughtStock(tr.boughtStock());
        this.addStockToWallet(orderRecord.getStockSymbol(), tr.boughtStock());

    }

    public void cancelOrder(String orderId) {
        if (!currentOrders.containsKey(orderId)) {
            throw new RuntimeException("Order not found");
        }
        InvestorOrderRecord orderRecord = currentOrders.get(orderId);
        Double returnedMoney = orderRecord.getMoneyLocked();
        Long returnedShares = orderRecord.getAmountOfStockToBeSold();
        StockSymbol stockId = orderRecord.getStockSymbol();

        balance += returnedMoney;
        addStockToWallet(stockId, returnedShares);
    }

    public void addMoney(Double amount) {
        balance += amount;
    }

    public void withdrawMoney(Double amount) {
        balance -= amount;
    }

    public List<WalletRecord> getWallet() {
        return new ArrayList<>(stocks.values());
    }

    public Double getBalance() {
        return balance;
    }

    public void addStockToAccount(StockSymbol stockId, Long amount) {
        if (amount < 0) {
            throw new RuntimeException("Stock amount is less than zero");
        }
        if (stocks.containsKey(stockId.getShortName())) {
            stocks.get(stockId.toString()).add(amount);
        } else {
            stocks.put(stockId.getShortName(), new WalletRecord(stockId, amount));
        }
    }
}

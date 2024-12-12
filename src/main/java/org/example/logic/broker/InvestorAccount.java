package org.example.logic.broker;

import org.example.datamodels.StockId;
import org.example.datamodels.TransactionResult;
import org.example.datamodels.WalletRecord;

import java.util.HashMap;
import java.util.UUID;

public class InvestorAccount {
    private HashMap<String, WalletRecord> stocks;
    private Double balance;
    private HashMap<String, InvestorOrderRecord> currentOrders;

    private Long getCurrentStockBalance(String shortName){

    }

    private Double calculateRequiredMoney(InvestorRequest req){

    }





    public void placeOrder(InvestorRequest req/* tutaj coś co bardziej jest życzeniem*/){
        String orderId = UUID.randomUUID().toString();

        InvestorOrderRecord rec = /*konwersja z życzenia i dodajemy OrderId*/ null;
        StockId stock;
        Long requestedStockAmount;
        Long currentStockAmount = getCurrentStockBalance(stock.shortName());
        Double requiredMoney = calculateRequiredMoney(req); //if no limit then calculate another way
        Double currentMoney = this.balance;

        if(requiredMoney > currentMoney){
            throw new RuntimeException("Required money is greater than current money");
        }
        if(requestedStockAmount > currentStockAmount){
            throw new RuntimeException("Required stock amount is greater than current stock amount");
        }

        if(requestedStockAmount > 0){
            this.stocks.get(stock.shortName()).sub(requestedStockAmount);
        }

        this.balance-=requiredMoney;

        currentOrders.put(orderId, rec);

    }

    public void processTransactionResult(TransactionResult tr){

    }

    public void cancelOrder(String orderId){

    }

    public void addMoney(){

    }

    public void withdrawMoney(){

    }
}

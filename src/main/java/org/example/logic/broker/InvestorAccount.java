package org.example.logic.broker;

import jade.core.AID;
import org.example.datamodels.StockId;
import org.example.datamodels.TransactionResult;
import org.example.datamodels.WalletRecord;

import java.util.HashMap;
import java.util.List;

public class InvestorAccount {
    private String investorName;
    private AID jadeAgentAddressee;
    private Double balance;
    private HashMap<String, Double> lockedBalance; // orderId / amount blocked
    private HashMap<String,WalletRecord> walletRecords; // short name / record
    private HashMap<String,WalletRecord> lockedRecords;// short name / record

    private void lockBalance(String orderId, Double amount) {
        if (this.balance < amount) {
            throw new RuntimeException("Insufficient balance to lock");
        }
        this.balance -= amount;
        this.lockedBalance.put(orderId,(Double) this.lockedBalance.getOrDefault(orderId, 0.0) + amount);
    }

    private void unlockBalance(String orderId){
        this.unlockBalance(orderId,false);
    }

    private void unlockBalance(String orderId, boolean pedantic) {
        Double lockedAmount = (Double) this.lockedBalance.getOrDefault(orderId, 0.0);
        if (lockedAmount == 0.0 && pedantic) {
            throw new RuntimeException("No balance locked for this order ID: " + orderId);
        }
        this.balance += lockedAmount;
        this.lockedBalance.remove(orderId);
    }

    private void pay(String orderId, Double amount){
        pay( orderId,  amount, false);
    }
    private void pay(String orderId, Double amount, boolean pedantic) {
        Double lockedAmount = (Double) this.lockedBalance.getOrDefault(orderId, 0.0);
        if (lockedAmount < amount) {
            this.lockedBalance.remove(orderId);
            balance-=amount-lockedAmount;
            if(pedantic) {
                throw new RuntimeException("Insufficient locked balance for order ID: " + orderId);
            }
        }
        else {
            this.lockedBalance.put(orderId, lockedAmount - amount);
            if ((Double) this.lockedBalance.get(orderId) == 0.0) {
                this.lockedBalance.remove(orderId);
            }
        }
    }


    private void receive(Double amount) {
        this.balance += amount;
    }

    private static StockId getStockIdFromShortName(String shortName) {
        //TODO
        return null;
    }

    private void lockStock(String shortName, Long amount) {
        if (!this.walletRecords.containsKey(shortName)) {
            throw new RuntimeException("Insufficient stock to lock: " + shortName);
        }

        WalletRecord record = this.walletRecords.get(shortName);
        if (record.getAmount() < amount) {
            throw new RuntimeException("Insufficient stock amount to lock for " + shortName);
        }

        record.add(-amount);

        if (this.lockedRecords.containsKey(shortName)) {
            this.lockedRecords.get(shortName).add(amount);
        } else {
            WalletRecord lockedRecord = new WalletRecord(getStockIdFromShortName(shortName), amount);
            this.lockedRecords.put(shortName, lockedRecord);
        }

        if (record.getAmount() == 0) {
            this.walletRecords.remove(shortName);
        }
    }

    private void receiveStock(String shortName, Long amount){
        if (this.walletRecords.containsKey(shortName)) {
            this.walletRecords.get(shortName).add(amount);
        }
        else {
            WalletRecord record = new WalletRecord(this.getStockIdFromShortName(shortName), amount);
            this.walletRecords.put(shortName, record);
        }
    }

    private void payStock(String shortName, Long amount){
        /*
        Pojedyncza logika - zawsze wiemy ile akcji sprzedajemy i zawsze musi być tyle zablokowane
         */
        if (!this.lockedRecords.containsKey(shortName)) {
            throw new RuntimeException("Insufficient stock to sell: " + shortName);
        }

        WalletRecord record = this.lockedRecords.get(shortName);
        if (record.getAmount() < amount) {
            throw new RuntimeException("Insufficient stock amount for " + shortName);
        }

        record.add(-amount);

        if (record.getAmount() == 0) {
            this.lockedRecords.remove(shortName);
        }
    }

    public void processResult(TransactionResult result) {
        pay(result.brokerOrderId(),result.toPay());
        receive(result.toWithdraw());

        String stockShortName = result.shortName();
        receiveStock(stockShortName,result.boughtStock());
        payStock(stockShortName,result.soldStock());
    }

    public void deposit(Double amount) {
        this.balance += amount;
    }

    public void withdraw(Double amount) {
        if(this.balance < amount) {
            throw new RuntimeException("Insufficient balance to withdraw");
        }
        this.balance -= amount;
    }


    //todo unlock po tym jak wykona się zlecenie i otrzymamy raport zdjęcia z giełdy
}

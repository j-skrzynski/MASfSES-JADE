package org.example.logic.broker;

import jade.core.AID;
import org.example.datamodels.StockId;
import org.example.datamodels.TransactionResult;
import org.example.datamodels.WalletRecord;
import org.example.global.StockDictionary;

import java.util.HashMap;

public class InvestorAccountv1 {
    private String investorName;
    private AID jadeAgentAddressee;
    private Double balance;
    private HashMap<String, Double> lockedBalance; // orderId / amount blocked
    private HashMap<String,WalletRecord> walletRecords; // short name / record
    private HashMap<String,WalletRecord> lockedRecords;// orderId / record

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

    private static StockId getStockIdFromShortName(String shortName)     {
        return StockDictionary.getStockIdByShortName(shortName);
        /*
        * This is just a workaround. Proper way would be that it should ask all known exchanges in a cyclic behaviour to have
        * them provide list of symbols they know, to make exchanges a decentralised database
        * */
    }

    private void lockStock(String shortName, Long amount, String orderId) {
        if (!this.walletRecords.containsKey(shortName)) {
            throw new RuntimeException("Insufficient stock to lock: " + shortName);
        }

        WalletRecord record = this.walletRecords.get(shortName);
        if (record.getAmount() < amount) {
            throw new RuntimeException("Insufficient stock amount to lock for " + shortName);
        }

        record.add(-amount);

        if (this.lockedRecords.containsKey(orderId)) {
            throw new RuntimeException("Lock already exists for order " + orderId);
        } else {
            WalletRecord lockedRecord = new WalletRecord(getStockIdFromShortName(shortName), amount);
            this.lockedRecords.put(orderId, lockedRecord);
        }

        if (record.getAmount() == 0) {
            this.walletRecords.remove(shortName);
        }
    }
    private void unlockStock(String orderId, Long amount) {
        // Sprawdź, czy istnieją zablokowane akcje dla podanej krótkiej nazwy
        if (!this.lockedRecords.containsKey(orderId)) {
            throw new RuntimeException("No locked stock found for order: " + orderId);
        }

        WalletRecord lockedRecord = this.lockedRecords.get(orderId);

        // Sprawdź, czy ilość do odblokowania nie przekracza dostępnych zablokowanych akcji
        if (lockedRecord.getAmount() < amount) {
            throw new RuntimeException("Insufficient locked stock for " + lockedRecord.getStock().shortName() + ": requested " + amount + ", available " + lockedRecord.getAmount());
        }

        // Zmniejsz ilość zablokowanych akcji
        lockedRecord.add(-amount);

        // Jeśli wszystkie akcje zostały odblokowane, usuń wpis z lockedRecords
        if (lockedRecord.getAmount() == 0) {
            this.lockedRecords.remove(orderId);
        }

        // Dodaj odblokowane akcje z powrotem do portfela
        this.addStockToWallet(lockedRecord.getStock(), amount);
    }

    private void addStockToWallet(StockId stock, Long amount){
        if (this.walletRecords.containsKey(stock.shortName())) {
            this.walletRecords.get(stock.shortName()).add(amount);
        }
        else {
            WalletRecord record = new WalletRecord(stock, amount);
            this.walletRecords.put(stock.shortName(), record);
        }
    }
    private void receiveStock(String shortName, Long amount){
        this.addStockToWallet(getStockIdFromShortName(shortName), amount);
        //maybe log
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

    public void notifyOrderCompletion(String OrderId){
        //jak zlecenie się wykonało; trezeba zdjąć wszelakie blokady z gotówki i akcji
        Double money = (Double) this.lockedBalance.get(OrderId);
        Long stock = (Long) this.lockedRecords.get(OrderId).getAmount();

        this.unlockBalance(OrderId);
        this.unlockStock(OrderId,stock);
    }

    public void notifyOrderCanceled(String OrderId, Long unprocessedShares){
        //zdejmij blokadę ze akcji, jeśli ilość zablokowanych inna niz te zwrócone jako nieprzetworzone to odblokuj tylko te zwrócone
        Double balanceToUnlock = this.lockedBalance.getOrDefault(OrderId,0.0);
        this.lockedBalance.remove(OrderId);
        this.balance+=balanceToUnlock;
        if(this.lockedRecords.containsKey(OrderId)){
            Long remainingStock = this.lockedRecords.get(OrderId).getAmount();
            Long stockToUnlock = Math.min(remainingStock,unprocessedShares);
            this.unlockStock(OrderId, stockToUnlock);
        }
    }
}

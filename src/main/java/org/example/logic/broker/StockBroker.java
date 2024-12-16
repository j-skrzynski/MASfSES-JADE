package org.example.logic.broker;

import org.example.datamodels.TransactionResult;
import org.example.datamodels.WalletRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StockBroker {

    public HashMap<String, InvestorAccount> accounts;
    public List<String> supportedStockMarketsNames;

    public StockBroker() {
        accounts = new HashMap<>();
        supportedStockMarketsNames = new ArrayList<>();
    }

    public void registerTrader(String name){
        InvestorAccount investorAccount = new InvestorAccount();
        if(accounts.containsKey(name)){
            throw new RuntimeException("Trader already exists");
        }
        accounts.put(name, investorAccount);
    }

    public List<WalletRecord> getInvestorPortfolio(String name) {
         if(!accounts.containsKey(name)){
             throw new RuntimeException("Trader does not exist");
         }
         return accounts.get(name).getWallet();
    }

    public Double getMoneyBalance(String name) {
        if(!accounts.containsKey(name)){
            throw new RuntimeException("Trader does not exist");
        }
        return accounts.get(name).getBalance();
    }

    public void deposit(String name, Double amount) {
        if(!accounts.containsKey(name)){
            throw new RuntimeException("Trader does not exist");
        }
        accounts.get(name).addMoney(amount);
    }

    public void withdraw(String name, Double amount) {
        accounts.get(name).withdrawMoney(amount);
    }

    public void placeOrder(String name, InvestorRequest req) {
        if (!accounts.containsKey(name)) {
            throw new RuntimeException("Trader does not exist");
        }
        accounts.get(name).placeOrder(req);
    }

    public void cancelOrder(String name, String orderId) {
        if (!accounts.containsKey(name)) {
            throw new RuntimeException("Trader does not exist");
        }
        accounts.get(name).cancelOrder(orderId);
    }

    public void notifyOnSettlement(String name, TransactionResult tr) {
        if (!accounts.containsKey(name)) {
            throw new RuntimeException("Trader does not exist");
        }
        accounts.get(name).processTransactionResult(tr);
    }
}

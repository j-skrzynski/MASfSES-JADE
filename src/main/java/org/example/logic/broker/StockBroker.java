package org.example.logic.broker;

import jade.core.AID;
import org.example.datamodels.TransactionResult;
import org.example.datamodels.WalletRecord;
import org.example.global.StockDictionary;

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

    public void registerTrader(String name) {
        InvestorAccount investorAccount = new InvestorAccount(name);
        if (accounts.containsKey(name)) {
            throw new RuntimeException("Trader already exists");
        }
        accounts.put(name, investorAccount);
    }

    public List<WalletRecord> getInvestorPortfolio(String name) {
        if (!accounts.containsKey(name)) {
            throw new RuntimeException("Trader does not exist");
        }
        return accounts.get(name).getWallet();
    }

    public Double getMoneyBalance(String name) {
        if (!accounts.containsKey(name)) {
            throw new RuntimeException("Trader does not exist");
        }
        return accounts.get(name).getBalance();
    }

    public void deposit(String name, Double amount) {
        if (!accounts.containsKey(name)) {
            throw new RuntimeException("Trader does not exist");
        }
        accounts.get(name).addMoney(amount);
    }

    public void withdraw(String name, Double amount) {
        accounts.get(name).withdrawMoney(amount);
    }

    public String placeOrder(String name, InvestorRequest req) {
        if (!accounts.containsKey(name)) {
            throw new RuntimeException("Trader does not exist");
        }
        return accounts.get(name).placeOrder(req);
        //redirect order to stock exchange
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

    public AID getExchangeAddressee(String exchangeName) {
        if (this.supportedStockMarketsNames.contains(exchangeName)) {
            return new AID(exchangeName, AID.ISLOCALNAME);
        } else {
            throw new RuntimeException("Specified stock market is not supported by this broker");
        }
    }

    public void addStockExchange(String exchangeName) {
        this.supportedStockMarketsNames.add(exchangeName);
    }

    public void addStockToAccount(String name, String shortName, Long amount) {
        if (!accounts.containsKey(name)) {
            throw new RuntimeException("Trader does not exist");
        }
        accounts.get(name).addStockToAccount(StockDictionary.getStockIdByShortName(shortName), amount);
    }
}

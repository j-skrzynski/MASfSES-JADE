package org.example.visualization.viewmodels;

import org.example.datamodels.WalletRecord;
import org.example.logic.broker.InvestorOrderRecord;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public record TraderViewModel(Double[] balanceHistory,
                              HashMap<String, WalletRecord> stocks,
                              HashMap<String, InvestorOrderRecord> currentOrders) implements Comparable<TraderViewModel> {
    @Override
    public int compareTo(TraderViewModel otherModel) {
        if (otherModel == null) {
            return -1;
        }

        if (!Arrays.equals(balanceHistory, otherModel.balanceHistory()) ||
                areHashMapsDifferent(stocks, otherModel.stocks()) ||
                areHashMapsDifferent(currentOrders, otherModel.currentOrders())) {
            return 1;
        }

        return 0;
    }

    private <T> boolean areHashMapsDifferent(HashMap<String, T> lhs, HashMap<String, T> rhs) {
        if (lhs.size() != rhs.size()) {
            return true;
        }

        return !lhs.keySet().stream().allMatch(key -> compareEntries(lhs, rhs, key));
    }

    private <T> boolean compareEntries(HashMap<String, T> lhs, HashMap<String, T> rhs, String key) {
        return lhs.containsKey(key) && rhs.containsKey(key) && Objects.equals(lhs.get(key), rhs.get(key));
    }
}

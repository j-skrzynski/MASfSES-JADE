package org.example.logic.stockexchange.order.awaitingorder;

import java.util.Comparator;

public class AwaitingExchangeOrderComparatorDescending implements Comparator<AwaitingExchangeOrder> {

    @Override
    public int compare(AwaitingExchangeOrder o1, AwaitingExchangeOrder o2) {
        return Double.compare(o2.getActivationPrice(), o1.getActivationPrice());
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }
}

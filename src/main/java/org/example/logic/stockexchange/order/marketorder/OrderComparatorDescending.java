package org.example.logic.stockexchange.order.marketorder;

import org.example.datamodels.order.Order;

import java.util.Comparator;

public class OrderComparatorDescending implements Comparator<ExchangeOrder> {

    @Override
    public int compare(ExchangeOrder o1, ExchangeOrder o2) {
        int priceComparison = Order.compare(o2, o1);
        if (priceComparison != 0) {
            return priceComparison;
        }
        return o1.getSeqId().compareTo(o2.getSeqId());
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }
}
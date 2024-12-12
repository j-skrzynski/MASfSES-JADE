package org.example.logic.stockexchange.order;

import java.util.Comparator;

public class OrderComparatorDescending implements Comparator<Order> {

    @Override
    public int compare(Order o1, Order o2) {
        int priceComparison = Double.compare(o2.getPrice(), o1.getPrice());
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
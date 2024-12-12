package org.example.logic.stockexchange.order;

import java.util.Comparator;

public class OrderComparatorAscending implements Comparator<Order> {

    @Override
    public int compare(Order o1, Order o2) {
        int priceComparison = Double.compare(o1.getPrice(), o2.getPrice());
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

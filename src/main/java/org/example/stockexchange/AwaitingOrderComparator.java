package org.example.stockexchange;

import java.util.Comparator;

public class AwaitingOrderComparator implements Comparator<AwaitingOrder> {

    @Override
    public int compare(AwaitingOrder o1, AwaitingOrder o2) {
        return Double.compare(o1.getActivationPrice(), o2.getActivationPrice());
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    @Override
    public Comparator<AwaitingOrder> reversed() {
        return Comparator.super.reversed();
    }
}

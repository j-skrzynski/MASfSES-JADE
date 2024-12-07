package org.example.stockexchange;

import java.util.Comparator;

public class AwaitingOrderComparator implements Comparator<AwaitingOrder> {

    @Override
    public int compare(AwaitingOrder o1, AwaitingOrder o2) {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }

    @Override
    public Comparator<AwaitingOrder> reversed() {
        return Comparator.super.reversed();
    }
}

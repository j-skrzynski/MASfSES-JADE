package org.example.stockexchange;

import java.util.Comparator;

public class OrderComparator implements Comparator<Order> {

    @Override
    public int compare(Order o1, Order o2) {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }

    @Override
    public Comparator<Order> reversed() {
        return Comparator.super.reversed();
    }
}

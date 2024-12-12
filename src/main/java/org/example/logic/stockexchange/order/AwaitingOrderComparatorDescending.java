package org.example.logic.stockexchange.order;

import java.util.Comparator;

public class AwaitingOrderComparatorDescending implements Comparator<AwaitingOrder> {

    @Override
    public int compare(AwaitingOrder o1, AwaitingOrder o2) {
        return Double.compare(o2.getActivationPrice(), o1.getActivationPrice());
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

}

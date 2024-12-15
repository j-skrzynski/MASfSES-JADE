package org.example.logic.stockexchange.order.awaitingorder;

import java.util.Comparator;

public class AwaitingExchangeOrderComparatorAscending implements Comparator<AwaitingExchangeOrder> {

    @Override
    public int compare(AwaitingExchangeOrder o1, AwaitingExchangeOrder o2) {
        return Double.compare(o1.getActivationPrice(), o2.getActivationPrice());
//        if (cmp != 0){
//            return cmp;
//        }
//        return Double.compare(o1.getActivatedOrder().
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

}

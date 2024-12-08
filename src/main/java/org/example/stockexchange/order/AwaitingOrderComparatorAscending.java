package org.example.stockexchange.order;

import java.util.Comparator;

public class AwaitingOrderComparatorAscending implements Comparator<AwaitingOrder> {

    @Override
    public int compare(AwaitingOrder o1, AwaitingOrder o2) {
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

package org.example.stockexchange;

import org.example.stockexchange.orders.Order;
import org.example.stockexchange.orders.gpw.GPWOrderLIMIT;

public class GPWOrderSpreadsheet extends OrderSpreadsheet {

    @Override
    public void submitOrder(Order order) {
        if (order.getClass() == GPWOrderLIMIT.class) { // Zlecenie to LIMIT
            GPWOrderLIMIT limit = (GPWOrderLIMIT) order;
        }
    }
}

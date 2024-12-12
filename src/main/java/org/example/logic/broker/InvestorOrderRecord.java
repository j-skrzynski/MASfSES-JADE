package org.example.logic.broker;

import org.example.datamodels.StockId;

public class InvestorOrderRecord {
    /*
    * Tu nie rozrózniamy czy jest awaiting czy nie bo w zasadzier to nas nie interesi tutaj, w środku awaiting jest i
    * tak zwykłe zlecenie które może się uwolnic albo nie
    * */

    private String orderId;
    private StockId stockId;
    private Long amountToBeTraded;
    private Long moneyLocked;
    private boolean limit;
    private OrderAction action;
}

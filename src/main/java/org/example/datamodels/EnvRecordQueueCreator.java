package org.example.datamodels;

import org.example.logic.stockexchange.utils.EnvRecord;

import java.util.LinkedList;
import java.util.Queue;

public class EnvRecordQueueCreator {
    public static Queue<EnvRecord> getQueueFromPriceList(String shortName, Queue<Double> prices, Long amount) {
        Queue<EnvRecord> queue = new LinkedList<EnvRecord>();
        for (Double price : prices) {
            queue.add(new EnvRecord(shortName, price, amount));
        }
        return queue;
    }
}

package org.example.datamodels;

import org.example.logic.stockexchange.utils.EnvRecord;

import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.Collectors;

public class EnvRecordQueueCreator {
    public static Queue<EnvRecord> getQueueFromPriceList(
            String shortName,
            Queue<Double> prices,
            Long amount
    ) {
        return prices.stream()
                .map(price -> new EnvRecord(shortName, price, amount))
                .collect(Collectors.toCollection(LinkedList::new));
    }
}

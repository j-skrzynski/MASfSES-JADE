package org.example.stockexchange.utils;

public class ExchangeDate implements Comparable<ExchangeDate> {

    public boolean isAfter(ExchangeDate other) {
        return compareTo(other) > 0;
    }
    public boolean isAfterOrEqual(ExchangeDate other) {
        return isAfter(other) || isEqual(other);
    }
    public boolean isEqual(ExchangeDate other) {
        return compareTo(other) == 0;
    }

    public boolean isBefore(ExchangeDate other) {
        return compareTo(other) < 0;
    }
    public boolean isBeforeOrEqual(ExchangeDate other) {
        return isBefore(other) || isEqual(other);
    }

    @Override
    public int compareTo(ExchangeDate o) {
        return 0;//todo
    }
}

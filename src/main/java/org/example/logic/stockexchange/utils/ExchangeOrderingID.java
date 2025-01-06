package org.example.logic.stockexchange.utils;

public class ExchangeOrderingID implements Comparable<ExchangeOrderingID> {
    private final Long id;

    public ExchangeOrderingID(Long id) {
        this.id = id;
    }

    public ExchangeOrderingID() {
        id = 0L;
    }

    public static ExchangeOrderingID getZero() {
        return new ExchangeOrderingID(0L);
    }

    public ExchangeOrderingID next() {
        return new ExchangeOrderingID(id + 1L);
    }

    public Long getId() {
        return id;
    }

    @Override
    public int compareTo(ExchangeOrderingID o) {
        return Long.compare(this.id, o.id);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ExchangeOrderingID) {
            return compareTo((ExchangeOrderingID) obj) == 0;
        }
        return false;
    }
}

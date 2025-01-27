package org.example.logic.stockexchange.utils;

public class ExchangeDate implements Comparable<ExchangeDate> {

    private Long sessionId;
    private Long milliseconds;

    public ExchangeDate(Long sessionId, Long milliseconds) {
        this.sessionId = sessionId;
        this.milliseconds = milliseconds;
    }

    public ExchangeDate() {
        this(0L, 0L);
    }

    public ExchangeDate(Long sessionId) {
        this(sessionId, 0L);
    }

    public ExchangeDate(ExchangeDate d) {
        this(d.getSessionId(), d.getMilliseconds());
    }

    public void addMillisecondsSeconds(Long milliseconds) {
        this.milliseconds += milliseconds;
    }

    public void addSessions(Long sessions) {
        this.sessionId += sessions;
    }

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
        int cmp = sessionId.compareTo(o.sessionId);
        if (cmp != 0) return cmp;
        return milliseconds.compareTo(o.milliseconds);
    }

    public ExchangeDate getNexSessionDate() {
        return new ExchangeDate(sessionId + 1L);
    }

    public Long getSessionId() {
        return sessionId;
    }

    public Long getMilliseconds() {
        return milliseconds;
    }
}

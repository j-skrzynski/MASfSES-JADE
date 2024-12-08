package org.example.stockexchange.utils;

public class ExchangeDate implements Comparable<ExchangeDate> {

    private Long sessionId;
    private Long miliseconds;

    public ExchangeDate(Long sessionId, Long miliseconds) {
        this.sessionId = sessionId;
        this.miliseconds = miliseconds;
    }
    public ExchangeDate() {
        this(0L,0L);
    }
    public ExchangeDate(Long sessionId) {
        this(sessionId, 0L);
    }
    public ExchangeDate(ExchangeDate d) {
        this(d.getSessionId(), d.getMiliseconds());
    }

    public void addMilisecondsSeconds(Long miliseconds) {
        this.miliseconds += miliseconds;
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
        if(cmp != 0) return cmp;
        return miliseconds.compareTo(o.miliseconds);
    }

    public ExchangeDate getNexSessionDate() {
        return new ExchangeDate(sessionId+1L);
    }

    public Long getSessionId() {
        return sessionId;
    }

    public Long getMiliseconds() {
        return miliseconds;
    }
}

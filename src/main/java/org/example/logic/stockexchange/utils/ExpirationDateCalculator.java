package org.example.logic.stockexchange.utils;

public class ExpirationDateCalculator {
    public static ExchangeDate getWDADate(ExchangeDate currentSessionStart, Long sessionsTillEnd) {
        ExchangeDate result = new ExchangeDate(currentSessionStart.getSessionId());
        result.addSessions(sessionsTillEnd);
        return result;
    }

    /**
     * Oblicza datę dla zleceń typu D (na dzień bieżący).
     */
    public static ExchangeDate getDDate(ExchangeDate currentSessionStart) {
        return currentSessionStart.getNexSessionDate();
    }

    /**
     * Oblicza datę dla zleceń typu WDD (ważne do określonej liczby sesji).
     */
    public static ExchangeDate getWDDDate(
            ExchangeDate currentSessionStart,
            long sessionsCount,
            long sessionsTillYearEnd
    ) {
        ExchangeDate result = new ExchangeDate(currentSessionStart.getSessionId());
        result.addSessions(Math.min(sessionsCount, sessionsTillYearEnd));
        return result;
    }

    /**
     * Oblicza datę dla zleceń typu WDA (ważne do końca roku).
     */
    public static ExchangeDate getWDADate(ExchangeDate currentSessionStart, long sessionsTillYearEnd) {
        ExchangeDate result = new ExchangeDate(currentSessionStart.getSessionId());
        result.addSessions(sessionsTillYearEnd);
        return result;
    }

    /**
     * Oblicza datę dla zleceń typu WDC (ważne do określonej liczby sesji i sekund w ostatniej sesji).
     */
    public static ExchangeDate getWDCDate(
            ExchangeDate currentSessionStart,
            long sessionsCount,
            long millisecondsInLastSession
    ) {
        ExchangeDate result = new ExchangeDate(currentSessionStart.getSessionId(), currentSessionStart.getMilliseconds());
        result.addSessions(sessionsCount);
        result.addMillisecondsSeconds(millisecondsInLastSession);
        return result;
    }

//    /**
//     * Oblicza datę dla zleceń typu WNF (ważne na fixing).
//     */
//    public static ExchangeDate getWNFDate(ExchangeDate currentSessionStart) {
//
//
//    }
//
//    /**
//     * Oblicza datę dla zleceń typu WNZ (ważne na zamknięcie).
//     */
//    public static ExchangeDate getWNZDate(ExchangeDate currentSessionStart) {
//
//    }
}

package org.example.datamodels.order;


import org.example.logic.stockexchange.utils.ExchangeDate;

public enum OrderExpirationType {
    D("Ważne na dzień bieżący (D)"),
    WDD("Ważne do określonego dnia (WDD)"), // wymaga popdania na ile sesji ważne
    WDA("Ważne na czas nieoznaczony (WDA)"),// ważne do końca roku
    WDC("Ważne do określonego czasu (WDC)"),//pobiera ile sesji i ile sekund w ostatniej sesji
    WNF("Ważne na fixing (WNF)"),
    WNZ("Ważne na zamknięcie (WNZ)");

    private final String description;

    OrderExpirationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
    public static OrderExpirationType fromString(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        try {
            return OrderExpirationType.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid OrderExpirationType: " + input);
        }
    }
}

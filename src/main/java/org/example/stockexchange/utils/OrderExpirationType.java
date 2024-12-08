package org.example.stockexchange.utils;


public enum OrderExpirationType {
    D("Ważne na dzień bieżący (D)"),
    WDD("Ważne do określonego dnia (WDD)"),
    WDA("Ważne na czas nieoznaczony (WDA)"),
    WDC("Ważne do określonego czasu (WDC)"),
    WNF("Ważne na fixing (WNF)"),
    WNZ("Ważne na zamknięcie (WNZ)");

    private final String description;

    OrderExpirationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

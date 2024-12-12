package org.example.logic.stockexchange.utils;

public enum OrderType{
    BUY,SELL;

    public static OrderType fromString(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        try {
            return OrderType.valueOf(input.toUpperCase()); // Ignoruje wielkość liter
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid OrderType: " + input);
        }
    }
}

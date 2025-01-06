package org.example.datamodels.order;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class OrderParser {
    private final Gson gson;

    public OrderParser() {
        this.gson = new Gson();
    }



    // Parsowanie AwaitingOrder
    public AwaitingOrder parseAwaitingOrder(JsonObject argumentsJson) throws IllegalArgumentException {
        try {
            JsonObject orderJson = argumentsJson.getAsJsonObject("order");
            Order order = parseOrder(orderJson);
            if (order.getPrice() == null || order.getPrice().compareTo(0.0) == 0){
                order.setHasLimit(false);
            }
            else{
                order.setHasLimit(true);
            }

            boolean awaiting = argumentsJson.get("awaiting").getAsBoolean();
            Double price = argumentsJson.get("price").getAsDouble();

            return new AwaitingOrder(order, awaiting, price);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid AwaitingOrder JSON: " + e.getMessage(), e);
        }
    }

    // Parsowanie Order
    public Order parseOrder(JsonObject orderJson) throws IllegalArgumentException {
        try {
            return gson.fromJson(orderJson, Order.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid Order JSON: " + e.getMessage(), e);
        }
    }

}

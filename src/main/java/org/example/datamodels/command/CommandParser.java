package org.example.datamodels.command;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.example.datamodels.TransactionResult;
import org.example.datamodels.order.AwaitingOrder;
import org.example.datamodels.order.OrderParser;

import java.util.ArrayList;
import java.util.List;

public class CommandParser {

    private final Gson gson = new Gson();

    // Parsowanie całej komendy
    public Command parseCommand(String json) throws IllegalArgumentException {
        try {
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            String command = root.get("command").getAsString();
            String stockExchangeName = root.get("exchangeName").getAsString();
            JsonArray arguments = root.getAsJsonArray("arguments");
            String traderName = root.get("traderName").getAsString();
            String brokerName = root.get("traderName").getAsString();
            String brokerOrderId = root.get("brokerOrderId").getAsString();

            List<Object> argumentsList = parseArguments(arguments);
            return new Command(
                    command,
                    stockExchangeName,
                    argumentsList,
                    traderName,
                    brokerName,
                    stockExchangeName,
                    brokerOrderId
            );
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON command: " + e.getMessage(), e);
        }
    }

    private List<Object> parseArguments(JsonArray argumentsJson) {
        List<Object> argumentsList = new ArrayList<>();

        // Iterowanie po każdym elemencie w tablicy arguments
        for (var element : argumentsJson) {
            if (element.isJsonObject()) {
                JsonObject jsonObject = element.getAsJsonObject();

                // Sprawdzamy, czy jest to AwaitingOrder
                if (jsonObject.has("order") && jsonObject.has("awaiting")) {
                    OrderParser op = new OrderParser();
                    AwaitingOrder awaitingOrder = op.parseAwaitingOrder(jsonObject);
                    argumentsList.add(awaitingOrder);
                }
                // Sprawdzamy, czy jest to TransactionResult
                else if (isTransactionResult(jsonObject)) {
                    TransactionResult transactionResult = parseTransactionResult(jsonObject);
                    argumentsList.add(transactionResult);
                } else {
                    // Inny obiekt JSON, traktujemy go ogólnie
                    argumentsList.add(gson.fromJson(jsonObject, Object.class));
                }
            } else if (element.isJsonArray()) {
                // Jeśli to tablica JSON, rekursywnie ją parsujemy
                argumentsList.add(parseArguments(element.getAsJsonArray()));
            } else if (element.isJsonPrimitive()) {
                // Jeśli to prymityw (string, number, boolean) - traktujemy konkretne typy
                if (element.getAsJsonPrimitive().isString()) {
                    argumentsList.add(element.getAsString());
                } else if (element.getAsJsonPrimitive().isNumber()) {
                    try {
                        argumentsList.add(element.getAsDouble());
                    } catch (NumberFormatException e) {
                        try {
                            argumentsList.add(element.getAsLong());
                        } catch (NumberFormatException ex) {
                            argumentsList.add(element.getAsInt());
                        }
                    }
                } else if (element.getAsJsonPrimitive().isBoolean()) {
                    argumentsList.add(element.getAsBoolean());
                }
            }
        }

        return argumentsList;
    }

    // Sprawdzenie, czy JsonObject ma pola charakterystyczne dla TransactionResult
    private boolean isTransactionResult(JsonObject jsonObject) {
        return jsonObject.has("toPay") && jsonObject.has("toWithdraw") &&
                jsonObject.has("soldStock") && jsonObject.has("boughtStock") &&
                jsonObject.has("shortName") && jsonObject.has("brokerOrderId");
    }

    // Parsowanie TransactionResult z JsonObject
    private TransactionResult parseTransactionResult(JsonObject jsonObject) {
        Double toPay = jsonObject.get("toPay").getAsDouble();
        Double toWithdraw = jsonObject.get("toWithdraw").getAsDouble();
        Long soldStock = jsonObject.get("soldStock").getAsLong();
        Long boughtStock = jsonObject.get("boughtStock").getAsLong();
        String shortName = jsonObject.get("shortName").getAsString();
        String brokerOrderId = jsonObject.get("brokerOrderId").getAsString();

        return new TransactionResult(
                toPay,
                toWithdraw,
                soldStock,
                boughtStock,
                shortName,
                brokerOrderId
        );
    }


}

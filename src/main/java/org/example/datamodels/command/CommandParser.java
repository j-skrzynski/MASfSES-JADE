package org.example.datamodels.command;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.example.datamodels.order.AwaitingOrder;
import org.example.datamodels.order.OrderParser;

import java.util.ArrayList;
import java.util.List;

public class CommandParser {

    private final Gson gson;

    public CommandParser() {
        this.gson = new Gson();
    }

    // Parsowanie całej komendy
    public Command parseCommand(String json) throws IllegalArgumentException {
        try {
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            String command = root.get("command").getAsString();
            String stockExchangeName = root.get("exchangeName").getAsString();
            JsonArray arguments = root.getAsJsonArray("arguments");
            String traderName = root.get("traderName").getAsString();
            String brokerName = root.get("traderName").getAsString();


            List<Object> argumentsList = parseArguments(arguments);
            return new Command(command,stockExchangeName,argumentsList,traderName,brokerName,stockExchangeName);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON command: " + e.getMessage(), e);
        }
    }

    private List<Object> parseArguments(JsonArray argumentsJson) {
        List<Object> argumentsList = new ArrayList<>();

        // Iterowanie po każdym elemencie w tablicy arguments
        for (var element : argumentsJson.getAsJsonArray()) {
            if (element.isJsonObject()) {
                JsonObject jsonObject = element.getAsJsonObject();

                // Sprawdzamy, czy jest to AwaitingOrder
                if (jsonObject.has("order") && jsonObject.has("awaiting")) {
                    // Parsujemy jako AwaitingOrder
                    OrderParser op = new OrderParser();
                    AwaitingOrder awaitingOrder = op.parseAwaitingOrder(jsonObject);
                    argumentsList.add(awaitingOrder);
                } else {
                    // Inny obiekt JSON, traktujemy go ogólnie
                    argumentsList.add(gson.fromJson(jsonObject, Object.class));
                }
            } else if (element.isJsonArray()) {
                // Jeśli to tablica JSON, rekursywnie ją parsujemy
                argumentsList.add(parseArguments(element.getAsJsonArray()));
            } else if (element.isJsonPrimitive()) {
                // Jeśli to prymityw (string, number, boolean)
                argumentsList.add(element.getAsJsonPrimitive());
            }
        }

        return argumentsList;
    }

}

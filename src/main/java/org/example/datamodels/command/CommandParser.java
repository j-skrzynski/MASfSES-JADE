package org.example.datamodels.command;

import com.google.gson.Gson;
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
            String stockExchangeName = root.get("stockExchangeName").getAsString();
            JsonObject arguments = root.getAsJsonObject("arguments");
            String traderName = root.get("traderName").getAsString();
            String brokerName = root.get("traderName").getAsString();


            List<Object> argumentsList = parseArguments(arguments);
            return new Command(command,stockExchangeName,argumentsList,traderName,brokerName,stockExchangeName);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON command: " + e.getMessage(), e);
        }
    }

    private List<Object> parseArguments(JsonObject argumentsJson) {
        List<Object> argumentsList = new ArrayList<>();

        if (argumentsJson.has("order") && argumentsJson.has("awaiting")) {
            OrderParser op = new OrderParser();
            AwaitingOrder awaitingOrder = op.parseAwaitingOrder(argumentsJson);
            argumentsList.add(awaitingOrder);
        }

        // Możemy tu dodać kolejne typy argumentów, w zależności od komendy

        return argumentsList;
    }
}

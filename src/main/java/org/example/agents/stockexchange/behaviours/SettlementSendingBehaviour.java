package org.example.agents.stockexchange.behaviours;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import org.example.agents.stockexchange.StockExchangeAgent;
import org.example.datamodels.StockSymbol;
import org.example.logic.stockexchange.settlements.TransactionSettlement;

import java.util.logging.*;

public class SettlementSendingBehaviour extends TickerBehaviour {
    private static final Logger logger = Logger.getLogger(SettlementSendingBehaviour.class.getName());

    static {
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.INFO); // Log messages at INFO level or higher
        logger.addHandler(consoleHandler);
        try {
            FileHandler fileHandler = new FileHandler("OutgoingSettlementLog.log", true); // Append to the log file
            fileHandler.setFormatter(new SimpleFormatter()); // Add a simple text formatter
            logger.addHandler(fileHandler);
        } catch (Exception e) {
            logger.severe("Failed to set up file handler: " + e.getMessage());
        }

    }

    private final StockExchangeAgent agent;
    private final Gson gson = new Gson();

    public SettlementSendingBehaviour(StockExchangeAgent a, long period) {
        super(a, period);
        agent = a;
    }

    @Override
    protected void onTick() {
        broadcastSettlements();
    }

    private void broadcastSettlements() {
//        logger.info("["+agent.getStockExchange().getName()+"] broadcasting settlements");
        for (StockSymbol symbol : agent.getStockExchange().getAllStocksInExchange()) {
            TransactionSettlement settlement = agent.getStockExchange().popNextSettlement(symbol);

            while (settlement != null) {
                ACLMessage message = new ACLMessage(ACLMessage.INFORM);
                logger.info("[" + agent.getStockExchange().getName() + "] Sending " + settlement.toJson() +
                        " to " + settlement.getAddressee().getBroker());

                JsonObject commandObject = new JsonObject();
                commandObject.addProperty("command", "SETTLEMENT");
                commandObject.addProperty("exchangeName", agent.getStockExchange().getName());
                commandObject.addProperty("traderName", settlement.getAddressee().getSubmitterName());
                commandObject.addProperty("brokerOrderId", settlement.getAddressee().getBrokerOrderId());
                JsonArray jsonArray = new JsonArray();
                jsonArray.add(gson.toJsonTree(settlement.getTransactionResult()));
                commandObject.add("arguments", jsonArray);

                message.setContent(gson.toJson(commandObject));
                message.addReceiver(settlement.getAddressee().getBroker());
                agent.send(message);

                // Pobierz następne rozliczenie dla tego symbolu
                settlement = agent.getStockExchange().popNextSettlement(symbol);
            }
        }
    }
}

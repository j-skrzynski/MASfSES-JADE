package org.example.agents.stockexchange.behaviours;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import org.example.agents.stockexchange.StockExchangeAgent;
import org.example.datamodels.StockSymbol;
import org.example.logic.stockexchange.settlements.TransactionSettlement;
import org.example.logic.stockexchange.utils.OrderSubmitter;

import java.util.logging.*;


import java.util.logging.*;

public class CancelationSendingBehaviour extends TickerBehaviour {
    private static final Logger logger = Logger.getLogger(CancelationSendingBehaviour.class.getName());
    static{
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.OFF); // Log messages at INFO level or higher
        logger.addHandler(consoleHandler);
        logger.setUseParentHandlers(false);

        try {
            FileHandler fileHandler = new FileHandler("canceledtransactions.log", true); // Append to the log file
            fileHandler.setFormatter(new SimpleFormatter()); // Add a simple text formatter
            logger.addHandler(fileHandler);
        } catch (Exception e) {
            logger.severe("Failed to set up file handler: " + e.getMessage());
        }

    }

    private StockExchangeAgent agent;
    public CancelationSendingBehaviour(StockExchangeAgent a, long period) {
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
            OrderSubmitter adressee = agent.getStockExchange().popNextCancelationnotification(symbol);

            while (adressee != null) {
                ACLMessage message = new ACLMessage(ACLMessage.INFORM);
                logger.info("["+agent.getStockExchange().getName()+"] Sending cancelation of "+ adressee.getBrokerOrderId() + " to " + adressee.getBroker());


                JsonObject commandObject = new JsonObject();
                commandObject.addProperty("command", "CANCEL_ORDER");
                commandObject.addProperty("exchangeName", agent.getStockExchange().getName());
                commandObject.addProperty("traderName", adressee.getSubmitterName());
                commandObject.addProperty("brokerOrderId", adressee.getBrokerOrderId());
                commandObject.add("arguments", new JsonArray());

                // Dodajemy argumenty (w tym przypadku ID anulowanego zlecenia)
//                JsonArray arguments = new JsonArray();
//                arguments.add(brokerOrderId);
//                commandObject.add("arguments", arguments);

                Gson gson = new Gson();
                message.setContent(gson.toJson(commandObject));
                message.addReceiver(adressee.getBroker());
                agent.send(message);

                // Pobierz następne rozliczenie dla tego symbolu
                adressee = agent.getStockExchange().popNextCancelationnotification(symbol);
            }
        }
    }
}


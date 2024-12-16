package org.example.agents.broker;


import com.google.gson.Gson;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import org.example.agents.broker.behaviours.BrokerOrderProcessingBehaviour;
import org.example.datamodels.TransactionResult;
import org.example.datamodels.order.OrderType;
import org.example.logic.broker.InvestorRequest;
import org.example.logic.broker.StockBroker;

public class BrokerAgent extends Agent {

    private StockBroker stockBroker;

    @Override
    protected void setup() {
        // Inicjalizacja brokera
        stockBroker = new StockBroker();

        System.out.println("BrokerAgent " + getLocalName() + " started.");

        // Dodanie zachowania cyklicznego, które obsłuży wiadomości
//        addBehaviour(new );
        addBehaviour(new BrokerOrderProcessingBehaviour(this));
    }

    @Override
    protected void takeDown() {
        System.out.println("BrokerAgent " + getLocalName() + " terminating.");
    }

//    private void sendReply(ACLMessage msg, String content) {
//        ACLMessage reply = msg.createReply();
//        reply.setPerformative(ACLMessage.INFORM);
//        reply.setContent(content);
//        send(reply);
//    }

//    private InvestorRequest parseInvestorRequest(String[] parts) {
//        // Przykład parsowania wiadomości na InvestorRequest
//        String action = parts[2];
//        String shortName = parts[3];
//        long amount = Long.parseLong(parts[4]);
//        double price = Double.parseDouble(parts[5]);
//        boolean limitless = Boolean.parseBoolean(parts[6]);
//        String exchangeName = parts[7];
//
//        return new InvestorRequest(
//                amount,shortName,action.equalsIgnoreCase("BUY") ? OrderType.BUY : OrderType.SELL,price,limitless,exchangeName
//        );
//    }

    public StockBroker getStockBroker() {
        return stockBroker;
    }

    private TransactionResult parseTransactionResult(String json) {
        Gson gson = new Gson();
        TransactionResult result = gson.fromJson(json, TransactionResult.class);
        return result;
    }
}

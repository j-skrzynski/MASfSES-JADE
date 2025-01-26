package org.example.agents.investor.behaviours;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.example.agents.investor.InvestorAgent;
import org.example.agents.investor.InvestorPriceRecordLabel;
import org.example.global.StockPriceDictionary;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class PriceCheckerBehaviour extends TickerBehaviour {

    private InvestorAgent agent;

    public PriceCheckerBehaviour(InvestorAgent agent,Long period) {
        super(agent,period);
        this.agent = agent;
    }

    @Override
    protected void onTick() {
        this.updatePrices();
    }


    private void updatePrices(){
        Set< InvestorPriceRecordLabel > stocks = agent.getObservedStocks();
        for (InvestorPriceRecordLabel stock : stocks) {
            updateBestBuy(stock);
            updateLastPrice(stock);
            updateBestSell(stock);
        }
    }
    private void updateLastPrice(InvestorPriceRecordLabel stock){
        Double price = StockPriceDictionary.getPrice(stock.shortName(),stock.stockExchangeName());
        agent.updateLastPrice(stock,price);
//            System.out.println(stock.shortName()+":"+price+"**************************");
    }


    private String createJsonCommand(String command, String shortName) {
        HashMap<String, Object> commandMap = new HashMap<>();
        commandMap.put("command", command);
        commandMap.put("arguments", List.of(shortName));
        commandMap.put("traderName", "");
        commandMap.put("brokerName", "");
        commandMap.put("exchangeName", "");
        commandMap.put("brokerOrderId", "");

        return new Gson().toJson(commandMap);
    }
    private Double extractPriceFromJson(String json) {
        try {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<HashMap<String, Object>>>() {}.getType();
            List<HashMap<String, Object>> results = gson.fromJson(json, listType);

            if (results != null && !results.isEmpty()) {
                HashMap<String, Object> topResult = results.get(0); // Pierwszy element
                return (Double) topResult.get("price"); // Wyciągnięcie ceny
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void updateBestBuy(InvestorPriceRecordLabel stock) {
        try {
            // Tworzenie zapytania
            ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
            request.addReceiver(new AID(stock.stockExchangeName(), AID.ISLOCALNAME));
            request.setContent(createJsonCommand("GET_TOP_BUY", stock.shortName()));
            String rid = UUID.randomUUID().toString();
            request.setReplyWith(rid);
            myAgent.send(request);

            // Oczekiwanie na odpowiedź
            ACLMessage reply = myAgent.blockingReceive(MessageTemplate.MatchInReplyTo(rid), 1000);
            if (reply != null) {
                String content = reply.getContent();
                Double price = extractPriceFromJson(content);
                if (price != null) {
                    agent.updateBuyPrice(stock, price);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateBestSell(InvestorPriceRecordLabel stock) {
        try {
            // Tworzenie zapytania
            ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
            request.addReceiver(new AID(stock.stockExchangeName(), AID.ISLOCALNAME));
            request.setContent(createJsonCommand("GET_TOP_SELL", stock.shortName()));
            String rid = UUID.randomUUID().toString();
            request.setReplyWith(rid);
            myAgent.send(request);

            // Oczekiwanie na odpowiedź
            ACLMessage reply = myAgent.blockingReceive(MessageTemplate.MatchInReplyTo(rid), 1000);
            if (reply != null) {
                String content = reply.getContent();
                Double price = extractPriceFromJson(content);
                if (price != null) {
                    agent.updateSellPrice(stock, price);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}

package org.example.agents.investor;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.example.agents.investor.behaviours.PriceCheckerBehaviour;
import org.example.commandCreator.broker.BrokerCommandFactory;
import org.example.datamodels.order.OrderType;

import java.util.*;

public class InvestorAgent extends Agent {
    /*
    todo
    - behaviour for asking for the price
    - behaviour for deciding on broker offer
    -
     */
    protected Set<InvestorPriceRecordLabel> observedStocks;
    protected HashMap<InvestorPriceRecordLabel, Double> bestBuyPrice;
    protected HashMap<InvestorPriceRecordLabel, Double> bestSellPrice;
    protected HashMap<InvestorPriceRecordLabel, Double> lastPrice;

    protected String traderName = "trader";
    protected Double moneyBalance;
    protected List<String> supportedBrokers;
    protected List<String> supportedExchanges;

    public InvestorAgent() {
        observedStocks = new HashSet<>();
        bestBuyPrice = new HashMap<>();
        bestSellPrice = new HashMap<>();
        lastPrice = new HashMap<>();
        moneyBalance = 0.0;
        supportedBrokers = new ArrayList<>();
        supportedExchanges = new ArrayList<>();

        supportedExchanges.add("GPW");
        supportedBrokers.add("Broker1");
    }

    private void registerObservedStock(InvestorPriceRecordLabel label){
        observedStocks.add(label);
        bestBuyPrice.put(label, null);
        bestSellPrice.put(label, null);
        lastPrice.put(label, null);
    }

    @Override
    protected void setup() {
        super.setup();
        traderName = this.getName();
        Object[] args = getArguments();
        Collection<InvestorPriceRecordLabel> stocksToObserve = (Collection<InvestorPriceRecordLabel>) args[0];
        for (InvestorPriceRecordLabel stock : stocksToObserve) {
            registerObservedStock(stock);
        }
        this.moneyBalance = (Double) args[1];
        this.addBehaviour(new PriceCheckerBehaviour(this, 5000L)); // checks prices every second
        for (String broker : supportedBrokers) {
            this.registerInBroker(broker);
        }

    }


    public Set<InvestorPriceRecordLabel> getObservedStocks() {
        return observedStocks;
    }
    public void updateBuyPrice(InvestorPriceRecordLabel label, Double price){
        bestBuyPrice.put(label, price);
    }
    public void updateSellPrice(InvestorPriceRecordLabel label, Double price){
        bestSellPrice.put(label, price);
    }
    public void updateLastPrice(InvestorPriceRecordLabel label, Double price){
        lastPrice.put(label, price);
    }

    protected Double getBalance(String broker) {
        BrokerCommandFactory bcf = new BrokerCommandFactory("", traderName);
        String requestId = UUID.randomUUID().toString(); // Generowanie unikalnego ID zapytania
        String balanceRequest = bcf.getBalance().getJsonCommand();
        sendMessageToBroker(getBrokerAID(broker), balanceRequest, requestId);

        // Oczekiwanie na odpowiedź od brokera
        ACLMessage reply = blockingReceive(MessageTemplate.MatchInReplyTo(requestId));

        if (reply != null && reply.getPerformative() == ACLMessage.INFORM) {
            // Parsowanie odpowiedzi
            String content = reply.getContent();
            try {
                Double balance = Double.valueOf(content);
                System.out.println("Balance received: " + balance);
                return balance;
            } catch (NumberFormatException e) {
                System.out.println("Invalid balance response: " + content);
                return 0.0;
            }
        } else {
            System.out.println("No valid reply received for balance request.");
            return 0.0;
        }
    }
    protected void depositMoney(String broker, double amount){
        BrokerCommandFactory bcf = new BrokerCommandFactory("", traderName);
        sendMessageToBroker(getBrokerAID(broker), bcf.deposit(amount).getJsonCommand(),"");
    }
    protected void withdrawMoney(String broker, double amount){
        BrokerCommandFactory bcf = new BrokerCommandFactory("", traderName);
        sendMessageToBroker(getBrokerAID(broker), bcf.withdraw(amount).getJsonCommand(),"");
    }
    protected void registerInBroker(String broker){
        BrokerCommandFactory bcf = new BrokerCommandFactory("", traderName);
        sendMessageToBroker(getBrokerAID(broker), bcf.register().getJsonCommand(),"");
    }
    protected AID getBrokerAID(String broker){
        return new AID(broker, AID.ISLOCALNAME);
    }

    protected void sendMarketOrder(String shortName, OrderType type, Double price, Long quantity, String exchange, String broker){
        BrokerCommandFactory bcf = new BrokerCommandFactory(exchange, traderName);
        String msg = bcf.marketOrder(shortName,type,price,quantity).getJsonCommand();
        AID brokerAID = getBrokerAID(broker);
        sendMessageToBroker(brokerAID, msg,"");
    }
    protected void sendLimitlessOrder(String shortName, OrderType type, Long quantity, String exchange, String broker){
        BrokerCommandFactory bcf = new BrokerCommandFactory(exchange, traderName);
        String msg = bcf.limitlessOrder(shortName,type,quantity).getJsonCommand();
        AID brokerAID = getBrokerAID(broker);
        sendMessageToBroker(brokerAID, msg,"");
    }
    protected void sendAwaintingOrder(String shortName, OrderType type,Double price, Long quantity,Double activationPrice, String exchange, String broker){
        BrokerCommandFactory bcf = new BrokerCommandFactory(exchange, traderName);
        String msg = bcf.awaitingOrder(shortName,type,price,quantity,activationPrice).getJsonCommand();
        AID brokerAID = getBrokerAID(broker);
        sendMessageToBroker(brokerAID, msg,"");
    }
    protected void sendAwaintingLimitlessOrder(String shortName, OrderType type, Long quantity,Double activationPrice, String exchange, String broker){
        BrokerCommandFactory bcf = new BrokerCommandFactory(exchange, traderName);
        String msg = bcf.awaitingLimitlessOrder(shortName,type,quantity,activationPrice).getJsonCommand();
        AID brokerAID = getBrokerAID(broker);
        sendMessageToBroker(brokerAID, msg,"");
    }

    private void sendMessageToBroker(AID receiver, String content, String requestId) {
        // Tworzenie wiadomości
        ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
        message.addReceiver(receiver);
        message.setContent(content);
        message.setReplyWith(requestId); // Ustawiamy ID zapytania
        send(message); // Wysyłanie wiadomości
        System.out.println("Message sent to broker: " + content);
    }
}

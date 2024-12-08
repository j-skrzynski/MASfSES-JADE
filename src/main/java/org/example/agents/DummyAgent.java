package org.example.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

public class DummyAgent extends Agent {

    @Override
    protected void setup() {
        System.out.println(getLocalName() + " started and ready to send messages.");

        // Adres odbiorcy
        AID gpwAgent = new AID("GPW", AID.ISLOCALNAME);

        // Wiadomości do wysłania
        String[] messages = {
                "#ADD_STOCK;TestStock;XD;20;1000",
                "Trader1#PLACE_ORDER;LIMIT;SELL;WDD/5;XD;100;102",
                "Trader2#PLACE_ORDER;NOLIMIT;BUY;WDD/5;XD;100"
        };

        for (String msg : messages) {
            sendMessageToGPW(gpwAgent, msg);
        }
    }

    private void sendMessageToGPW(AID receiver, String content) {
        // Tworzenie wiadomości
        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
        message.addReceiver(receiver);
        message.setContent(content);
        send(message); // Wysyłanie wiadomości
        System.out.println("Message sent to GPW: " + content);
    }
}

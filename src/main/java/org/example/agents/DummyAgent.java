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
                "{\n" +
                        "  \"command\": \"ADD_STOCK\",\n" +
                        "  \"arguments\": [\"Apple\", \"AAPL\", 150.0, 1000000],\n" +
                        "  \"traderName\": \"\",\n" +
                        "  \"brokerName\": \"\",\n" +
                        "  \"exchangeName\": \"\",\n" +
                        "  \"brokerOrderId\": \"\"\n" +
                        "}\n",
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

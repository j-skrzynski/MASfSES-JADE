package org.example.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.WakerBehaviour;
import org.example.commandCreator.broker.BrokerCommandFactory;
import org.example.datamodels.order.OrderType;

public class DummyAgent extends Agent {

    @Override
    protected void setup() {
        System.out.println(getLocalName() + " started and ready to send messages.");

        // Adres odbiorcy
        AID gpwAgent = new AID("GPW", AID.ISLOCALNAME);
        AID brokerAgent = new AID("Broker1", AID.ISLOCALNAME);

        // Komendy do wysłania
        String[] messagesToGPW = {
                "{\n" +
                        "  \"command\": \"ADD_STOCK\",\n" +
                        "  \"arguments\": [\"Apple\", \"AAPL\", 150.0, 1000000],\n" +
                        "  \"traderName\": \"\",\n" +
                        "  \"brokerName\": \"\",\n" +
                        "  \"exchangeName\": \"\",\n" +
                        "  \"brokerOrderId\": \"\"\n" +
                        "}\n",
                "{\n" +
                        "\"command\": \"PLACE_ORDER\",\n" +
                        "\"arguments\": [{\n" +
                        "\"order\": {\n" +
                        "\"symbol\": {\"shortName\": \"AAPL\"},\n" +
                        "\"orderType\": \"SELL\",\n" +
                        "\"price\": 5.0,\n" +
                        "\"quantity\": 500,\n" +
                        "\"expirationSecpification\": \"D\"\n" +
                        "},\n" +
                        "\"awaiting\": false,\n" +
                        "\"price\": 0.0\n" +
                        "}],\n" +
                        "\"traderName\": \"JohnDoe\",\n" +
                        "\"brokerName\": \"XYZBroker\",\n" +
                        "\"exchangeName\": \"GPW\",\n" +
                        "\"brokerOrderId\": \"\"\n" +
                        "}"
        };

        // Tworzenie fabryki komend dla brokera
        BrokerCommandFactory bcf = new BrokerCommandFactory("GPW", "trader1");

        // Tworzenie zachowania sekwencyjnego
        SequentialBehaviour behaviour = new SequentialBehaviour();

        // Dodanie wiadomości do GPW
        for (String msg : messagesToGPW) {
            behaviour.addSubBehaviour(new WakerBehaviour(this, 1000) { // Opóźnienie 1000 ms
                @Override
                protected void onWake() {
                    sendMessageToGPW(gpwAgent, msg);
                }
            });
        }

        // Dodanie wiadomości do brokera
        behaviour.addSubBehaviour(new WakerBehaviour(this, 3000) { // Opóźnienie 3000 ms
            @Override
            protected void onWake() {
                sendMessageToBroker(brokerAgent, bcf.register().getJsonCommand());
            }
        });

        behaviour.addSubBehaviour(new WakerBehaviour(this, 4000) { // Opóźnienie 4000 ms
            @Override
            protected void onWake() {
                sendMessageToBroker(brokerAgent, bcf.deposit(5000.0).getJsonCommand());
            }
        });

        behaviour.addSubBehaviour(new WakerBehaviour(this, 5000) { // Opóźnienie 5000 ms
            @Override
            protected void onWake() {
                sendMessageToBroker(brokerAgent, bcf.marketOrder("AAPL", OrderType.BUY, 5.0, 1000L).expD().getJsonCommand());
            }
        });

        // Dodanie zachowania do agenta
        addBehaviour(behaviour);
    }

    private void sendMessageToGPW(AID receiver, String content) {
        // Tworzenie wiadomości
        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
        message.addReceiver(receiver);
        message.setContent(content);
        send(message); // Wysyłanie wiadomości
        System.out.println("Message sent to GPW: " + content);
    }

    private void sendMessageToBroker(AID receiver, String content) {
        // Tworzenie wiadomości
        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
        message.addReceiver(receiver);
        message.setContent(content);
        send(message); // Wysyłanie wiadomości
        System.out.println("Message sent to broker: " + content);
    }
}

package org.example;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import org.example.agents.DummyAgent;
import org.example.agents.broker.BrokerAgent;
import org.example.agents.stockexchange.StockExchangeAgent;

public class Main {
    public static void main(String[] args) {
        Runtime runtime = Runtime.instance();
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.GUI, "true");
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        profile.setParameter(Profile.MAIN_PORT, "1099");

        AgentContainer mainContainer = runtime.createMainContainer(profile);
        try {
            mainContainer.start();
            System.out.println("JADE container started!");

            // Tworzenie i uruchamianie agentów
            Object[] agentArgs = {"GPW"}; // Argumenty przekazywane do agenta
            AgentController gpwAgent = mainContainer.createNewAgent(
                    "GPW",                      // Nazwa agenta
                    StockExchangeAgent.class.getName(), // Klasa agenta
                    agentArgs                  // Argumenty agenta
            );

            Object[] agentArgs2 = {}; // Argumenty przekazywane do agenta
            AgentController brokerAgent = mainContainer.createNewAgent(
                    "Broker1",                      // Nazwa agenta
                    BrokerAgent.class.getName(), // Klasa agenta
                    agentArgs2                 // Argumenty agenta
            );

            gpwAgent.start();
            brokerAgent.start();

            System.out.println("GPW agent started!");

            AgentController dummyAgent = mainContainer.createNewAgent(
                    "DummyAgent",
                    DummyAgent.class.getName(),
                    null
            );

            dummyAgent.start();
            System.out.println("DummyAgent started and sending messages!");

            // Tworzenie agenta Sniffer
            String[] snifferTargets = {"GPW", "Broker1", "DummyAgent"};
            String snifferTargetArgs = String.join(";", snifferTargets);

            AgentController snifferAgent = mainContainer.createNewAgent(
                    "SnifferAgent",
                    "jade.tools.sniffer.Sniffer", // Klasa agenta Sniffer
                    new Object[]{snifferTargetArgs} // Przekazanie nazw agentów
            );

            snifferAgent.start();

            System.out.println("SnifferAgent started and listening to messages!");

        } catch (ControllerException e) {
            e.printStackTrace();
        }
    }
}

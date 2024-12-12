package org.example;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
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


            Object[] agentArgs = {"GPW Exchange"}; // Argumenty przekazywane do agenta
            AgentController gpwAgent = mainContainer.createNewAgent(
                    "GPW",                      // Nazwa agenta
                    StockExchangeAgent.class.getName(), // Klasa agenta
                    agentArgs                  // Argumenty agenta
            );

            gpwAgent.start();
            System.out.println("GPW agent started!");

//            AgentController dummyAgent = mainContainer.createNewAgent(
//                    "DummyAgent",
//                    DummyAgent.class.getName(),
//                    null
//            );
//            dummyAgent.start();
//            System.out.println("DummyAgent started and sending messages!");

        } catch (ControllerException e) {
            e.printStackTrace();
        }
    }
}
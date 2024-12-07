package org.example;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.ControllerException;

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
        } catch (ControllerException e) {
            e.printStackTrace();
        }
    }
}
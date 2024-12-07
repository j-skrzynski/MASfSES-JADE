package org.example.agents;

import jade.core.Agent;

public class MyAgent extends Agent {
    @Override
    protected void setup() {
        System.out.println("Hello! Agent " + getLocalName() + " is ready.");
    }
}
package org.example.stockexchange.listeners;

import org.example.logic.stockexchange.settlements.TransactionSettlement;
import org.example.visualization.AgentWindow;
import org.example.visualization.AgentWindowManager;

public class SentSettlementsListener extends BaseQueueEventListener<TransactionSettlement> {
    public SentSettlementsListener(AgentWindowManager manager) { super(manager); }

    @Override
    public void handleAddEvent(AgentWindow agentWindow, TransactionSettlement element) {
        String brokerName = element.getAddressee().getBroker().getName();

        agentWindow.drawText(brokerName);
    }

    @Override
    public void handleRemoveEvent(AgentWindow agentWindow, TransactionSettlement element) {
        handleAddEvent(agentWindow, element);
    }
}

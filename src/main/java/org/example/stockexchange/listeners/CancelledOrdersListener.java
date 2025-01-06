package org.example.stockexchange.listeners;

import org.example.logic.stockexchange.utils.OrderSubmitter;
import org.example.visualization.AgentWindow;
import org.example.visualization.AgentWindowManager;

public class CancelledOrdersListener extends BaseQueueEventListener<OrderSubmitter> {
    public CancelledOrdersListener(AgentWindowManager manager) { super(manager); }

    @Override
    public void handleAddEvent(AgentWindow agentWindow, OrderSubmitter element) {
        String brokerName = element.getBroker().getName();

        agentWindow.drawText(brokerName);
    }

    @Override
    public void handleRemoveEvent(AgentWindow agentWindow, OrderSubmitter element) {
        handleAddEvent(agentWindow, element);
    }
}

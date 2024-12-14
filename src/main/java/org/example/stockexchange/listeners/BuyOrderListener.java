package org.example.stockexchange.listeners;

import org.example.stockexchange.order.Order;
import org.example.visualization.AgentWindow;
import org.example.visualization.AgentWindowManager;

public class BuyOrderListener extends BaseQueueEventListener<Order> {
    public BuyOrderListener(AgentWindowManager manager) { super(manager); }

    @Override
    protected void handleAddEvent(AgentWindow agentWindow, Order element) {
        String brokerName = element.getSubmitter().getBroker().getName();

        agentWindow.drawText(brokerName);
    }

    @Override
    protected void handleRemoveEvent(AgentWindow agentWindow, Order element) {
        handleAddEvent(agentWindow, element);
    }
}

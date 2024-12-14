package org.example.stockexchange.listeners;

import org.example.stockexchange.order.Order;
import org.example.visualization.AgentWindow;
import org.example.visualization.AgentWindowManager;

public class SellOrderListener extends BaseQueueEventListener<Order> {
    public SellOrderListener(AgentWindowManager manager) { super(manager); }

    @Override
    public void handleAddEvent(AgentWindow agentWindow, Order element) {
        String brokerName = element.getSubmitter().getBroker().getName();

        agentWindow.drawText(brokerName);
    }

    @Override
    public void handleRemoveEvent(AgentWindow agentWindow, Order element) {
        handleAddEvent(agentWindow, element);
    }
}

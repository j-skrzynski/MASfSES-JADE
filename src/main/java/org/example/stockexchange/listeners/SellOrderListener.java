package org.example.stockexchange.listeners;

import org.example.logic.stockexchange.order.marketorder.ExchangeOrder;
import org.example.visualization.AgentWindow;
import org.example.visualization.AgentWindowManager;

public class SellOrderListener extends BaseQueueEventListener<ExchangeOrder> {
    public SellOrderListener(AgentWindowManager manager) { super(manager); }

    @Override
    public void handleAddEvent(AgentWindow agentWindow, ExchangeOrder element) {
        String brokerName = element.getSubmitter().getBroker().getName();

        agentWindow.drawText(brokerName);
    }

    @Override
    public void handleRemoveEvent(AgentWindow agentWindow, ExchangeOrder element) {
        handleAddEvent(agentWindow, element);
    }
}

package org.example.stockexchange.listeners;

import org.example.logic.stockexchange.order.marketorder.ExchangeOrder;
import org.example.visualization.AgentWindow;
import org.example.visualization.AgentWindowManager;

public class BuyOrderListener extends BaseQueueEventListener<ExchangeOrder> {
    public BuyOrderListener(AgentWindowManager manager) { super(manager); }

    @Override
    protected void handleAddEvent(AgentWindow agentWindow, ExchangeOrder element) {
        String brokerName = element.getSubmitter().getBroker().getName();

        agentWindow.drawText(brokerName);
    }

    @Override
    protected void handleRemoveEvent(AgentWindow agentWindow, ExchangeOrder element) {
        handleAddEvent(agentWindow, element);
    }
}

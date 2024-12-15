package org.example.stockexchange.listeners;

import org.example.logic.stockexchange.order.awaitingorder.AwaitingExchangeOrder;
import org.example.visualization.AgentWindow;
import org.example.visualization.AgentWindowManager;

public class AwaitingBuyOrderListener extends BaseQueueEventListener<AwaitingExchangeOrder> {
    public AwaitingBuyOrderListener(AgentWindowManager manager) { super(manager); }

    @Override
    protected void handleAddEvent(AgentWindow agentWindow, AwaitingExchangeOrder element) {
        String brokerName = element.getActivatedOrder().getSubmitter().getBroker().getName();

        agentWindow.drawText(brokerName);
    }

    @Override
    protected void handleRemoveEvent(AgentWindow agentWindow, AwaitingExchangeOrder element) {
        handleAddEvent(agentWindow, element);
    }
}

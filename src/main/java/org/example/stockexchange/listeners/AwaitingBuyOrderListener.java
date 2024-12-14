package org.example.stockexchange.listeners;

import org.example.stockexchange.order.AwaitingOrder;
import org.example.visualization.AgentWindow;
import org.example.visualization.AgentWindowManager;

public class AwaitingBuyOrderListener extends BaseQueueEventListener<AwaitingOrder> {
    public AwaitingBuyOrderListener(AgentWindowManager manager) { super(manager); }

    @Override
    protected void handleAddEvent(AgentWindow agentWindow, AwaitingOrder element) {
        String brokerName = element.getActivatedOrder().getSubmitter().getBroker().getName();

        agentWindow.drawText(brokerName);
    }

    @Override
    protected void handleRemoveEvent(AgentWindow agentWindow, AwaitingOrder element) {
        handleAddEvent(agentWindow, element);
    }
}

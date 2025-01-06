package org.example.stockexchange.listeners;

import org.example.util.QueueEventListener;
import org.example.visualization.AgentWindow;
import org.example.visualization.AgentWindowManager;

public abstract class BaseQueueEventListener<E> implements QueueEventListener<E> {
    protected final AgentWindowManager _agentWindowManager;

    public BaseQueueEventListener(AgentWindowManager agentWindowManager) {
        _agentWindowManager = agentWindowManager;
    }

    @Override
    public void onAdd(E element) {
        AgentWindow window = getAgentWindow(element);
        handleAddEvent(window, element);
    }

    @Override
    public void onRemove(E element) {
        AgentWindow window = getAgentWindow(element);
        if (window != null) {
            handleRemoveEvent(window, element);
        }
    }

    protected abstract void handleAddEvent(AgentWindow agentWindow, E element);

    protected abstract void handleRemoveEvent(AgentWindow agentWindow, E element);

    /**
     * Dummy method (for now). Should be made abstract so that each listener obtains
     * the necessary window depending on the event model
     * @return AgentWindow
     */
    private AgentWindow getAgentWindow(Object model) {
        return _agentWindowManager.getAgentWindows()
                .stream().
                findFirst().
                orElse(null);
    }
}

package org.example.visualization;

import java.util.ArrayList;
import java.util.List;

public class AgentWindowManager {
    private final List<AgentWindow> _agentWindows;

    private static AgentWindowManager TEST_INSTANCE;

    /**
     * Dummy method (for now) to retrieve all agent windows
     * @return AgentWindowManager
     */
    public static AgentWindowManager getTestInstance() {
        if (TEST_INSTANCE != null) {
            return TEST_INSTANCE;
        }

        TEST_INSTANCE = new AgentWindowManager();
        TEST_INSTANCE.addAgentWindow(new AgentWindow("GPW"));

        return TEST_INSTANCE;
    }

    public AgentWindowManager() {
        _agentWindows = new ArrayList<>();
    }

    public void addAgentWindow(AgentWindow window) {
        _agentWindows.add(window);
    }

    public List<AgentWindow> get_agentWindows() {
        return _agentWindows;
    }
}

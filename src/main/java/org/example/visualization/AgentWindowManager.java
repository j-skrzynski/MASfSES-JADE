package org.example.visualization;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AgentWindowManager {
    private final List<AgentWindow> agentWindows;

    private static AgentWindowManager INSTANCE;

    public static AgentWindowManager getInstance() {
        if (INSTANCE != null) {
            return INSTANCE;
        }

        INSTANCE = new AgentWindowManager();
        return INSTANCE;
    }

    private AgentWindowManager() {
        agentWindows = new ArrayList<>();
    }

    public List<AgentWindow> getAgentWindows() {
        return agentWindows;
    }

    public void addAgentWindow(String agentName, Object initialValue, Consumer<AgentWindow> postAdd) {
        SwingUtilities.invokeLater(() -> {
            if (agentWindows.stream().anyMatch(aw -> aw.getName() == agentName)) {
                System.out.printf("Agent window %s is not added because window with such name already exists%n", agentName);
            }
            else {
                AgentWindow newAgentWindow = new AgentWindow(agentName, initialValue);
                agentWindows.add(newAgentWindow);
                if (postAdd != null) {
                    postAdd.accept(newAgentWindow);
                }
            }
        });
    }
}

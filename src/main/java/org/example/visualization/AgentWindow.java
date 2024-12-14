package org.example.visualization;

import javax.swing.*;

public class AgentWindow {
    // frame components
    private final AgentWindowPanel _framePanel;

    public AgentWindow(String agentName) {
        JFrame frame = new JFrame();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setTitle(String.format("%s frame", agentName));

        _framePanel = new AgentWindowPanel();

        frame.getContentPane().add(this._framePanel);
        frame.pack();

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void drawText(String text) {
        _framePanel.setContent(text);
    }
}

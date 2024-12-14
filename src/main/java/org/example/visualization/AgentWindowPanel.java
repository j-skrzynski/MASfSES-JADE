package org.example.visualization;

import java.awt.*;

/**
 * Right now this is a simple panel that only displays text
 * Later on it will be modified, as more data to display will appear
 */
class AgentWindowPanel extends BaseWindowPanel<String> {
    private static final int PANEL_WIDTH = 400;
    private static final int PANEL_HEIGHT = 400;

    public AgentWindowPanel() { super(PANEL_WIDTH, PANEL_HEIGHT, "init"); }

    @Override
    protected void draw(Graphics2D g2, String content) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(Color.black);
        g2.drawString(content, 100, 100);
    }
}

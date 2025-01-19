package org.example.visualization.windowpanels;

import javax.swing.*;
import java.awt.*;

public abstract class BaseWindowPanel<ViewModel extends Comparable<ViewModel>> extends JPanel {
    private ViewModel currentValue;

    protected BaseWindowPanel(int windowWidth, int windowHeight, ViewModel initialValue) {
        assert initialValue != null;
        currentValue = initialValue;

        setPreferredSize(new Dimension(windowWidth, windowHeight));
        setBackground(Color.gray);
        setDoubleBuffered(true);
    }

    public void setValue(ViewModel newValue) {
        assert newValue != null;

        if (newValue.compareTo(currentValue) != 0) {
            this.currentValue = newValue;
            SwingUtilities.invokeLater(this::repaint);
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (g instanceof Graphics2D g2) {
            draw(g2, currentValue);
        }
    }

    protected abstract void draw(Graphics2D g2, ViewModel value);
}

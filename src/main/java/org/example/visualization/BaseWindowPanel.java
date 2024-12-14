package org.example.visualization;

import javax.swing.*;
import java.awt.*;

public abstract class BaseWindowPanel<Content extends Comparable<Content>> extends JPanel {
    private Content _content;

    protected BaseWindowPanel(int windowWidth, int windowHeight, Content contentInitialValue) {
        assert contentInitialValue != null;
        _content = contentInitialValue;

        setPreferredSize(new Dimension(windowWidth, windowHeight));
        setBackground(Color.gray);
        setDoubleBuffered(true);
    }

    public void setContent(Content content) {
        assert content != null;

        if (content.compareTo(_content) != 0) {
            this._content = content;
            SwingUtilities.invokeLater(this::repaint);
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (g instanceof Graphics2D g2) {
            draw(g2, _content);
        }
    }

    protected abstract void draw(Graphics2D g2, Content content);
}

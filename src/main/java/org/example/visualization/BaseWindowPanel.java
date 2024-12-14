package org.example.visualization;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class BaseWindowPanel<Content extends Comparable<Content>> extends JPanel implements Runnable {
    private final Thread _panelThread;
    private Content _content;
    private final AtomicBoolean _hasChanges = new AtomicBoolean(false);
    private static final Object LOCK = new Object();

    protected BaseWindowPanel(int windowWidth, int windowHeight, Content contentInitialValue) {
        assert contentInitialValue != null;
        _content = contentInitialValue;

        setPreferredSize(new Dimension(windowWidth, windowHeight));
        setBackground(Color.gray);
        setDoubleBuffered(true);

        _panelThread = new Thread(this);
        _panelThread.start();
    }

    public void setContent(Content content) {
        synchronized (LOCK) {
            assert content != null;

            if (content.compareTo(_content) != 0) {
                this._content = content;
                _hasChanges.set(true);
                System.out.println(_hasChanges);
//            System.out.println("setcontent");
//            try {
//                synchronized (_panelThread) {
//                    this._content = content;
//                    _hasChanges.set(true);
//                    System.out.println(_hasChanges);
//                }
//            } catch (Exception e) {
//                System.out.println(e.toString());
//                _hasChanges.set(false);
//            }
            }
        }
    }

    @Override
    public void run() {
        double drawInterval = 1_000_000_000 / 60.0;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (_panelThread != null) {
//            currentTime = System.nanoTime();
//            delta += (currentTime - lastTime) / drawInterval;
//            lastTime = currentTime;
//
//            if (delta >= 1) {
//                if (_hasChanges.get()) {
//                    System.out.println("run");
//                    draw(getGraphics(), _content);
//                    repaint();
//
//                    _hasChanges.set(false);
//                }
//
//                delta--;
//            }
            repaint();
        }
    }

    @Override
    public void paint(Graphics g) {
        if (g instanceof Graphics2D g2) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(Color.black);
            g2.drawString(Long.toString(System.nanoTime()), 100, 100);
        }
    }

    protected abstract void draw(Graphics graphics, Content content);
}

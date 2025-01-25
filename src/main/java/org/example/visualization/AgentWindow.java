package org.example.visualization;

import org.example.visualization.viewmodels.TraderViewModel;
import org.example.visualization.viewmodels.StockExchangeViewModel;
import org.example.visualization.windowpanels.TraderWindowPanel;
import org.example.visualization.windowpanels.StockExchangeWindowPanel;

import javax.swing.*;
import java.util.function.Consumer;

public class AgentWindow {
    private final String name;

    private Consumer<Object> updateFunc = _ -> {};

    public AgentWindow(String name, Object initialValue) {
        this.name = name;

        JFrame frame = createFrame();
        createPanel(frame, initialValue);
        frame.pack();

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public String getName() {
        return name;
    }

    public void updateAndDraw(Object newValue) {
        updateFunc.accept(newValue);
    }

    private JFrame createFrame() {
        JFrame frame = new JFrame();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setTitle(String.format("%s frame", name));

        return frame;
    }

    private void createPanel(JFrame frame, Object initialViewModel) {
        if (initialViewModel instanceof TraderViewModel initialTraderViewModel) {
            TraderWindowPanel windowPanel = new TraderWindowPanel(initialTraderViewModel);
            updateFunc = model -> windowPanel.setValue((TraderViewModel) model);

            frame.getContentPane().add(windowPanel);
        }
        else if (initialViewModel instanceof StockExchangeViewModel initialStockExchangeViewModel) {
            StockExchangeWindowPanel windowPanel = new StockExchangeWindowPanel(initialStockExchangeViewModel);
            updateFunc = model -> windowPanel.setValue((StockExchangeViewModel) model);

            frame.getContentPane().add(windowPanel);
        }
    }
}

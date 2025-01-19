package org.example.visualization.windowpanels;

import org.example.visualization.viewmodels.InvestorViewModel;

import java.awt.*;

public class InvestorWindowPanel extends BaseWindowPanel<InvestorViewModel> {
    private static final int PANEL_WIDTH = 400;
    private static final int PANEL_HEIGHT = 400;

    public InvestorWindowPanel(InvestorViewModel initialModel) {
        super(PANEL_WIDTH, PANEL_HEIGHT, initialModel);
    }

    @Override
    protected void draw(Graphics2D g2, InvestorViewModel newValue) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(Color.black);
        g2.drawString(Double.toString(newValue.getMoneyBalance()), 100, 100);
    }
}

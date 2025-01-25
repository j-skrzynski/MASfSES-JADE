package org.example.visualization.windowpanels;

import org.example.datamodels.WalletRecord;
import org.example.datamodels.order.OrderType;
import org.example.logic.broker.InvestorOrderRecord;
import org.example.visualization.Constants;
import org.example.visualization.utils.Graphics;
import org.example.visualization.utils.Layout;
import org.example.visualization.utils.StatisticalGraphics;
import org.example.visualization.utils.StringUtils;
import org.example.visualization.viewmodels.TraderViewModel;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TraderWindowPanel extends BaseWindowPanel<TraderViewModel> {
    private static final int FIRST_SECTION_WIDTH = 200;
    private static final int SECOND_SECTION_WIDTH = 200;

    private static final int BALANCE_HISTORY_PLOT_HEIGHT = 200;
    private static final int STOCKS_PANEL_HEIGHT = 200;
    private static final int TEXT_HEADING_HEIGHT = 20;

    private static final int PANEL_WIDTH = FIRST_SECTION_WIDTH + SECOND_SECTION_WIDTH;
    private static final int PANEL_HEIGHT = BALANCE_HISTORY_PLOT_HEIGHT + STOCKS_PANEL_HEIGHT + 2 * TEXT_HEADING_HEIGHT;

    private static final int ACTION_INDICATOR_WIDTH = 5;
    private static final int ACTION_INDICATOR_HEIGHT = 5;

    private static final int TEXT_LENGTH_CONSTRAINT = 20;

    public TraderWindowPanel(TraderViewModel initialModel) {
        super(PANEL_WIDTH, PANEL_HEIGHT, initialModel);
    }

    @Override
    protected void draw(Graphics2D g2, TraderViewModel newValue) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(Color.black);
        Graphics.setFontSize(g2, Constants.DEFAULT_FONT_SIZE);

        Layout.drawText(g2, "Balance history", 0, TEXT_HEADING_HEIGHT / 2);

        StatisticalGraphics.plot2D(g2,
                newValue.balanceHistory(),
                0,
                TEXT_HEADING_HEIGHT,
                FIRST_SECTION_WIDTH,
                BALANCE_HISTORY_PLOT_HEIGHT);

        Layout.drawText(g2, "Stocks", 0, TEXT_HEADING_HEIGHT + BALANCE_HISTORY_PLOT_HEIGHT + TEXT_HEADING_HEIGHT / 2);

        Layout.drawGrid(g2,
                newValue.stocks().values().toArray(new WalletRecord[0]),
                this::drawWallet,
                0,
                PANEL_HEIGHT - STOCKS_PANEL_HEIGHT,
                FIRST_SECTION_WIDTH,
                STOCKS_PANEL_HEIGHT);

        g2.drawLine(FIRST_SECTION_WIDTH, 0, FIRST_SECTION_WIDTH, PANEL_HEIGHT);

        Layout.drawText(g2, "Current orders", FIRST_SECTION_WIDTH, TEXT_HEADING_HEIGHT / 2);

        Layout.drawGrid(g2,
                newValue.currentOrders().values().toArray(new InvestorOrderRecord[0]),
                this::drawCurrentOrder,
                FIRST_SECTION_WIDTH,
                TEXT_HEADING_HEIGHT,
                SECOND_SECTION_WIDTH,
                PANEL_HEIGHT - TEXT_HEADING_HEIGHT);
    }

    private void drawWallet(Layout.DrawParameters<WalletRecord> drawParameters) {
        Graphics2D g2 = drawParameters.g2();
        WalletRecord item = drawParameters.item();
        int startX = (int)drawParameters.startX();
        int startY = (int)drawParameters.startY();
        int cellSize = (int)drawParameters.cellSize();

        g2.setColor(Color.pink);
        g2.fillRect(startX, startY, cellSize, cellSize);

        List<String> records = new ArrayList<>();
        records.add(item.getStock().getShortName());
        records.add(String.format("x%s", item.getAmount()));

        Layout.drawList(g2,
                records,
                startX,
                startY,
                startY + cellSize,
                Constants.DEFAULT_FONT_SIZE,
                Color.black);
    }

    private void drawCurrentOrder(Layout.DrawParameters<InvestorOrderRecord> drawParameters) {
        Graphics2D g2 = drawParameters.g2();
        InvestorOrderRecord item = drawParameters.item();
        int startX = (int)drawParameters.startX();
        int startY = (int)drawParameters.startY();
        int cellSize = (int)drawParameters.cellSize();

        g2.setColor(Color.pink);
        g2.fillRect(startX, startY, cellSize, cellSize);

        g2.setColor((item.getAction() == OrderType.BUY) ? Color.cyan : Color.green);
        g2.fillRect(startX, startY, ACTION_INDICATOR_WIDTH, ACTION_INDICATOR_HEIGHT);

        if (item.isLimit()) {
            g2.setColor(Color.black);
            g2.fillRect(startX + ACTION_INDICATOR_WIDTH,
                    startY,
                    cellSize - ACTION_INDICATOR_WIDTH,
                    ACTION_INDICATOR_HEIGHT);
        }

        List<String> records = new ArrayList<>();
        records.add(StringUtils.hideString(item.getStockSymbol().getShortName(), TEXT_LENGTH_CONSTRAINT));
        records.add(StringUtils.hideString(String.format("To buy: %s", item.getAmountOfStockToBeBought()), TEXT_LENGTH_CONSTRAINT));
        records.add(StringUtils.hideString(String.format("To sell: %s", item.getAmountOfStockToBeSold()), TEXT_LENGTH_CONSTRAINT));
        records.add(StringUtils.hideString(String.format("Locked: %s", item.getMoneyLocked()), TEXT_LENGTH_CONSTRAINT));

        Layout.drawList(g2,
                records,
                startX,
                startY + ACTION_INDICATOR_HEIGHT,
                startY + cellSize,
                Constants.DEFAULT_FONT_SIZE,
                Color.black);
    }
}

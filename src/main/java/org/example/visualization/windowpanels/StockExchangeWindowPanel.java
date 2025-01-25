package org.example.visualization.windowpanels;

import org.example.logic.stockexchange.order.awaitingorder.AwaitingExchangeOrder;
import org.example.logic.stockexchange.order.marketorder.ExchangeOrder;
import org.example.logic.stockexchange.settlements.TransactionSettlement;
import org.example.logic.stockexchange.utils.OrderSubmitter;
import org.example.visualization.Constants;
import org.example.visualization.utils.Layout;
import org.example.visualization.utils.StatisticalGraphics;
import org.example.visualization.utils.StringUtils;
import org.example.visualization.viewmodels.StockExchangeViewModel;
import org.glassfish.pfl.basic.contain.Pair;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

public class StockExchangeWindowPanel extends BaseWindowPanel<StockExchangeViewModel> {
    private static final int PANEL_WIDTH = 1000;
    private static final int PANEL_HEIGHT = 800;

    private static final int TEXT_HEADING_HEIGHT = 20;
    private static final int QUEUE_LABEL_WIDTH = 100;

    private static final int EXCHANGE_ORDER_RECT_HEIGHT = 60;
    private static final int AWAITING_EXCHANGE_ORDER_RECT_HEIGHT = 80;
    private static final int TRANSACTION_SETTLEMENT_RECT_HEIGHT = 80;
    private static final int ORDER_SUBMITTER_RECT_HEIGHT = 80;

    private static final int PLOT_WIDTH = 300;
    private static final int PLOT_HEIGHT = 300;

    private static final int ACTION_INDICATOR_WIDTH = 5;
    private static final int ACTION_INDICATOR_HEIGHT = 5;

    private static final int QUEUE_VERTICAL_GAP = 30;

    private int queueY;

    public StockExchangeWindowPanel(StockExchangeViewModel initialModel) {
        super(PANEL_WIDTH, PANEL_HEIGHT, initialModel);
    }

    @Override
    protected void draw(Graphics2D g2, StockExchangeViewModel newValue) {
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            // draw queues

            resetY();

            drawQueue(g2,
                    newValue.getBuyOrders().toArray(new ExchangeOrder[0]),
                    dp -> drawExchangeOrder(dp, true, false),
                    "Buy orders queue",
                    EXCHANGE_ORDER_RECT_HEIGHT);

            drawQueue(g2,
                    newValue.getSellOrders().toArray(new ExchangeOrder[0]),
                    dp -> drawExchangeOrder(dp, false, false),
                    "Sell orders queue",
                    EXCHANGE_ORDER_RECT_HEIGHT);

            drawQueue(g2,
                    newValue.getNoLimitBuy().toArray(new ExchangeOrder[0]),
                    dp -> drawExchangeOrder(dp, true, true),
                    "No limit buy queue",
                    EXCHANGE_ORDER_RECT_HEIGHT);

            drawQueue(g2,
                    newValue.getNoLimitSell().toArray(new ExchangeOrder[0]),
                    dp -> drawExchangeOrder(dp, false, true),
                    "No limit sell queue",
                    EXCHANGE_ORDER_RECT_HEIGHT);

            drawQueue(g2,
                    newValue.getAwaitingActivationBuy().toArray(new AwaitingExchangeOrder[0]),
                    dp -> drawAwaitingExchangeOrder(dp, true),
                    "Awaiting activation buy queue",
                    AWAITING_EXCHANGE_ORDER_RECT_HEIGHT);

            drawQueue(g2,
                    newValue.getAwaitingActivationSell().toArray(new AwaitingExchangeOrder[0]),
                    dp -> drawAwaitingExchangeOrder(dp, false),
                    "Awaiting activation sell queue",
                    AWAITING_EXCHANGE_ORDER_RECT_HEIGHT);

            drawQueue(g2,
                    newValue.getSettlementsToSend().toArray(new TransactionSettlement[0]),
                    this::drawTransactionSettlement,
                    "Settlements to send queue",
                    TRANSACTION_SETTLEMENT_RECT_HEIGHT);

            drawQueue(g2,
                    newValue.getCanceledOrders().toArray(new OrderSubmitter[0]),
                    this::drawCancelledOrder,
                    "Canceled orders queue",
                    ORDER_SUBMITTER_RECT_HEIGHT);

            // draw history plots

            List<Pair<Double, Long>> history = newValue.getHistory();

            Layout.drawText(g2, "Price history", PANEL_WIDTH / 2, TEXT_HEADING_HEIGHT / 2);

            StatisticalGraphics.plot2D(g2,
                    history.stream().map(Pair::first).toList().toArray(new Double[0]),
                    PANEL_WIDTH / 2,
                    TEXT_HEADING_HEIGHT,
                    PLOT_WIDTH,
                    PLOT_HEIGHT);

            Layout.drawText(g2, "Quantity history", PANEL_WIDTH / 2, TEXT_HEADING_HEIGHT + PLOT_HEIGHT + TEXT_HEADING_HEIGHT / 2);

            StatisticalGraphics.plot2D(g2,
                    history.stream().map(p -> (double)p.second()).toList().toArray(new Double[0]),
                    PANEL_WIDTH / 2,
                    PLOT_HEIGHT + 2 * TEXT_HEADING_HEIGHT,
                    PLOT_WIDTH,
                    PLOT_HEIGHT);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void resetY() {
        queueY = 0;
    }

    private <T> void drawQueue(Graphics2D g2,
                               T[] items,
                               Consumer<Layout.DrawParameters<T>> drawFunc,
                               String queueName,
                               int queueHeight) {
        Layout.drawList(g2,
                new ArrayList<>(Collections.singletonList(queueName)),
                0,
                queueY,
                queueY + queueHeight,
                Constants.DEFAULT_FONT_SIZE,
                Color.black);

        Layout.drawGrid(g2,
                items,
                drawFunc,
                QUEUE_LABEL_WIDTH,
                queueY,
                PANEL_WIDTH / 2.0 - QUEUE_LABEL_WIDTH,
                queueHeight);

        queueY += queueHeight + QUEUE_VERTICAL_GAP;
        drawHorizontalLine(g2, queueY - QUEUE_VERTICAL_GAP / 2);
    }

    private void drawHorizontalLine(Graphics2D g2, int y) {
        g2.setColor(Color.black);
        g2.drawLine(0, y, PANEL_WIDTH / 2, y);
    }

    private void drawExchangeOrder(Layout.DrawParameters<ExchangeOrder> drawParameters, boolean buy, boolean limit) {
        Graphics2D g2 = drawParameters.g2();
        ExchangeOrder item = drawParameters.item();
        int startX = (int)drawParameters.startX();
        int startY = (int)drawParameters.startY();
        int cellSize = (int)drawParameters.cellSize();

        g2.setColor(Color.pink);
        g2.fillRect(startX, startY, cellSize, cellSize);

        g2.setColor(buy ? Color.cyan : Color.green);
        g2.fillRect(startX, startY, ACTION_INDICATOR_WIDTH, ACTION_INDICATOR_HEIGHT);

        if (limit) {
            g2.setColor(Color.black);
            g2.fillRect(startX + ACTION_INDICATOR_WIDTH,
                    startY,
                    cellSize - ACTION_INDICATOR_WIDTH,
                    ACTION_INDICATOR_HEIGHT);
        }

        List<String> records = new ArrayList<>();
        records.add(String.format("x%s", item.getQuantity()));
        records.add(String.format("%s$", item.getPrice()));

        Layout.drawList(g2,
                records,
                startX,
                startY,
                startY + cellSize,
                Constants.SMALL_FONT_SIZE,
                Color.black);
    }

    private void drawAwaitingExchangeOrder(Layout.DrawParameters<AwaitingExchangeOrder> drawParameters, boolean buy) {
        Graphics2D g2 = drawParameters.g2();
        AwaitingExchangeOrder item = drawParameters.item();
        int startX = (int)drawParameters.startX();
        int startY = (int)drawParameters.startY();
        int cellSize = (int)drawParameters.cellSize();

        g2.setColor(Color.pink);
        g2.fillRect(startX, startY, cellSize, EXCHANGE_ORDER_RECT_HEIGHT);

        g2.setColor(buy ? Color.cyan : Color.green);
        g2.fillRect(startX, startY, ACTION_INDICATOR_WIDTH, ACTION_INDICATOR_HEIGHT);

        List<String> exchangeOrderRecords = new ArrayList<>();
        exchangeOrderRecords.add(String.format("x%s", item.getActivatedOrder().getQuantity()));
        exchangeOrderRecords.add(String.format("%s$", item.getActivatedOrder().getPrice()));

        Layout.drawList(g2,
                exchangeOrderRecords,
                startX,
                startY,
                startY + EXCHANGE_ORDER_RECT_HEIGHT,
                Constants.SMALL_FONT_SIZE,
                Color.black);

        g2.setColor(Color.cyan);
        g2.fillRect(startX, startY + EXCHANGE_ORDER_RECT_HEIGHT, cellSize, cellSize - EXCHANGE_ORDER_RECT_HEIGHT);

        Layout.drawList(g2,
                new ArrayList<>(Collections.singletonList(Double.toString(item.getActivationPrice()))),
                0,
                startY + EXCHANGE_ORDER_RECT_HEIGHT,
                startY + cellSize,
                Constants.SMALL_FONT_SIZE,
                Color.black);
    }

    private void drawTransactionSettlement(Layout.DrawParameters<TransactionSettlement> drawParameters) {
        Graphics2D g2 = drawParameters.g2();
        TransactionSettlement item = drawParameters.item();
        int startX = (int)drawParameters.startX();
        int startY = (int)drawParameters.startY();
        int cellSize = (int)drawParameters.cellSize();

        g2.setColor(Color.darkGray);
        g2.fillRect(startX, startY, cellSize, cellSize - EXCHANGE_ORDER_RECT_HEIGHT);

        List<String> transactionSettlementRecords = new ArrayList<>();
        transactionSettlementRecords.add(String.format("%s$ to pay, %s$ to withdraw", item.getToPay(), item.getToWithdraw()));
        transactionSettlementRecords.add(String.format("%s bought, %s sold", item.getBoughtStock(), item.getSoldStock()));

        Layout.drawList(g2,
                transactionSettlementRecords,
                startX,
                startY,
                startY + cellSize - EXCHANGE_ORDER_RECT_HEIGHT,
                Constants.SMALL_FONT_SIZE,
                Color.white);

        g2.setColor(Color.pink);
        g2.fillRect(startX, startY + cellSize - EXCHANGE_ORDER_RECT_HEIGHT, cellSize, EXCHANGE_ORDER_RECT_HEIGHT);

        List<String> exchangeOrderRecords = new ArrayList<>();
        exchangeOrderRecords.add(String.format("x%s", item.getQuantity()));
        exchangeOrderRecords.add(String.format("%s$", item.getUnitPrice()));

        Layout.drawList(g2,
                exchangeOrderRecords,
                startX,
                startY + cellSize - EXCHANGE_ORDER_RECT_HEIGHT,
                startY + cellSize,
                Constants.SMALL_FONT_SIZE,
                Color.black);
    }

    private void drawCancelledOrder(Layout.DrawParameters<OrderSubmitter> drawParameters) {
        Graphics2D g2 = drawParameters.g2();
        OrderSubmitter item = drawParameters.item();
        int startX = (int)drawParameters.startX();
        int startY = (int)drawParameters.startY();
        int cellSize = (int)drawParameters.cellSize();

        g2.setColor(Color.red);
        g2.fillRect(startX, startY, cellSize, cellSize);

        List<String> orderSubmitterRecords = new ArrayList<>();
        orderSubmitterRecords.add(StringUtils.hideString(item.getSubmitterName(), 10));
        orderSubmitterRecords.add(StringUtils.hideString(item.getSubmitterBroker(), 10));
        Layout.drawList(g2,
                orderSubmitterRecords,
                startX,
                startY,
                startY + cellSize,
                Constants.SMALL_FONT_SIZE,
                Color.black);
    }
}

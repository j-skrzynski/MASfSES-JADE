package org.example.visualization.windowpanels;

import org.example.logic.stockexchange.order.awaitingorder.AwaitingExchangeOrder;
import org.example.logic.stockexchange.order.marketorder.ExchangeOrder;
import org.example.logic.stockexchange.settlements.TransactionSettlement;
import org.example.logic.stockexchange.utils.OrderSubmitter;
import org.example.visualization.viewmodels.StockExchangeViewModel;

import java.awt.*;
import java.util.Queue;

public class StockExchangeWindowPanel extends BaseWindowPanel<StockExchangeViewModel> {
    private static final int PANEL_WIDTH = 1000;
    private static final int PANEL_HEIGHT = 750;

    private static final int FONT_SIZE = 10;

    private static final int PADDING_LEFT = 50;
    private static final int PADDING_TOP = 50;
    private static final int TEXT_PADDING_LEFT = 2;
    private static final int QUEUE_LABEL_PADDING_TOP = 10;

    private static final int EXCHANGE_ORDER_RECT_WIDTH = 40;
    private static final int EXCHANGE_ORDER_RECT_HEIGHT = 40;

    private static final int ACTIVATION_PRICE_RECT_WIDTH = 40;
    private static final int ACTIVATION_PRICE_RECT_HEIGHT = 20;

    private static final int TRANSACTION_SETTLEMENT_RECT_WIDTH = 100;
    private static final int TRANSACTION_SETTLEMENT_RECT_HEIGHT = 40;

    private static final int ORDER_SUBMITTER_RECT_WIDTH = 100;
    private static final int ORDER_SUBMITTER_RECT_HEIGHT = 40;

    private static final int QUEUE_HORIZONTAL_GAP = 10;
    private static final int QUEUE_VERTICAL_GAP = 30;

    public StockExchangeWindowPanel(StockExchangeViewModel initialModel) {
        super(PANEL_WIDTH, PANEL_HEIGHT, initialModel);
    }

    @Override
    protected void draw(Graphics2D g2, StockExchangeViewModel newValue) {
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.black);
            g2.setFont(new Font("TimesRoman", Font.PLAIN, FONT_SIZE));

            int startY = PADDING_TOP;

            drawQueueHeading(g2, "Buy orders queue", startY);
            drawExchangeOrders(newValue.getBuyOrders(), g2, startY);
            startY += EXCHANGE_ORDER_RECT_HEIGHT + QUEUE_VERTICAL_GAP;

            drawQueueHeading(g2, "Sell orders queue", startY);
            drawExchangeOrders(newValue.getSellOrders(), g2, startY);
            startY += EXCHANGE_ORDER_RECT_HEIGHT + QUEUE_VERTICAL_GAP;

            drawQueueHeading(g2, "No limit buy queue", startY);
            drawExchangeOrders(newValue.getNoLimitBuy(), g2, startY);
            startY += EXCHANGE_ORDER_RECT_HEIGHT + QUEUE_VERTICAL_GAP;

            drawQueueHeading(g2, "No limit sell queue", startY);
            drawExchangeOrders(newValue.getNoLimitSell(), g2, startY);
            startY += EXCHANGE_ORDER_RECT_HEIGHT + QUEUE_VERTICAL_GAP;

            drawQueueHeading(g2, "Awaiting activation buy queue", startY);
            drawAwaitingExchangeOrders(newValue.getAwaitingActivationBuy(), g2, startY);
            startY += EXCHANGE_ORDER_RECT_HEIGHT + ACTIVATION_PRICE_RECT_HEIGHT + QUEUE_VERTICAL_GAP;

            drawQueueHeading(g2, "Awaiting activation sell queue", startY);
            drawAwaitingExchangeOrders(newValue.getAwaitingActivationSell(), g2, startY);
            startY += EXCHANGE_ORDER_RECT_HEIGHT + ACTIVATION_PRICE_RECT_HEIGHT + QUEUE_VERTICAL_GAP;

            drawQueueHeading(g2, "Settlements to send queue", startY);
            drawSettlementsToSend(newValue.getSettlementsToSend(), g2, startY);
            startY += TRANSACTION_SETTLEMENT_RECT_HEIGHT + EXCHANGE_ORDER_RECT_HEIGHT + QUEUE_VERTICAL_GAP;

            drawQueueHeading(g2, "Canceled orders queue", startY);
            drawCancelledOrders(newValue.getCanceledOrders(), g2, startY);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void drawQueueHeading(Graphics2D g2, String queueName, int y) {
        drawHorizontalLine(g2, y - QUEUE_VERTICAL_GAP / 2);
        g2.drawString(queueName, PADDING_LEFT, y - QUEUE_VERTICAL_GAP / 2 + QUEUE_LABEL_PADDING_TOP);
    }

    private void drawHorizontalLine(Graphics2D g2, int y) {
        g2.setColor(Color.black);
        g2.drawLine(0, y, PANEL_WIDTH / 2, y);
    }

    private void drawExchangeOrders(Queue<ExchangeOrder> exchangeOrders, Graphics2D g2, int y) {
        int x = PADDING_LEFT;
        for (ExchangeOrder eo : exchangeOrders) {
            drawExchangeOrder(eo, g2, x, y);

            x += EXCHANGE_ORDER_RECT_WIDTH + QUEUE_HORIZONTAL_GAP;
        }
    }

    private void drawAwaitingExchangeOrders(Queue<AwaitingExchangeOrder> awaitingExchangeOrders, Graphics2D g2, int y) {
        int x = PADDING_LEFT;
        for (AwaitingExchangeOrder eo : awaitingExchangeOrders) {
            drawExchangeOrder(eo.getActivatedOrder(), g2, x, y);
            drawActivationPrice(eo.getActivationPrice(), g2, x, y + EXCHANGE_ORDER_RECT_HEIGHT);

            x += EXCHANGE_ORDER_RECT_WIDTH + QUEUE_HORIZONTAL_GAP;
        }
    }

    private void drawSettlementsToSend(Queue<TransactionSettlement> settlementsToSend, Graphics2D g2, int y) {
        int x = PADDING_LEFT;
        for (TransactionSettlement ts : settlementsToSend) {
            g2.setColor(Color.darkGray);
            g2.fillRect(x, y, TRANSACTION_SETTLEMENT_RECT_WIDTH, TRANSACTION_SETTLEMENT_RECT_HEIGHT);

            g2.setColor(Color.white);
            g2.drawString(String.format("%s$ to pay, %s$ to withdraw", ts.getToPay(), ts.getToWithdraw()), x + TEXT_PADDING_LEFT, y + TRANSACTION_SETTLEMENT_RECT_HEIGHT / 3);
            g2.drawString(String.format("%s bought, %s sold", ts.getBoughtStock(), ts.getSoldStock()), x + TEXT_PADDING_LEFT, y + TRANSACTION_SETTLEMENT_RECT_HEIGHT * 2 / 3);

            g2.setColor(Color.pink);
            g2.fillRect(x, y + TRANSACTION_SETTLEMENT_RECT_HEIGHT, TRANSACTION_SETTLEMENT_RECT_WIDTH, EXCHANGE_ORDER_RECT_HEIGHT);

            g2.setColor(Color.black);
            g2.drawString(String.format("x%s", ts.getQuantity()), x + TEXT_PADDING_LEFT, y + TRANSACTION_SETTLEMENT_RECT_HEIGHT + EXCHANGE_ORDER_RECT_HEIGHT / 3);
            g2.drawString(String.format("%s$", ts.getUnitPrice()), x + TEXT_PADDING_LEFT, y + TRANSACTION_SETTLEMENT_RECT_HEIGHT + EXCHANGE_ORDER_RECT_HEIGHT * 2 / 3);

            x += TRANSACTION_SETTLEMENT_RECT_WIDTH + QUEUE_HORIZONTAL_GAP;
        }
    }

    private void drawCancelledOrders(Queue<OrderSubmitter> cancelledOrders, Graphics2D g2, int y) {
        int x = PADDING_LEFT;
        for (OrderSubmitter os : cancelledOrders) {
            g2.setColor(Color.red);
            g2.fillRect(x, y, ORDER_SUBMITTER_RECT_WIDTH, ORDER_SUBMITTER_RECT_HEIGHT);

            g2.setColor(Color.black);
            g2.drawString(os.getSubmitterName(), x + TEXT_PADDING_LEFT, y + ORDER_SUBMITTER_RECT_HEIGHT / 3);
            g2.drawString(shortenedSubmitterBrokerString(os), x + TEXT_PADDING_LEFT, y + ORDER_SUBMITTER_RECT_HEIGHT * 2 / 3);

            x += ORDER_SUBMITTER_RECT_WIDTH + QUEUE_HORIZONTAL_GAP;
        }
    }

    private String shortenedSubmitterBrokerString(OrderSubmitter os) {
        if (os.getSubmitterBroker().length() <= Math.max(os.getSubmitterName().length(), 3)) {
            return os.getSubmitterBroker();
        }

        return os.getSubmitterBroker().substring(os.getSubmitterName().length() - 3) + "...";
    }

    private void drawExchangeOrder(ExchangeOrder eo, Graphics2D g2, int x, int y) {
        g2.setColor(Color.pink);
        g2.fillRect(x, y, EXCHANGE_ORDER_RECT_WIDTH, EXCHANGE_ORDER_RECT_HEIGHT);

        g2.setColor(Color.black);
        g2.drawString(String.format("x%s", eo.getQuantity()), x + TEXT_PADDING_LEFT, y + EXCHANGE_ORDER_RECT_HEIGHT / 3);
        g2.drawString(String.format("%s$", eo.getPrice()), x + TEXT_PADDING_LEFT, y + EXCHANGE_ORDER_RECT_HEIGHT * 2 / 3);
    }

    private void drawActivationPrice(double activationPrice, Graphics2D g2, int x, int y) {
        g2.setColor(Color.cyan);
        g2.fillRect(x, y, ACTIVATION_PRICE_RECT_WIDTH, ACTIVATION_PRICE_RECT_HEIGHT);

        g2.setColor(Color.black);
        g2.drawString(Double.toString(activationPrice), x + TEXT_PADDING_LEFT, y + ACTIVATION_PRICE_RECT_HEIGHT / 2);
    }
}

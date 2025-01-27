package org.example.visualization.utils;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;

public class StatisticalGraphics {
    private static final double RELATIVE_PADDING = 0.05;

    private static final double AXIS_ARROW_ANGLE = Math.PI / 6;
    private static final double AXIS_ARROW_LENGTH = 10;

    public static void plot2D(Graphics2D g2,
                              Double[] y,
                              int upperLeftX,
                              int upperLeftY,
                              int width,
                              int height) {
        g2.setColor(Color.white);

        g2.fillRect(upperLeftX, upperLeftY, width, height);

        g2.setColor(Color.black);

        if (y.length < 1) {
            return;
        }

        int startX = (int)(upperLeftX + width * RELATIVE_PADDING);
        int endX = (int)(upperLeftX + width * (1 - RELATIVE_PADDING));
        int startY = (int)(upperLeftY + height * RELATIVE_PADDING);
        int endY = (int)(upperLeftY + height * (1 - RELATIVE_PADDING));

        double minY = Collections.min(Arrays.asList(y));
        double maxY = Collections.max(Arrays.asList(y));

        int originY = (int)(startY + getOriginYRatio(minY, maxY) * (endY - startY));

        // axes

        g2.drawLine(startX, originY, endX, originY);
        g2.drawLine(startX, endY, startX, startY);

        // axis arrows

        int alongArrowDelta = (int)(AXIS_ARROW_LENGTH * Math.cos(AXIS_ARROW_ANGLE));
        int acrossArrowDelta = (int)(AXIS_ARROW_LENGTH * Math.sin(AXIS_ARROW_ANGLE));

        g2.drawLine(endX, originY, endX - alongArrowDelta, originY - acrossArrowDelta);
        g2.drawLine(endX, originY, endX - alongArrowDelta, originY + acrossArrowDelta);

        g2.drawLine(startX, startY, startX - acrossArrowDelta, startY + alongArrowDelta);
        g2.drawLine(startX, startY, startX + acrossArrowDelta, startY + alongArrowDelta);

        // dashes

        if (y.length < 2) {
            return;
        }

        int xAxisLength = endX - startX - alongArrowDelta;
        for (int i = 0; i < y.length; i++) {
            int dashX = startX + i * xAxisLength / (y.length - 1);
            g2.drawLine(dashX, originY + acrossArrowDelta, dashX, originY - acrossArrowDelta);
        }

        int yAxisLength = endY - startY - alongArrowDelta;

        g2.drawLine(startX - acrossArrowDelta, endY - yAxisLength, startX + acrossArrowDelta, endY - yAxisLength);
        g2.drawString(Double.toString(maxY), startX + acrossArrowDelta, endY - yAxisLength);

        g2.drawLine(startX - acrossArrowDelta, endY, startX + acrossArrowDelta, endY);
        g2.drawString(Double.toString(minY), startX + acrossArrowDelta, endY);

        // draw coordinates

        g2.setColor(Color.red);

        for (int i = 0; i < y.length - 1; i++) {
            int startPointX = startX + i * xAxisLength / (y.length - 1);
            double startYRatio = (maxY - y[i]) / (maxY - minY);
            int startPointY = (int)((endY - yAxisLength) + startYRatio * yAxisLength);

            int endPointX = startX + (i + 1) * xAxisLength / (y.length - 1);
            double endYRatio = (maxY - y[i + 1]) / (maxY - minY);
            int endPointY = (int)((endY - yAxisLength) + endYRatio * yAxisLength);

            g2.drawLine(startPointX, startPointY, endPointX, endPointY);
        }

        g2.setColor(Color.black);
    }

    private static double getOriginYRatio(double minY, double maxY) {
        if (minY >= 0) {
            return 1;
        }

        if (maxY <= 0) {
            return 0;
        }

        return maxY / (maxY - minY);
    }
}

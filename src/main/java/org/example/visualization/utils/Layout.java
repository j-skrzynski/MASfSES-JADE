package org.example.visualization.utils;

import org.example.visualization.Constants;
import org.glassfish.pfl.basic.contain.Triple;

import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class Layout {
    private static final int CELL_PADDING = 2;

    public record DrawParameters<T>(T item, Graphics2D g2, double startX, double startY, double cellSize) { }

    public static <T> void drawGrid(Graphics2D g2,
                                    T[] items,
                                    Consumer<DrawParameters<T>> drawFunc,
                                    int startX,
                                    int startY,
                                    double gridWidth,
                                    double gridHeight) {
        g2.setColor(Color.black);

        Triple<Double, Integer, Integer> gridConfiguration = getGridConfiguration(gridWidth, gridHeight, items.length);
        double cellSize = gridConfiguration.first();
        int rows = gridConfiguration.second();
        int columns = gridConfiguration.third();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                int i = row * columns + col;
                if (i >= items.length) {
                    return;
                }

                DrawParameters<T> drawParameters = new DrawParameters<>(items[i],
                        g2,
                        startX + col * cellSize + CELL_PADDING,
                        startY + row * cellSize + CELL_PADDING,
                        cellSize - 2 * CELL_PADDING);
                drawFunc.accept(drawParameters);
            }
        }
    }

    public static void drawList(Graphics2D g2,
                                List<String> items,
                                int startX,
                                int startY,
                                int endY,
                                int fontSize,
                                Color fontColor) {
        g2.setColor(fontColor);
        Graphics.setFontSize(g2, fontSize);

        int n = items.size();
        for (int i = 1; i < n + 1; i++) {
            float recordY = startY + (float) ((endY - startY) * i) / (n + 1);
            g2.drawString(items.get(i - 1), startX + Constants.TEXT_PADDING_LEFT, recordY);
        }

        g2.setColor(Color.black);
        Graphics.setFontSize(g2, Constants.DEFAULT_FONT_SIZE);
    }

    public static void drawText(Graphics2D g2, String text, int startX, int startY) {
        g2.drawString(text, startX + Constants.TEXT_PADDING_LEFT, startY);
    }

    private static Triple<Double, Integer, Integer> getGridConfiguration(double gridWidth, double gridHeight, int n) {
        // k is grid division factor
        int k = 1;

        while (k * Math.floor(k * Math.max(gridWidth, gridHeight) / Math.min(gridWidth, gridHeight)) <= n) {
            k++;
        }

        double cellSize = Math.min(gridWidth, gridHeight) / k;
        int rows = (gridHeight <= gridWidth) ? k : (int)Math.floor(gridHeight / cellSize);
        int columns = (gridWidth <= gridHeight) ? k : (int)Math.floor(gridWidth / cellSize);

        return new Triple<>(cellSize, rows, columns);
    }
}

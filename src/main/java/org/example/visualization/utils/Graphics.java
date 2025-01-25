package org.example.visualization.utils;

import java.awt.*;

public class Graphics {
    public static void setFontSize(Graphics2D g2, int fontSize) {
        g2.setFont(new Font("TimesRoman", Font.PLAIN, fontSize));
    }
}

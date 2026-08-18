package com.nokia.mid.ui;

import javax.microedition.lcdui.Graphics;

public final class DirectUtils {
    private DirectUtils() {}

    public static DirectGraphics getDirectGraphics(Graphics g) {
        return (xPoints, xOffset, yPoints, yOffset, nPoints, argbColor) ->
                g.fillPolygon(xPoints, xOffset, yPoints, yOffset, nPoints, argbColor);
    }
}

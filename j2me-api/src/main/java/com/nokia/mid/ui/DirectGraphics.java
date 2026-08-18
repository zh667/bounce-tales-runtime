package com.nokia.mid.ui;

public interface DirectGraphics {
    int FLIP_HORIZONTAL = 0x2000;
    int FLIP_VERTICAL = 0x4000;
    int ROTATE_90 = 90;
    int ROTATE_180 = 180;
    int ROTATE_270 = 270;

    void fillPolygon(int[] xPoints, int xOffset, int[] yPoints, int yOffset, int nPoints, int argbColor);
}

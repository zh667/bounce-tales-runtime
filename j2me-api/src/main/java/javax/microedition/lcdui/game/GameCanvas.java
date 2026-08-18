package javax.microedition.lcdui.game;

import java.awt.image.BufferedImage;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;

public class GameCanvas extends Canvas {
    public static final int UP_PRESSED = 1 << Canvas.UP;
    public static final int DOWN_PRESSED = 1 << Canvas.DOWN;
    public static final int LEFT_PRESSED = 1 << Canvas.LEFT;
    public static final int RIGHT_PRESSED = 1 << Canvas.RIGHT;
    public static final int FIRE_PRESSED = 1 << Canvas.FIRE;

    private final BufferedImage buffer;
    private final BufferedImage present;
    private final Graphics graphics;
    private final Object presentLock = new Object();

    public GameCanvas(boolean suppressKeyEvents) {
        int width = Math.max(1, getWidth());
        int height = Math.max(1, getHeight());
        buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        present = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g2 = buffer.createGraphics();
        g2.setComposite(java.awt.AlphaComposite.SrcOver);
        graphics = new Graphics(g2);
    }

    protected Graphics getGraphics() {
        return graphics;
    }

    public void flushGraphics() {
        synchronized (presentLock) {
            java.awt.Graphics g = present.getGraphics();
            g.drawImage(buffer, 0, 0, null);
            g.dispose();
        }
        Display.getDisplay(null).flush();
    }

    @Override
    protected void paint(Graphics g) {
        synchronized (presentLock) {
            g.g2.drawImage(present, 0, 0, null);
        }
    }

    public BufferedImage buffer() {
        return buffer;
    }

    /** Last completed frame. Safe for the EDT to blit while the game draws the back buffer. */
    public void blitPresent(java.awt.Graphics g, int dx, int dy, int dw, int dh) {
        synchronized (presentLock) {
            g.drawImage(present, dx, dy, dw, dh, null);
        }
    }
}

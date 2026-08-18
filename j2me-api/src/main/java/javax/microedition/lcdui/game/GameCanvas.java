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
    private final Graphics graphics;

    public GameCanvas(boolean suppressKeyEvents) {
        buffer = new BufferedImage(Math.max(1, getWidth()), Math.max(1, getHeight()), BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2 = buffer.createGraphics();
        g2.setComposite(java.awt.AlphaComposite.SrcOver);
        graphics = new Graphics(g2);
    }

    protected Graphics getGraphics() {
        return graphics;
    }

    public void flushGraphics() {
        Display.getDisplay(null).flush();
    }

    @Override
    protected void paint(Graphics g) {
        g.g2.drawImage(buffer, 0, 0, null);
    }

    public BufferedImage buffer() {
        return buffer;
    }
}

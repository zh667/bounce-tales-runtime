package javax.microedition.lcdui.game;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.image.BufferedImage;
import javax.microedition.lcdui.Graphics;
import org.junit.jupiter.api.Test;

class GameCanvasTest {
    @Test
    void flushCopiesBackBufferSoLaterDrawsDoNotChangePresent() {
        GameCanvas canvas = new GameCanvas(false);
        Graphics g = canvas.getGraphics();
        g.setColor(0xFF0000);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        canvas.flushGraphics();

        g.setColor(0x0000FF);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        BufferedImage shot = new BufferedImage(canvas.getWidth(), canvas.getHeight(), BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics blit = shot.createGraphics();
        canvas.blitPresent(blit, 0, 0, canvas.getWidth(), canvas.getHeight());
        blit.dispose();
        assertEquals(0xFFFF0000, shot.getRGB(0, 0));
    }
}

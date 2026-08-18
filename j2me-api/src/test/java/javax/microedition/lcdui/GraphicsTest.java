package javax.microedition.lcdui;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.image.BufferedImage;
import org.junit.jupiter.api.Test;

class GraphicsTest {
    @Test
    void drawRegionClampsToSourceBounds() {
        Image src = Image.createImage(8, 8);
        Graphics srcG = src.getGraphics();
        srcG.setColor(0xFF0000);
        srcG.fillRect(0, 0, 8, 8);
        BufferedImage dest = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics g = new Graphics(dest.createGraphics());
        assertDoesNotThrow(() -> g.drawRegion(src, -2, -2, 40, 40, 0, 0, 0, Graphics.TOP | Graphics.LEFT));
        assertEquals(8, src.getWidth());
        assertEquals(0xFFFF0000, dest.getRGB(2, 2));
    }
}

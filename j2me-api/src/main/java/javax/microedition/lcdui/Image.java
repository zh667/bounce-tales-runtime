package javax.microedition.lcdui;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class Image {
    final BufferedImage raster;
    final boolean mutable;

    Image(BufferedImage raster, boolean mutable) {
        this.raster = raster;
        this.mutable = mutable;
    }

    public static Image createImage(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException();
        }
        BufferedImage buf = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g = buf.createGraphics();
        g.setColor(java.awt.Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.dispose();
        return new Image(buf, true);
    }

    public static Image createImage(Image source) {
        if (source == null) {
            throw new NullPointerException();
        }
        BufferedImage copy = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g = copy.createGraphics();
        g.drawImage(source.raster, 0, 0, null);
        g.dispose();
        return new Image(copy, false);
    }

    public static Image createImage(String name) throws IOException {
        if (name == null) {
            throw new NullPointerException();
        }
        String path = name.startsWith("/") ? name : "/" + name;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream in = loader == null ? null : loader.getResourceAsStream(path.startsWith("/") ? path.substring(1) : path);
        if (in == null) {
            in = Image.class.getResourceAsStream(path);
        }
        if (in == null) {
            throw new IOException("image not found: " + name);
        }
        try (InputStream stream = in) {
            BufferedImage decoded = ImageIO.read(stream);
            if (decoded == null) {
                throw new IOException("undecodable image: " + name);
            }
            return new Image(decoded, false);
        }
    }

    public static Image createImage(byte[] imageData, int imageOffset, int imageLength) {
        try {
            BufferedImage decoded = ImageIO.read(new ByteArrayInputStream(imageData, imageOffset, imageLength));
            if (decoded == null) {
                throw new IllegalArgumentException("undecodable image");
            }
            return new Image(decoded, false);
        } catch (IOException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    public static Image createRGBImage(int[] rgb, int width, int height, boolean processAlpha) {
        BufferedImage buf = new BufferedImage(
                width, height, processAlpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
        buf.setRGB(0, 0, width, height, rgb, 0, width);
        return new Image(buf, false);
    }

    public Graphics getGraphics() {
        if (!mutable) {
            throw new IllegalStateException("immutable");
        }
        return new Graphics(raster.createGraphics());
    }

    public int getWidth() {
        return raster.getWidth();
    }

    public int getHeight() {
        return raster.getHeight();
    }

    public void getRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height) {
        raster.getRGB(x, y, width, height, rgbData, offset, scanlength);
    }

    public BufferedImage awtImage() {
        return raster;
    }
}

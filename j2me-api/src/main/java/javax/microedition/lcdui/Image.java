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
        BufferedImage buf = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
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
        try (InputStream stream = in instanceof java.io.BufferedInputStream
                ? in
                : new java.io.BufferedInputStream(in)) {
            BufferedImage decoded = ImageIO.read(stream);
            if (decoded == null) {
                throw new IOException("undecodable image: " + name);
            }
            return copyArgb(decoded, false);
        }
    }

    public static Image createImage(byte[] imageData, int imageOffset, int imageLength) {
        try {
            int start = imageOffset;
            int length = imageLength;
            int magic = findImageMagic(imageData, imageOffset, imageLength);
            if (magic >= 0) {
                start = magic;
                length = imageOffset + imageLength - magic;
            }
            BufferedImage decoded = ImageIO.read(new ByteArrayInputStream(imageData, start, length));
            if (decoded == null) {
                throw new IllegalArgumentException("undecodable image");
            }
            return copyArgb(decoded, false);
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

    private static Image copyArgb(BufferedImage source, boolean mutable) {
        BufferedImage argb = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g = argb.createGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return new Image(argb, mutable);
    }

    /** Packed slices sometimes have a few bytes before a PNG/JPEG payload. */
    static int findImageMagic(byte[] data, int offset, int length) {
        int end = Math.min(data.length, offset + length) - 2;
        for (int i = offset; i <= end; i++) {
            int b0 = data[i] & 0xFF;
            int b1 = data[i + 1] & 0xFF;
            if (b0 == 0x89 && i + 3 < offset + length && b1 == 0x50 && (data[i + 2] & 0xFF) == 0x4E && (data[i + 3] & 0xFF) == 0x47) {
                return i;
            }
            if (b0 == 0xFF && b1 == 0xD8) {
                return i;
            }
        }
        return -1;
    }
}

package javax.microedition.lcdui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.microedition.lcdui.game.Sprite;

public class Graphics {
    public static final int HCENTER = 1;
    public static final int VCENTER = 2;
    public static final int LEFT = 4;
    public static final int RIGHT = 8;
    public static final int TOP = 16;
    public static final int BOTTOM = 32;
    public static final int BASELINE = 64;
    public static final int SOLID = 0;
    public static final int DOTTED = 1;

    public final Graphics2D g2;
    private Font font = Font.getDefaultFont();
    private int translateX;
    private int translateY;

    public Graphics(java.awt.Graphics graphics) {
        this.g2 = (Graphics2D) graphics;
        this.g2.setFont(font.awtFont);
    }

    public void translate(int x, int y) {
        translateX += x;
        translateY += y;
        g2.translate(x, y);
    }

    public int getTranslateX() {
        return translateX;
    }

    public int getTranslateY() {
        return translateY;
    }

    public void setColor(int rgb) {
        g2.setColor(new Color(rgb & 0xFFFFFF));
    }

    public void setColor(int r, int g, int b) {
        g2.setColor(new Color(r, g, b));
    }

    public int getColor() {
        return g2.getColor().getRGB() & 0xFFFFFF;
    }

    public void setFont(Font font) {
        this.font = font == null ? Font.getDefaultFont() : font;
        g2.setFont(this.font.awtFont);
    }

    public Font getFont() {
        return font;
    }

    public void setStrokeStyle(int style) {}

    public void setClip(int x, int y, int width, int height) {
        g2.setClip(x, y, width, height);
    }

    public void clipRect(int x, int y, int width, int height) {
        g2.clipRect(x, y, width, height);
    }

    public int getClipX() {
        java.awt.Rectangle clip = g2.getClipBounds();
        return clip == null ? 0 : clip.x;
    }

    public int getClipY() {
        java.awt.Rectangle clip = g2.getClipBounds();
        return clip == null ? 0 : clip.y;
    }

    public int getClipWidth() {
        java.awt.Rectangle clip = g2.getClipBounds();
        return clip == null ? Display.screenWidth() : clip.width;
    }

    public int getClipHeight() {
        java.awt.Rectangle clip = g2.getClipBounds();
        return clip == null ? Display.screenHeight() : clip.height;
    }

    public void fillRect(int x, int y, int width, int height) {
        g2.fillRect(x, y, width, height);
    }

    public void drawRect(int x, int y, int width, int height) {
        g2.drawRect(x, y, width, height);
    }

    public void drawLine(int x1, int y1, int x2, int y2) {
        g2.drawLine(x1, y1, x2, y2);
    }

    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        g2.fillArc(x, y, width, height, startAngle, arcAngle);
    }

    public void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3) {
        g2.fillPolygon(new int[] {x1, x2, x3}, new int[] {y1, y2, y3}, 3);
    }

    public void drawString(String str, int x, int y, int anchor) {
        drawSubstring(str, 0, str.length(), x, y, anchor);
    }

    public void drawSubstring(String str, int offset, int len, int x, int y, int anchor) {
        String slice = str.substring(offset, offset + len);
        int w = font.stringWidth(slice);
        int h = font.getHeight();
        int ax = x;
        int ay = y;
        if ((anchor & HCENTER) != 0) {
            ax -= w / 2;
        } else if ((anchor & RIGHT) != 0) {
            ax -= w;
        }
        if ((anchor & VCENTER) != 0) {
            ay += h / 2;
        } else if ((anchor & TOP) != 0) {
            ay += font.getBaselinePosition();
        } else if ((anchor & BOTTOM) != 0) {
            ay -= h - font.getBaselinePosition();
        }
        g2.drawString(slice, ax, ay);
    }

    public void drawImage(Image img, int x, int y, int anchor) {
        if (img == null) {
            throw new NullPointerException();
        }
        int ax = x;
        int ay = y;
        if ((anchor & HCENTER) != 0) {
            ax -= img.getWidth() / 2;
        } else if ((anchor & RIGHT) != 0) {
            ax -= img.getWidth();
        }
        if ((anchor & VCENTER) != 0) {
            ay -= img.getHeight() / 2;
        } else if ((anchor & BOTTOM) != 0) {
            ay -= img.getHeight();
        }
        g2.drawImage(img.raster, ax, ay, null);
    }

    public void drawRegion(
            Image src,
            int xSrc,
            int ySrc,
            int width,
            int height,
            int transform,
            int xDest,
            int yDest,
            int anchor) {
        if (src == null || width <= 0 || height <= 0) {
            return;
        }
        int sx = xSrc;
        int sy = ySrc;
        int sw = width;
        int sh = height;
        int dx = xDest;
        int dy = yDest;
        if (sx < 0) {
            sw += sx;
            dx -= sx;
            sx = 0;
        }
        if (sy < 0) {
            sh += sy;
            dy -= sy;
            sy = 0;
        }
        if (sx + sw > src.getWidth()) {
            sw = src.getWidth() - sx;
        }
        if (sy + sh > src.getHeight()) {
            sh = src.getHeight() - sy;
        }
        if (sw <= 0 || sh <= 0) {
            return;
        }
        int ax = dx;
        int ay = dy;
        if (transform == Sprite.TRANS_NONE) {
            if ((anchor & HCENTER) != 0) {
                ax -= sw / 2;
            } else if ((anchor & RIGHT) != 0) {
                ax -= sw;
            }
            if ((anchor & VCENTER) != 0) {
                ay -= sh / 2;
            } else if ((anchor & BOTTOM) != 0) {
                ay -= sh;
            }
            g2.drawImage(src.raster, ax, ay, ax + sw, ay + sh, sx, sy, sx + sw, sy + sh, null);
            return;
        }
        BufferedImage region = copyRegion(src.raster, sx, sy, sw, sh);
        BufferedImage transformed = applyTransform(region, transform);
        ax = dx;
        ay = dy;
        if ((anchor & HCENTER) != 0) {
            ax -= transformed.getWidth() / 2;
        } else if ((anchor & RIGHT) != 0) {
            ax -= transformed.getWidth();
        }
        if ((anchor & VCENTER) != 0) {
            ay -= transformed.getHeight() / 2;
        } else if ((anchor & BOTTOM) != 0) {
            ay -= transformed.getHeight();
        }
        g2.drawImage(transformed, ax, ay, null);
    }

    /** Copy pixels instead of {@code getSubimage}; child rasters can blit at the wrong offset. */
    static BufferedImage copyRegion(BufferedImage src, int x, int y, int width, int height) {
        BufferedImage copy = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        int[] rgb = new int[width * height];
        src.getRGB(x, y, width, height, rgb, 0, width);
        copy.setRGB(0, 0, width, height, rgb, 0, width);
        return copy;
    }

    public void drawRGB(
            int[] rgbData, int offset, int scanlength, int x, int y, int width, int height, boolean processAlpha) {
        if (width <= 0 || height <= 0) {
            return;
        }
        BufferedImage img = new BufferedImage(
                width, height, processAlpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
        img.setRGB(0, 0, width, height, rgbData, offset, scanlength);
        g2.drawImage(img, x, y, null);
    }

    public void fillPolygon(int[] xPoints, int xOffset, int[] yPoints, int yOffset, int nPoints, int argb) {
        int[] xs = new int[nPoints];
        int[] ys = new int[nPoints];
        for (int i = 0; i < nPoints; i++) {
            xs[i] = xPoints[xOffset + i];
            ys[i] = yPoints[yOffset + i];
        }
        Color previous = g2.getColor();
        g2.setColor(new Color(argb, true));
        g2.fillPolygon(xs, ys, nPoints);
        g2.setColor(previous);
    }

    private static BufferedImage applyTransform(BufferedImage src, int transform) {
        if (transform == Sprite.TRANS_NONE) {
            return src;
        }
        int w = src.getWidth();
        int h = src.getHeight();
        boolean swap = transform == Sprite.TRANS_ROT90
                || transform == Sprite.TRANS_ROT270
                || transform == Sprite.TRANS_MIRROR_ROT90
                || transform == Sprite.TRANS_MIRROR_ROT270;
        int dw = swap ? h : w;
        int dh = swap ? w : h;
        BufferedImage out = new BufferedImage(dw, dh, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        AffineTransform tx = new AffineTransform();
        switch (transform) {
            case Sprite.TRANS_ROT90 -> {
                tx.translate(dh, 0);
                tx.rotate(Math.PI / 2);
            }
            case Sprite.TRANS_ROT180 -> {
                tx.translate(w, h);
                tx.rotate(Math.PI);
            }
            case Sprite.TRANS_ROT270 -> {
                tx.translate(0, dw);
                tx.rotate(-Math.PI / 2);
            }
            case Sprite.TRANS_MIRROR -> {
                tx.translate(w, 0);
                tx.scale(-1, 1);
            }
            case Sprite.TRANS_MIRROR_ROT90 -> {
                tx.translate(dh, 0);
                tx.rotate(Math.PI / 2);
                tx.translate(w, 0);
                tx.scale(-1, 1);
            }
            case Sprite.TRANS_MIRROR_ROT180 -> {
                tx.translate(0, h);
                tx.scale(1, -1);
            }
            case Sprite.TRANS_MIRROR_ROT270 -> {
                tx.translate(0, dw);
                tx.rotate(-Math.PI / 2);
                tx.translate(w, 0);
                tx.scale(-1, 1);
            }
            default -> {
            }
        }
        g.drawImage(src, tx, null);
        g.dispose();
        return out;
    }
}

package javax.microedition.lcdui;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public final class Font {
    public static final int FACE_SYSTEM = 0;
    public static final int FACE_MONOSPACE = 32;
    public static final int FACE_PROPORTIONAL = 64;
    public static final int STYLE_PLAIN = 0;
    public static final int STYLE_BOLD = 1;
    public static final int STYLE_ITALIC = 2;
    public static final int STYLE_UNDERLINED = 4;
    public static final int SIZE_SMALL = 8;
    public static final int SIZE_MEDIUM = 0;
    public static final int SIZE_LARGE = 16;

    final java.awt.Font awtFont;
    private final FontMetrics metrics;

    private Font(java.awt.Font awtFont) {
        this.awtFont = awtFont;
        BufferedImage probe = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = probe.createGraphics();
        this.metrics = g.getFontMetrics(awtFont);
        g.dispose();
    }

    public static Font getDefaultFont() {
        return getFont(FACE_SYSTEM, STYLE_PLAIN, SIZE_MEDIUM);
    }

    public static Font getFont(int face, int style, int size) {
        int awtStyle = java.awt.Font.PLAIN;
        if ((style & STYLE_BOLD) != 0) {
            awtStyle |= java.awt.Font.BOLD;
        }
        if ((style & STYLE_ITALIC) != 0) {
            awtStyle |= java.awt.Font.ITALIC;
        }
        int px = switch (size) {
            case SIZE_SMALL -> 10;
            case SIZE_LARGE -> 16;
            default -> 12;
        };
        String family = (face == FACE_MONOSPACE) ? java.awt.Font.MONOSPACED : java.awt.Font.SANS_SERIF;
        return new Font(new java.awt.Font(family, awtStyle, px));
    }

    public int getHeight() {
        return metrics.getHeight();
    }

    public int stringWidth(String str) {
        return metrics.stringWidth(str == null ? "" : str);
    }

    public int substringWidth(String str, int offset, int len) {
        return stringWidth(str.substring(offset, offset + len));
    }

    public int charWidth(char ch) {
        return metrics.charWidth(ch);
    }

    public int getBaselinePosition() {
        return metrics.getAscent();
    }
}

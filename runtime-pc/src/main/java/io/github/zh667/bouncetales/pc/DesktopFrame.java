package io.github.zh667.bouncetales.pc;

import io.github.zh667.bouncetales.logic.AssetInventory;
import io.github.zh667.bouncetales.logic.AssetLocator;
import io.github.zh667.bouncetales.logic.GameAction;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * Hangar-style desktop host: one AWT frame, keymap, local asset status.
 */
final class DesktopFrame {
    private static final int WIDTH = 420;
    private static final int HEIGHT = 640;

    private final UiText strings;
    private final AssetInventory inventory;
    private volatile Optional<GameAction> held = Optional.empty();
    private JFrame frame;

    DesktopFrame(UiText strings, AssetInventory inventory) {
        this.strings = strings;
        this.inventory = inventory;
    }

    void show() {
        if (!SwingUtilities.isEventDispatchThread()) {
            throw new IllegalStateException("DesktopFrame.show must run on the EDT");
        }
        frame = new JFrame(strings.title());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
        loadIcon().ifPresent(frame::setIconImage);
        KeyView view = new KeyView();
        view.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        view.setFocusable(true);
        view.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                Optional<GameAction> action = KeyMap.actionFor(event.getKeyCode());
                if (action.isEmpty()) {
                    return;
                }
                held = action;
                view.repaint();
            }

            @Override
            public void keyReleased(KeyEvent event) {
                Optional<GameAction> action = KeyMap.actionFor(event.getKeyCode());
                if (action.isPresent() && held.equals(action)) {
                    held = Optional.empty();
                    view.repaint();
                }
            }
        });
        frame.setLayout(new BorderLayout());
        frame.add(view, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        view.requestFocusInWindow();
    }

    void dispose() {
        if (frame != null) {
            frame.dispose();
        }
    }

    private Optional<BufferedImage> loadIcon() {
        return inventory.jar().flatMap(jar -> AssetLocator.readEntry(jar, "icon.png")).flatMap(bytes -> {
            try {
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
                return Optional.ofNullable(image);
            } catch (Exception ex) {
                return Optional.empty();
            }
        });
    }

    private final class KeyView extends JPanel {
        private KeyView() {
            setBackground(new Color(18, 22, 28));
            setForeground(new Color(236, 239, 244));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setColor(getForeground());
            int y = 32;
            g.setFont(new Font("SansSerif", Font.BOLD, 16));
            y = draw(g, strings.title(), 20, y, 16);
            g.setFont(new Font("SansSerif", Font.PLAIN, 13));
            y = draw(g, strings.hostLine(), 20, y + 8, 16);
            y += 12;
            g.setFont(new Font("SansSerif", Font.BOLD, 14));
            y = draw(g, strings.assetsHeading(), 20, y, 18);
            g.setFont(new Font("SansSerif", Font.PLAIN, 13));
            y = draw(g, strings.assetsStatus(inventory), 20, y + 4, 16);
            String details = strings.assetsDetails(inventory);
            if (!details.isBlank()) {
                y = draw(g, details, 20, y, 16);
            }
            y = draw(g, strings.assetsHint(), 20, y, 16);
            y += 12;
            g.setFont(new Font("SansSerif", Font.BOLD, 14));
            y = draw(g, strings.helpHeading(), 20, y, 18);
            g.setFont(new Font("SansSerif", Font.PLAIN, 14));
            for (GameAction action : GameAction.values()) {
                y = draw(g, strings.binding(action), 20, y, 22);
            }
            y += 10;
            g.setFont(new Font("SansSerif", Font.BOLD, 14));
            draw(g, held.map(strings::pressed).orElse(strings.idle()), 20, y, 18);
            g.dispose();
        }

        private int draw(Graphics2D g, String text, int x, int y, int lineHeight) {
            int maxWidth = getWidth() - 40;
            for (String line : wrap(text, g, maxWidth)) {
                y += lineHeight;
                g.drawString(line, x, y);
            }
            return y;
        }

        private List<String> wrap(String text, Graphics2D g, int maxWidth) {
            if (text == null || text.isBlank()) {
                return List.of();
            }
            if (g.getFontMetrics().stringWidth(text) <= maxWidth) {
                return List.of(text);
            }
            List<String> lines = new ArrayList<>();
            String remaining = text;
            while (!remaining.isEmpty()) {
                int cut = remaining.length();
                while (cut > 1 && g.getFontMetrics().stringWidth(remaining.substring(0, cut)) > maxWidth) {
                    cut--;
                }
                lines.add(remaining.substring(0, cut));
                remaining = remaining.substring(cut);
            }
            return lines;
        }
    }
}

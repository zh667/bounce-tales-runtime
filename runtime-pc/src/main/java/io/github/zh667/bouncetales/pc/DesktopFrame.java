package io.github.zh667.bouncetales.pc;

import io.github.zh667.bouncetales.logic.AssetInventory;
import io.github.zh667.bouncetales.logic.AssetLocator;
import io.github.zh667.bouncetales.logic.BallSim;
import io.github.zh667.bouncetales.logic.GameAction;
import io.github.zh667.bouncetales.logic.SaveStore;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.WindowConstants;

/**
 * Hangar-style desktop host: AWT frame, keymap, JAR blit, MIDI, save, ball preview.
 */
final class DesktopFrame {
    private static final int WIDTH = 420;
    private static final int HEIGHT = 640;
    private static final int IMAGE_TOP = 28;
    private static final int IMAGE_HEIGHT = 168;
    private static final int FIELD_TOP = 204;
    private static final Color FIELD = new Color(32, 48, 40);
    private static final Color BALL = new Color(80, 196, 92);
    private static final Color BALL_SHADOW = new Color(18, 28, 22);

    private final UiText strings;
    private final AssetInventory inventory;
    private final SaveStore saves;
    private final Set<GameAction> held = EnumSet.noneOf(GameAction.class);
    private final BallSim ball = new BallSim();
    private Workbench workbench;
    private boolean saveOk;
    private JFrame frame;
    private Timer timer;

    DesktopFrame(UiText strings, AssetInventory inventory, SaveStore saves) {
        this.strings = strings;
        this.inventory = inventory;
        this.saves = saves;
    }

    void show() {
        if (!SwingUtilities.isEventDispatchThread()) {
            throw new IllegalStateException("DesktopFrame.show must run on the EDT");
        }
        workbench = openWorkbench();
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
                boolean first = held.add(action.get());
                if (first) {
                    onAction(action.get());
                }
                view.repaint();
            }

            @Override
            public void keyReleased(KeyEvent event) {
                KeyMap.actionFor(event.getKeyCode()).ifPresent(held::remove);
                view.repaint();
            }
        });
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                shutdown();
            }
        });
        frame.setLayout(new BorderLayout());
        frame.add(view, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        view.requestFocusInWindow();
        timer = new Timer(16, event -> {
            ball.tick(
                    16f / 1000f,
                    held.contains(GameAction.LEFT),
                    held.contains(GameAction.RIGHT),
                    held.contains(GameAction.UP));
            workbench.poll();
            view.repaint();
        });
        timer.start();
    }

    void dispose() {
        shutdown();
        if (frame != null) {
            frame.dispose();
        }
    }

    private void onAction(GameAction action) {
        switch (action) {
            case STAR -> workbench.nextImage();
            case FIRE -> workbench.toggleMidi();
            case DOWN -> workbench.nextMidi();
            case BACK -> {
                workbench.stopMidi();
                saveOk = workbench.save();
            }
            default -> {
                // movement is sampled from held keys in the timer
            }
        }
    }

    private Workbench openWorkbench() {
        return inventory.jar()
                .map(jar -> {
                    try {
                        return Workbench.open(jar, saves);
                    } catch (IOException ex) {
                        return Workbench.empty(saves);
                    }
                })
                .orElseGet(() -> Workbench.empty(saves));
    }

    private void shutdown() {
        if (timer != null) {
            timer.stop();
        }
        if (workbench != null) {
            workbench.close();
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
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            drawImagePreview(g);
            drawPlayfield(g);
            drawHud(g);
            g.dispose();
        }

        private void drawImagePreview(Graphics2D g) {
            int x = 20;
            int y = IMAGE_TOP;
            int w = getWidth() - 40;
            int h = IMAGE_HEIGHT;
            g.setColor(new Color(10, 12, 16));
            g.fillRoundRect(x, y, w, h, 12, 12);
            Optional<BufferedImage> preview = workbench.image();
            if (preview.isPresent()) {
                BufferedImage image = preview.get();
                float scale = Math.min(w / (float) Math.max(1, image.getWidth()), h / (float) Math.max(1, image.getHeight()));
                int dw = Math.max(1, Math.round(image.getWidth() * scale));
                int dh = Math.max(1, Math.round(image.getHeight() * scale));
                int dx = x + (w - dw) / 2;
                int dy = y + (h - dh) / 2;
                g.drawImage(image, dx, dy, dw, dh, null);
            } else {
                g.setColor(new Color(120, 128, 140));
                g.setFont(new Font("SansSerif", Font.PLAIN, 13));
                g.drawString(strings.imageEmpty(), x + 12, y + h / 2);
            }
        }

        private void drawPlayfield(Graphics2D g) {
            int x = 20;
            int y = FIELD_TOP;
            int w = Math.round(BallSim.WIDTH);
            int h = Math.round(BallSim.HEIGHT);
            g.setColor(FIELD);
            g.fillRoundRect(x, y, w, h, 12, 12);
            int bx = x + Math.round(ball.x());
            int by = y + Math.round(ball.y());
            int r = Math.round(ball.radius());
            g.setColor(BALL_SHADOW);
            g.fillOval(bx - r + 3, by - r + 5, r * 2, r * 2);
            g.setColor(BALL);
            g.fillOval(bx - r, by - r, r * 2, r * 2);
            g.setColor(new Color(220, 255, 220));
            g.fillOval(bx - r / 2, by - r / 2, r / 2, r / 2);
        }

        private void drawHud(Graphics2D g) {
            g.setColor(getForeground());
            int y = FIELD_TOP + Math.round(BallSim.HEIGHT) + 8;
            g.setFont(new Font("SansSerif", Font.BOLD, 14));
            y = draw(g, strings.assetsHeading(), 20, y, 18);
            g.setFont(new Font("SansSerif", Font.PLAIN, 12));
            y = draw(g, strings.assetsStatus(inventory), 20, y, 15);
            String details = strings.assetsDetails(inventory);
            if (!details.isBlank()) {
                y = draw(g, details, 20, y, 15);
            }
            y = draw(g, strings.imageLine(workbench), 20, y, 15);
            y = draw(g, strings.midiLine(workbench), 20, y, 15);
            y = draw(g, strings.langLine(workbench), 20, y, 15);
            y = draw(g, strings.packedLine(workbench), 20, y, 15);
            y = draw(g, strings.saveLine(saveOk, workbench.saves.directory()), 20, y, 15);
            y += 6;
            g.setFont(new Font("SansSerif", Font.BOLD, 13));
            y = draw(g, strings.helpHeading(), 20, y, 16);
            g.setFont(new Font("SansSerif", Font.PLAIN, 12));
            y = draw(g, strings.workbenchHint(), 20, y, 15);
            g.setFont(new Font("SansSerif", Font.BOLD, 13));
            draw(g, heldStatus(), 20, y + 6, 16);
        }

        private String heldStatus() {
            if (held.isEmpty()) {
                return strings.idle();
            }
            GameAction first = held.iterator().next();
            return strings.pressed(first);
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

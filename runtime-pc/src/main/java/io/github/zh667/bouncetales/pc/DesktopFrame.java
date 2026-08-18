package io.github.zh667.bouncetales.pc;

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
import java.util.Optional;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * Hangar-style desktop host: one AWT frame, keymap, no original game assets.
 */
final class DesktopFrame {
    private static final int WIDTH = 360;
    private static final int HEIGHT = 520;

    private final UiText strings;
    private volatile Optional<GameAction> held = Optional.empty();
    private JFrame frame;

    DesktopFrame(UiText strings) {
        this.strings = strings;
    }

    void show() {
        if (!SwingUtilities.isEventDispatchThread()) {
            throw new IllegalStateException("DesktopFrame.show must run on the EDT");
        }
        frame = new JFrame(strings.title());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
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
            int y = 36;
            g.setFont(new Font("SansSerif", Font.BOLD, 16));
            g.drawString(strings.title(), 20, y);
            y += 28;
            g.setFont(new Font("SansSerif", Font.PLAIN, 13));
            g.drawString(strings.hostLine(), 20, y);
            y += 36;
            g.setFont(new Font("SansSerif", Font.BOLD, 14));
            g.drawString(strings.helpHeading(), 20, y);
            y += 26;
            g.setFont(new Font("SansSerif", Font.PLAIN, 14));
            for (GameAction action : GameAction.values()) {
                g.drawString(strings.binding(action), 20, y);
                y += 24;
            }
            y += 16;
            g.setFont(new Font("SansSerif", Font.BOLD, 14));
            g.drawString(held.map(strings::pressed).orElse(strings.idle()), 20, y);
            g.dispose();
        }
    }
}

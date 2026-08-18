package io.github.zh667.bouncetales.pc;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.midlet.DisplayBridge;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

final class MidletWindow implements DisplayBridge {
    static final int LOGICAL_WIDTH = 240;
    static final int LOGICAL_HEIGHT = 320;
    static final int SCALE = 2;

    private final JFrame frame;
    private final View view;
    private volatile Canvas canvas;

    MidletWindow(String title) {
        frame = new JFrame(title);
        view = new View();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(view);
        frame.pack();
        frame.setLocationRelativeTo(null);
        view.setFocusable(true);
        view.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                int code = MidletKeyMap.keyCode(event.getKeyCode());
                if (code != 0 && canvas != null) {
                    canvas.dispatchKeyPressed(code);
                }
            }

            @Override
            public void keyReleased(KeyEvent event) {
                int code = MidletKeyMap.keyCode(event.getKeyCode());
                if (code != 0 && canvas != null) {
                    canvas.dispatchKeyReleased(code);
                }
            }
        });
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.dispose();
            }
        });
    }

    void show() {
        frame.setVisible(true);
        view.requestFocusInWindow();
    }

    void dispose() {
        frame.dispose();
    }

    @Override
    public void attach(Object next) {
        if (next instanceof Canvas c) {
            canvas = c;
            SwingUtilities.invokeLater(() -> {
                view.repaint();
                view.requestFocusInWindow();
            });
        }
    }

    @Override
    public void flush() {
        if (SwingUtilities.isEventDispatchThread()) {
            view.repaint();
        } else {
            view.repaint();
        }
    }

    @Override
    public int width() {
        return LOGICAL_WIDTH;
    }

    @Override
    public int height() {
        return LOGICAL_HEIGHT;
    }

    private final class View extends JPanel {
        private View() {
            setPreferredSize(new Dimension(LOGICAL_WIDTH * SCALE, LOGICAL_HEIGHT * SCALE));
            setBackground(Color.BLACK);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Canvas current = canvas;
            if (current == null) {
                return;
            }
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            if (current instanceof GameCanvas game) {
                BufferedImage buffer = game.buffer();
                g.drawImage(buffer, 0, 0, getWidth(), getHeight(), null);
            } else {
                BufferedImage scratch = new BufferedImage(LOGICAL_WIDTH, LOGICAL_HEIGHT, BufferedImage.TYPE_INT_RGB);
                current.dispatchPaint(new javax.microedition.lcdui.Graphics(scratch.createGraphics()));
                g.drawImage(scratch, 0, 0, getWidth(), getHeight(), null);
            }
            g.dispose();
        }
    }
}

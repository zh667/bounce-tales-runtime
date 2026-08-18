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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.imageio.ImageIO;
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
    private final AtomicBoolean dumped = new AtomicBoolean();

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
        HostLog.windowOpened(LOGICAL_WIDTH, LOGICAL_HEIGHT, SCALE);
        if (Boolean.parseBoolean(System.getProperty("bounce.debug.dump", "false"))) {
            javax.swing.Timer timer = new javax.swing.Timer(5000, event -> dumpFrame());
            timer.setRepeats(false);
            timer.start();
        }
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
        HostLog.flush();
        view.repaint();
    }

    @Override
    public int width() {
        return LOGICAL_WIDTH;
    }

    @Override
    public int height() {
        return LOGICAL_HEIGHT;
    }

    private void dumpFrame() {
        if (!dumped.compareAndSet(false, true)) {
            return;
        }
        Canvas current = canvas;
        if (!(current instanceof GameCanvas game)) {
            return;
        }
        try {
            String dir = System.getProperty("bounce.save.dir");
            Path root = (dir == null || dir.isBlank())
                    ? Path.of(System.getProperty("user.home"), ".bounce-tales-runtime")
                    : Path.of(dir);
            Files.createDirectories(root);
            Path out = root.resolve("frame-dump.png");
            BufferedImage shot =
                    new BufferedImage(LOGICAL_WIDTH, LOGICAL_HEIGHT, BufferedImage.TYPE_INT_RGB);
            java.awt.Graphics g = shot.createGraphics();
            game.blitPresent(g, 0, 0, LOGICAL_WIDTH, LOGICAL_HEIGHT);
            g.dispose();
            ImageIO.write(shot, "png", out.toFile());
            System.out.println("debug dump: " + out.toAbsolutePath());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private final class View extends JPanel {
        private View() {
            setPreferredSize(new Dimension(LOGICAL_WIDTH * SCALE, LOGICAL_HEIGHT * SCALE));
            setBackground(Color.BLACK);
            setOpaque(true);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            HostLog.paint();
            Canvas current = canvas;
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            if (current instanceof GameCanvas game) {
                game.blitPresent(g, 0, 0, getWidth(), getHeight());
            } else if (current != null) {
                BufferedImage scratch = new BufferedImage(LOGICAL_WIDTH, LOGICAL_HEIGHT, BufferedImage.TYPE_INT_RGB);
                current.dispatchPaint(new javax.microedition.lcdui.Graphics(scratch.createGraphics()));
                g.drawImage(scratch, 0, 0, getWidth(), getHeight(), null);
            } else {
                super.paintComponent(g);
            }
            g.dispose();
        }
    }
}

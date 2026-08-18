package io.github.zh667.bouncetales.pc;

import io.github.zh667.bouncetales.logic.AssetInventory;
import io.github.zh667.bouncetales.logic.AssetLocator;
import io.github.zh667.bouncetales.logic.ChapterId;
import io.github.zh667.bouncetales.logic.ChapterLoader;
import io.github.zh667.bouncetales.logic.GameLogic;
import io.github.zh667.bouncetales.logic.HostTarget;
import io.github.zh667.bouncetales.logic.JarCatalog;
import io.github.zh667.bouncetales.logic.PackedIndex;
import io.github.zh667.bouncetales.logic.SaveStore;
import java.awt.BorderLayout;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * Desktop host: load the user JAR and run its MIDlet. Overlay remake is opt-in.
 */
public final class DesktopRuntime {
    private DesktopRuntime() {
    }

    public static HostTarget target() {
        return HostTarget.DESKTOP;
    }

    public static String banner() {
        return "bounce-tales-runtime " + target() + " hosting " + GameLogic.describe();
    }

    public static AssetInventory start(boolean headless) {
        return start(headless, AssetPaths.discover(), false);
    }

    public static AssetInventory start(boolean headless, Path assetsDir) {
        return start(headless, assetsDir, false);
    }

    public static AssetInventory start(boolean headless, Path assetsDir, boolean debugOverlay) {
        System.setOut(new PrintStream(System.out, true));
        System.setErr(new PrintStream(System.err, true));
        Thread.setDefaultUncaughtExceptionHandler((thread, error) -> {
            System.err.println("uncaught in " + thread.getName());
            error.printStackTrace();
        });
        System.out.println(banner());
        AssetInventory inventory = AssetLocator.scan(assetsDir);
        System.out.println(inventory.toLogLine());
        SaveStore saves = new SaveStore(SaveStore.defaultDirectory());
        inventory.jar().ifPresent(jar -> {
            logCatalog(jar);
            MidletManifest.read(jar).ifPresent(manifest -> System.out.println(manifest.toLogLine()));
        });
        if (headless) {
            return inventory;
        }
        UiText ui = UiText.forDefaultLocale();
        if (debugOverlay) {
            SwingUtilities.invokeLater(() -> new DesktopFrame(ui, inventory, saves).show());
            return inventory;
        }
        if (inventory.ready()) {
            try {
                MidletHost.launch(inventory.jar().orElseThrow(), saves);
            } catch (Exception ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> showLaunchError(ui, ex));
            }
            return inventory;
        }
        SwingUtilities.invokeLater(() -> new DesktopFrame(ui, inventory, saves).show());
        return inventory;
    }

    public static void main(String[] args) {
        boolean headless = GraphicsEnvironment.isHeadless();
        boolean debugOverlay = false;
        Path assets = AssetPaths.discover();
        List<String> rest = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if ("--headless".equals(arg)) {
                headless = true;
            } else if ("--debug-overlay".equals(arg)) {
                debugOverlay = true;
            } else if ("--assets".equals(arg) && i + 1 < args.length) {
                assets = Path.of(args[++i]);
            } else if (arg.startsWith("--assets=")) {
                assets = Path.of(arg.substring("--assets=".length()));
            } else {
                rest.add(arg);
            }
        }
        if (!rest.isEmpty()) {
            System.err.println("unknown args: " + rest);
        }
        start(headless, assets, debugOverlay);
    }

    private static void showLaunchError(UiText ui, Exception ex) {
        JFrame frame = new JFrame(ui.title());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JTextArea area = new JTextArea(ui.midletFailed(ex.getMessage()));
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        frame.setLayout(new BorderLayout());
        frame.add(new JScrollPane(area), BorderLayout.CENTER);
        frame.setSize(480, 240);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void logCatalog(Path jar) {
        try {
            JarCatalog catalog = JarCatalog.open(jar);
            System.out.println(catalog.toLogLine());
            if (catalog.hasPackedIndex()) {
                AssetLocator.readEntry(jar, "a")
                        .map(PackedIndex::tryParse)
                        .ifPresent(index -> {
                            System.out.println(index.toLogLine());
                            ChapterLoader.load(jar, index, ChapterId.MISTY_MORNING)
                                    .ifPresent(level -> System.out.println(level.toLogLine()));
                        });
            }
        } catch (IOException ex) {
            System.out.println("catalog: UNREADABLE " + ex.getMessage());
        }
    }
}

package io.github.zh667.bouncetales.pc;

import io.github.zh667.bouncetales.logic.AssetInventory;
import io.github.zh667.bouncetales.logic.AssetLocator;
import io.github.zh667.bouncetales.logic.GameLogic;
import io.github.zh667.bouncetales.logic.HostTarget;
import java.awt.GraphicsEnvironment;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 * Hangar-style desktop host entry. Windowing and input live here; gameplay stays in game-logic.
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
        return start(headless, AssetPaths.discover());
    }

    public static AssetInventory start(boolean headless, Path assetsDir) {
        System.out.println(banner());
        AssetInventory inventory = AssetLocator.scan(assetsDir);
        System.out.println(inventory.toLogLine());
        if (headless) {
            return inventory;
        }
        UiText ui = UiText.forDefaultLocale();
        SwingUtilities.invokeLater(() -> new DesktopFrame(ui, inventory).show());
        return inventory;
    }

    public static void main(String[] args) {
        boolean headless = GraphicsEnvironment.isHeadless();
        Path assets = AssetPaths.discover();
        List<String> rest = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if ("--headless".equals(arg)) {
                headless = true;
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
        start(headless, assets);
    }
}

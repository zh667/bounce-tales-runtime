package io.github.zh667.bouncetales.pc;

import io.github.zh667.bouncetales.logic.GameLogic;
import io.github.zh667.bouncetales.logic.HostTarget;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
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

    public static void start(boolean headless) {
        System.out.println(banner());
        if (headless) {
            return;
        }
        UiText ui = UiText.forDefaultLocale();
        SwingUtilities.invokeLater(() -> new DesktopFrame(ui).show());
    }

    public static void main(String[] args) {
        boolean headless = GraphicsEnvironment.isHeadless()
                || Arrays.asList(args).contains("--headless");
        start(headless);
    }
}

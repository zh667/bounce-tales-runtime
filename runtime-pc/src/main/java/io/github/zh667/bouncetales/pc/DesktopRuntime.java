package io.github.zh667.bouncetales.pc;

import io.github.zh667.bouncetales.logic.GameLogic;
import io.github.zh667.bouncetales.logic.HostTarget;

/**
 * Desktop host stub. Later this process will own windowing, input, MIDI, and saves.
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

    public static void main(String[] args) {
        System.out.println(banner());
    }
}

package io.github.zh667.bouncetales.android;

import io.github.zh667.bouncetales.logic.GameLogic;
import io.github.zh667.bouncetales.logic.HostTarget;

/**
 * Android host stub. This module is not an Android Gradle plugin project yet.
 */
public final class AndroidRuntime {
    private AndroidRuntime() {
    }

    public static HostTarget target() {
        return HostTarget.ANDROID;
    }

    public static String banner() {
        return "bounce-tales-runtime " + target() + " hosting " + GameLogic.describe();
    }
}

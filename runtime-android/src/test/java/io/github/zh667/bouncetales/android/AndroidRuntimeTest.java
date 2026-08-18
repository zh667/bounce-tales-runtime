package io.github.zh667.bouncetales.android;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.zh667.bouncetales.logic.HostTarget;
import org.junit.jupiter.api.Test;

class AndroidRuntimeTest {
    @Test
    void targetsAndroid() {
        assertEquals(HostTarget.ANDROID, AndroidRuntime.target());
    }
}

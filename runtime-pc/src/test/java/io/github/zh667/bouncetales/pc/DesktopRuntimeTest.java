package io.github.zh667.bouncetales.pc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zh667.bouncetales.logic.HostTarget;
import org.junit.jupiter.api.Test;

class DesktopRuntimeTest {
    @Test
    void targetsDesktop() {
        assertEquals(HostTarget.DESKTOP, DesktopRuntime.target());
    }

    @Test
    void bannerNamesSharedLogic() {
        assertTrue(DesktopRuntime.banner().contains("game-logic/skeleton"));
    }
}

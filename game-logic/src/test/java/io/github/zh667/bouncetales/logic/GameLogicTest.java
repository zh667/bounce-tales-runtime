package io.github.zh667.bouncetales.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class GameLogicTest {
    @Test
    void moduleNameIsStable() {
        assertEquals("game-logic", GameLogic.MODULE_NAME);
    }

    @Test
    void describeMarksPreviewPhase() {
        assertEquals("game-logic/preview", GameLogic.describe());
    }

    @Test
    void hostTargetsExistForPlannedRuntimes() {
        assertNotNull(HostTarget.DESKTOP);
        assertNotNull(HostTarget.ANDROID);
        assertEquals(2, HostTarget.values().length);
    }
}

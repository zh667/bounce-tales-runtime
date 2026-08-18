package io.github.zh667.bouncetales.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class GameActionTest {
    @Test
    void coversNokiaPadActions() {
        assertEquals(7, GameAction.values().length);
        assertEquals("UP", GameAction.UP.name());
        assertEquals("STAR", GameAction.STAR.name());
        assertEquals("BACK", GameAction.BACK.name());
    }
}

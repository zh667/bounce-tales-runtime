package io.github.zh667.bouncetales.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import org.junit.jupiter.api.Test;

class RlefLevelTest {
    @Test
    void parsesSyntheticPlatformAndPlayer() throws IOException {
        RlefLevel level = RlefLevel.parse(RlefFixtures.platformChapter());
        assertEquals(3, level.objectCount());
        assertEquals(1, level.terrain().size());
        assertEquals(4, level.terrain().get(0).vertices().size());
        assertTrue(level.playable());
        RlefLevel.Vec2 spawn = level.playerSpawn().orElseThrow();
        assertEquals(0, spawn.x());
        assertEquals(70, spawn.y());
        assertEquals(-120, level.minX());
        assertEquals(120, level.maxX());
        assertTrue(level.toLogLine().contains("geoms=1"));
    }

    @Test
    void tryParseRejectsGarbage() {
        assertTrue(RlefLevel.tryParse(new byte[] {1, 2, 3, 4}).terrain().isEmpty());
    }
}

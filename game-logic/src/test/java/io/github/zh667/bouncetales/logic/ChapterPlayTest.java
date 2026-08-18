package io.github.zh667.bouncetales.logic;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import org.junit.jupiter.api.Test;

class ChapterPlayTest {
    @Test
    void ballLandsOnSyntheticPlatform() throws IOException {
        RlefLevel level = RlefLevel.parse(RlefFixtures.platformChapter());
        ChapterPlay play = new ChapterPlay(level);
        assertEqualsish(70f, play.y(), 0.1f);
        for (int i = 0; i < 180; i++) {
            play.tick(1f / 60f, false, false, false);
        }
        assertTrue(play.onGround(), "expected to land, y=" + play.y());
        assertTrue(play.y() > 24f && play.y() < 24f + ChapterPlay.RADIUS + 8f, "y=" + play.y());
        float groundedY = play.y();
        play.tick(1f / 60f, false, true, true);
        for (int i = 0; i < 8; i++) {
            play.tick(1f / 60f, false, true, true);
        }
        assertTrue(play.y() > groundedY + 8f, "jump y=" + play.y() + " from " + groundedY);
    }

    private static void assertEqualsish(float expected, float actual, float eps) {
        if (Math.abs(expected - actual) > eps) {
            throw new AssertionError("expected " + expected + " but was " + actual);
        }
    }
}

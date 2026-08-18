package io.github.zh667.bouncetales.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BallSimTest {
    @Test
    void startsOnTheFloor() {
        BallSim ball = new BallSim();
        assertTrue(ball.onGround());
        assertEquals(BallSim.WIDTH / 2f, ball.x(), 0.01f);
        assertEquals(BallSim.HEIGHT - BallSim.RADIUS, ball.y(), 0.01f);
    }

    @Test
    void jumpLeavesTheGroundThenLands() {
        BallSim ball = new BallSim();
        ball.tick(1f / 60f, false, false, true);
        assertFalse(ball.onGround());
        assertTrue(ball.vy() < 0f);
        for (int i = 0; i < 240; i++) {
            ball.tick(1f / 60f, false, false, true);
        }
        assertTrue(ball.onGround());
        assertEquals(BallSim.HEIGHT - BallSim.RADIUS, ball.y(), 0.5f);
    }

    @Test
    void leftAndRightStayInsideThePlayfield() {
        BallSim ball = new BallSim();
        for (int i = 0; i < 300; i++) {
            ball.tick(1f / 60f, true, false, false);
        }
        assertEquals(BallSim.RADIUS, ball.x(), 0.5f);
        for (int i = 0; i < 300; i++) {
            ball.tick(1f / 60f, false, true, false);
        }
        assertEquals(BallSim.WIDTH - BallSim.RADIUS, ball.x(), 0.5f);
    }
}

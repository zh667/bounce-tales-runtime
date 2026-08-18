package io.github.zh667.bouncetales.logic;

/**
 * Tiny original ball preview. Not a chapter loader and not a port of BounceObject.
 */
public final class BallSim {
    public static final float WIDTH = 380f;
    public static final float HEIGHT = 240f;
    public static final float RADIUS = 14f;

    private static final float GRAVITY = 1400f;
    private static final float JUMP_SPEED = -520f;
    private static final float MOVE_SPEED = 220f;
    private static final float AIR_CONTROL = 160f;
    private static final float GROUND_DAMP = 0.72f;
    private static final float AIR_DAMP = 0.98f;

    private float x;
    private float y;
    private float vx;
    private float vy;
    private boolean onGround;
    private boolean jumpLocked;

    public BallSim() {
        reset();
    }

    public void reset() {
        x = WIDTH / 2f;
        y = HEIGHT - RADIUS;
        vx = 0f;
        vy = 0f;
        onGround = true;
        jumpLocked = false;
    }

    public void tick(float dtSeconds, boolean left, boolean right, boolean jump) {
        float dt = dtSeconds;
        if (dt <= 0f || dt > 0.1f) {
            dt = 1f / 60f;
        }
        if (left == right) {
            vx *= onGround ? GROUND_DAMP : AIR_DAMP;
        } else {
            float speed = onGround ? MOVE_SPEED : AIR_CONTROL;
            vx = left ? -speed : speed;
        }
        if (jump && onGround && !jumpLocked) {
            vy = JUMP_SPEED;
            onGround = false;
            jumpLocked = true;
        }
        if (!jump) {
            jumpLocked = false;
        }
        vy += GRAVITY * dt;
        x += vx * dt;
        y += vy * dt;
        if (x < RADIUS) {
            x = RADIUS;
            vx = 0f;
        } else if (x > WIDTH - RADIUS) {
            x = WIDTH - RADIUS;
            vx = 0f;
        }
        if (y >= HEIGHT - RADIUS) {
            y = HEIGHT - RADIUS;
            vy = 0f;
            onGround = true;
        } else {
            onGround = false;
        }
        if (y < RADIUS) {
            y = RADIUS;
            vy = Math.abs(vy);
        }
    }

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }

    public float vx() {
        return vx;
    }

    public float vy() {
        return vy;
    }

    public float radius() {
        return RADIUS;
    }

    public boolean onGround() {
        return onGround;
    }
}

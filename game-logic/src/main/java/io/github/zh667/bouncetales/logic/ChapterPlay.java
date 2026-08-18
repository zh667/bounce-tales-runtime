package io.github.zh667.bouncetales.logic;

import java.util.List;
import java.util.Objects;

/**
 * Ball on a loaded RLEF chapter. Original gravity/jump, not a BounceObject port.
 */
public final class ChapterPlay {
    public static final float RADIUS = 22f;
    private static final float GRAVITY = -2600f;
    private static final float JUMP_SPEED = 920f;
    private static final float MOVE_SPEED = 280f;
    private static final float AIR_CONTROL = 200f;
    private static final float GROUND_DAMP = 0.78f;
    private static final float AIR_DAMP = 0.98f;

    private final RlefLevel level;
    private float x;
    private float y;
    private float vx;
    private float vy;
    private boolean onGround;
    private boolean jumpLocked;

    public ChapterPlay(RlefLevel level) {
        this.level = Objects.requireNonNull(level, "level");
        reset();
    }

    public RlefLevel level() {
        return level;
    }

    public void reset() {
        RlefLevel.Vec2 spawn = level.playerSpawn().orElse(new RlefLevel.Vec2(0, 0));
        x = spawn.x();
        y = spawn.y();
        vx = 0f;
        vy = 0f;
        onGround = false;
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
        onGround = false;
        collide();
        if (y < level.minY() - 400f) {
            reset();
        }
    }

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }

    public float radius() {
        return RADIUS;
    }

    public boolean onGround() {
        return onGround;
    }

    private void collide() {
        for (int pass = 0; pass < 3; pass++) {
            for (RlefLevel.Terrain poly : level.terrain()) {
                List<RlefLevel.Vec2> verts = poly.vertices();
                int n = verts.size();
                if (n < 2) {
                    continue;
                }
                float cx = 0f;
                float cy = 0f;
                for (RlefLevel.Vec2 v : verts) {
                    cx += v.x();
                    cy += v.y();
                }
                cx /= n;
                cy /= n;
                for (int i = 0; i < n; i++) {
                    RlefLevel.Vec2 a = verts.get(i);
                    RlefLevel.Vec2 b = verts.get((i + 1) % n);
                    resolveEdge(a.x(), a.y(), b.x(), b.y(), cx, cy);
                }
            }
        }
    }

    private void resolveEdge(float ax, float ay, float bx, float by, float cx, float cy) {
        float ex = bx - ax;
        float ey = by - ay;
        float len2 = ex * ex + ey * ey;
        if (len2 < 1f) {
            return;
        }
        float t = ((x - ax) * ex + (y - ay) * ey) / len2;
        t = Math.max(0f, Math.min(1f, t));
        float px = ax + ex * t;
        float py = ay + ey * t;
        float dx = x - px;
        float dy = y - py;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);
        float nx = -ey;
        float ny = ex;
        float nlen = (float) Math.sqrt(nx * nx + ny * ny);
        if (nlen < 0.001f) {
            return;
        }
        nx /= nlen;
        ny /= nlen;
        if (nx * (cx - ax) + ny * (cy - ay) > 0f) {
            nx = -nx;
            ny = -ny;
        }
        float sep = dx * nx + dy * ny;
        if (sep < 0f || sep >= RADIUS) {
            return;
        }
        float push = RADIUS - sep;
        x += nx * push;
        y += ny * push;
        float vn = vx * nx + vy * ny;
        if (vn < 0f) {
            vx -= nx * vn;
            vy -= ny * vn;
        }
        if (ny > 0.45f) {
            onGround = true;
        }
    }
}

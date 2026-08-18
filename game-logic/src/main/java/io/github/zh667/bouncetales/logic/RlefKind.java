package io.github.zh667.bouncetales.logic;

/**
 * RLEF object kinds. Numeric values match the on-disk table; this enum is an independent mapping.
 */
public enum RlefKind {
    ROOT,
    GEOMETRY,
    EVENT,
    PLAYER,
    SPRITE,
    WATER,
    CANNON,
    TRAMPOLINE,
    EGG,
    FRIEND,
    ENEMY,
    UNKNOWN;

    public static final int END = 127;

    public static RlefKind fromCode(int code) {
        return switch (code) {
            case 0 -> ROOT;
            case 4 -> GEOMETRY;
            case 6 -> EVENT;
            case 8 -> PLAYER;
            case 9 -> SPRITE;
            case 10 -> WATER;
            case 11 -> CANNON;
            case 12 -> TRAMPOLINE;
            case 13 -> EGG;
            case 14 -> FRIEND;
            case 15 -> ENEMY;
            default -> UNKNOWN;
        };
    }
}

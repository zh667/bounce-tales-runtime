package io.github.zh667.bouncetales.logic;

/**
 * Resource batch kinds as used in the packed index. Values match the original table;
 * this enum is an independent mapping, not a copy of upstream sources.
 */
public enum PackedKind {
    UNKNOWN,
    IMAGE,
    MIDI,
    STRINGS,
    LAYOUT,
    LEVEL;

    public static PackedKind fromCode(int code) {
        return switch (code) {
            case 2 -> IMAGE;
            case 3 -> MIDI;
            case 4 -> STRINGS;
            case 5 -> LAYOUT;
            case 8 -> LEVEL;
            default -> UNKNOWN;
        };
    }
}

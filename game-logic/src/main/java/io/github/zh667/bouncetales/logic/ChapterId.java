package io.github.zh667.bouncetales.logic;

/**
 * Campaign chapters we know how to find in a user JAR. Misty Morning is entry {@code bf}.
 */
public enum ChapterId {
    MISTY_MORNING("bf", "misty-morning");

    private final String jarEntry;
    private final String slug;

    ChapterId(String jarEntry, String slug) {
        this.jarEntry = jarEntry;
        this.slug = slug;
    }

    public String jarEntry() {
        return jarEntry;
    }

    public String slug() {
        return slug;
    }
}

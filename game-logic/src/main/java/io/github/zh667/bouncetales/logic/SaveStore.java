package io.github.zh667.bouncetales.logic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * File-backed save slots. Independent of MIDP RecordStore; hosts inject the directory.
 */
public final class SaveStore {
    private static final String SLOT_PATTERN = "[A-Za-z0-9_-]{1,32}";

    private final Path directory;

    public SaveStore(Path directory) {
        this.directory = Objects.requireNonNull(directory, "directory").toAbsolutePath().normalize();
    }

    public static Path defaultDirectory() {
        String property = System.getProperty("bounce.save.dir");
        if (property != null && !property.isBlank()) {
            return Path.of(property).toAbsolutePath().normalize();
        }
        String env = System.getenv("BOUNCE_SAVE_DIR");
        if (env != null && !env.isBlank()) {
            return Path.of(env).toAbsolutePath().normalize();
        }
        return Path.of(System.getProperty("user.home"), ".bounce-tales-runtime", "saves")
                .toAbsolutePath()
                .normalize();
    }

    public Path directory() {
        return directory;
    }

    public void write(String slot, byte[] data) throws IOException {
        Objects.requireNonNull(data, "data");
        Files.createDirectories(directory);
        Files.write(slotPath(slot), data);
    }

    public Optional<byte[]> read(String slot) throws IOException {
        Path path = slotPath(slot);
        if (!Files.isRegularFile(path)) {
            return Optional.empty();
        }
        return Optional.of(Files.readAllBytes(path));
    }

    public boolean exists(String slot) {
        try {
            return Files.isRegularFile(slotPath(slot));
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    private Path slotPath(String slot) {
        if (slot == null || !slot.matches(SLOT_PATTERN)) {
            throw new IllegalArgumentException("invalid save slot: " + slot);
        }
        String file = slot.toLowerCase(Locale.ROOT) + ".bin";
        Path resolved = directory.resolve(file).toAbsolutePath().normalize();
        if (!resolved.startsWith(directory)) {
            throw new IllegalArgumentException("save path escaped store: " + slot);
        }
        return resolved;
    }
}

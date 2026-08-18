package io.github.zh667.bouncetales.pc;

import java.nio.file.Files;
import java.nio.file.Path;

final class AssetPaths {
    private AssetPaths() {
    }

    static Path discover() {
        String property = System.getProperty("bounce.assets.dir");
        if (property != null && !property.isBlank()) {
            return Path.of(property).toAbsolutePath().normalize();
        }
        String env = System.getenv("BOUNCE_ASSETS_DIR");
        if (env != null && !env.isBlank()) {
            return Path.of(env).toAbsolutePath().normalize();
        }
        Path current = Path.of(System.getProperty("user.dir", ".")).toAbsolutePath().normalize();
        for (int i = 0; i < 8 && current != null; i++) {
            Path candidate = current.resolve("assets");
            if (Files.isDirectory(candidate) && Files.isRegularFile(candidate.resolve("README.md"))) {
                return candidate;
            }
            current = current.getParent();
        }
        return Path.of(System.getProperty("user.dir", ".")).toAbsolutePath().normalize().resolve("assets");
    }
}

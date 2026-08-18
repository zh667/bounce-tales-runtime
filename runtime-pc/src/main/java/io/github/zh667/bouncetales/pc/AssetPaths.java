package io.github.zh667.bouncetales.pc;

import io.github.zh667.bouncetales.logic.AssetLocator;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

final class AssetPaths {
    private AssetPaths() {}

    static Path discover() {
        String property = System.getProperty("bounce.assets.dir");
        if (property != null && !property.isBlank()) {
            return Path.of(property).toAbsolutePath().normalize();
        }
        String env = System.getenv("BOUNCE_ASSETS_DIR");
        if (env != null && !env.isBlank()) {
            return Path.of(env).toAbsolutePath().normalize();
        }
        return discoverFrom(installDirectory().orElse(null), cwd());
    }

    static Path discoverFrom(Path installDir, Path workingDir) {
        List<Path> starts = new ArrayList<>();
        if (installDir != null) {
            starts.add(installDir.toAbsolutePath().normalize());
        }
        if (workingDir != null) {
            Path cwd = workingDir.toAbsolutePath().normalize();
            if (!starts.contains(cwd)) {
                starts.add(cwd);
            }
        }
        for (Path start : starts) {
            Path found = search(start);
            if (found != null) {
                return found;
            }
        }
        Path fallback = installDir != null ? installDir : workingDir;
        if (fallback == null) {
            fallback = cwd();
        }
        return fallback.toAbsolutePath().normalize().resolve("assets");
    }

    static Optional<Path> installDirectory() {
        try {
            CodeSource source = DesktopRuntime.class.getProtectionDomain().getCodeSource();
            if (source == null || source.getLocation() == null) {
                return Optional.empty();
            }
            Path path = Path.of(source.getLocation().toURI()).toAbsolutePath().normalize();
            if (Files.isRegularFile(path)) {
                return Optional.of(path.getParent());
            }
            if (Files.isDirectory(path)) {
                return Optional.of(path);
            }
        } catch (Exception ignored) {
            // fall back to the working directory
        }
        return Optional.empty();
    }

    private static Path search(Path start) {
        Path current = start;
        for (int i = 0; i < 8 && current != null; i++) {
            Path assets = current.resolve("assets");
            if (looksLikeAssetsFolder(assets)) {
                return assets;
            }
            if (hasGameJar(current)) {
                return current;
            }
            current = current.getParent();
        }
        return null;
    }

    private static boolean looksLikeAssetsFolder(Path assets) {
        return Files.isDirectory(assets)
                && (Files.isRegularFile(assets.resolve("README.md")) || hasGameJar(assets));
    }

    private static boolean hasGameJar(Path directory) {
        if (!Files.isDirectory(directory)) {
            return false;
        }
        try (var stream = Files.newDirectoryStream(directory, "*.jar")) {
            for (Path jar : stream) {
                String name = jar.getFileName().toString().toLowerCase(Locale.ROOT);
                if (!name.equals("bounce-tales-runtime.jar") && !AssetLocator.isHostRuntimeJar(jar)) {
                    return true;
                }
            }
        } catch (Exception ignored) {
            return false;
        }
        return false;
    }

    private static Path cwd() {
        return Path.of(System.getProperty("user.dir", ".")).toAbsolutePath().normalize();
    }
}

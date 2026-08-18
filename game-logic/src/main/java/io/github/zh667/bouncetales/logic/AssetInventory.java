package io.github.zh667.bouncetales.logic;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Result of scanning a local assets directory. Never holds original image/audio bytes.
 */
public final class AssetInventory {
    public enum Status {
        MISSING_DIR,
        EMPTY,
        JAR_FOUND,
        AMBIGUOUS,
        UNREADABLE
    }

    private final Status status;
    private final Path directory;
    private final Path jar;
    private final String fileName;
    private final String midletName;
    private final String midletVersion;
    private final String vendor;
    private final int entryCount;
    private final boolean hasIcon;
    private final boolean hasChineseLang;
    private final String error;
    private final List<String> extraJars;

    private AssetInventory(
            Status status,
            Path directory,
            Path jar,
            String fileName,
            String midletName,
            String midletVersion,
            String vendor,
            int entryCount,
            boolean hasIcon,
            boolean hasChineseLang,
            String error,
            List<String> extraJars) {
        this.status = Objects.requireNonNull(status);
        this.directory = directory;
        this.jar = jar;
        this.fileName = fileName;
        this.midletName = midletName;
        this.midletVersion = midletVersion;
        this.vendor = vendor;
        this.entryCount = entryCount;
        this.hasIcon = hasIcon;
        this.hasChineseLang = hasChineseLang;
        this.error = error;
        this.extraJars = List.copyOf(extraJars);
    }

    public static AssetInventory missingDir(Path directory) {
        return new AssetInventory(
                Status.MISSING_DIR, directory, null, null, null, null, null, 0, false, false, null, List.of());
    }

    public static AssetInventory empty(Path directory) {
        return new AssetInventory(
                Status.EMPTY, directory, null, null, null, null, null, 0, false, false, null, List.of());
    }

    public static AssetInventory found(
            Path directory,
            Path jar,
            String midletName,
            String midletVersion,
            String vendor,
            int entryCount,
            boolean hasIcon,
            boolean hasChineseLang) {
        return new AssetInventory(
                Status.JAR_FOUND,
                directory,
                jar,
                jar.getFileName().toString(),
                midletName,
                midletVersion,
                vendor,
                entryCount,
                hasIcon,
                hasChineseLang,
                null,
                List.of());
    }

    public static AssetInventory ambiguous(Path directory, List<String> jarNames) {
        return new AssetInventory(
                Status.AMBIGUOUS, directory, null, null, null, null, null, 0, false, false, null, jarNames);
    }

    public static AssetInventory unreadable(Path directory, Path jar, String error) {
        return new AssetInventory(
                Status.UNREADABLE,
                directory,
                jar,
                jar.getFileName().toString(),
                null,
                null,
                null,
                0,
                false,
                false,
                error,
                List.of());
    }

    public Status status() {
        return status;
    }

    public Path directory() {
        return directory;
    }

    public Optional<Path> jar() {
        return Optional.ofNullable(jar);
    }

    public Optional<String> fileName() {
        return Optional.ofNullable(fileName);
    }

    public Optional<String> midletName() {
        return Optional.ofNullable(midletName);
    }

    public Optional<String> midletVersion() {
        return Optional.ofNullable(midletVersion);
    }

    public Optional<String> vendor() {
        return Optional.ofNullable(vendor);
    }

    public int entryCount() {
        return entryCount;
    }

    public boolean hasIcon() {
        return hasIcon;
    }

    public boolean hasChineseLang() {
        return hasChineseLang;
    }

    public Optional<String> error() {
        return Optional.ofNullable(error);
    }

    public List<String> extraJars() {
        return extraJars;
    }

    public boolean ready() {
        return status == Status.JAR_FOUND;
    }

    public String toLogLine() {
        return switch (status) {
            case MISSING_DIR -> "assets: MISSING_DIR dir=" + directory;
            case EMPTY -> "assets: EMPTY dir=" + directory;
            case JAR_FOUND -> "assets: JAR_FOUND file="
                    + fileName
                    + " midlet="
                    + midletName
                    + " version="
                    + midletVersion
                    + " entries="
                    + entryCount;
            case AMBIGUOUS -> "assets: AMBIGUOUS dir=" + directory + " jars=" + extraJars;
            case UNREADABLE -> "assets: UNREADABLE file=" + fileName + " error=" + error;
        };
    }
}

package io.github.zh667.bouncetales.logic;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Scans a user-supplied assets directory. CI tests must pass a temp dir, never original game files.
 */
public final class AssetLocator {
    private static final String BOUNCE_TALES = "bounce tales";

    private AssetLocator() {
    }

    public static AssetInventory scan(Path assetsDir) {
        if (assetsDir == null || !Files.isDirectory(assetsDir)) {
            return AssetInventory.missingDir(assetsDir);
        }
        List<Path> jars;
        try {
            jars = listJars(assetsDir);
        } catch (IOException ex) {
            return AssetInventory.unreadable(assetsDir, assetsDir, ex.getMessage());
        }
        if (jars.isEmpty()) {
            return AssetInventory.empty(assetsDir);
        }
        Path chosen = chooseJar(jars);
        if (chosen == null) {
            return AssetInventory.ambiguous(assetsDir, names(jars));
        }
        try {
            return readJar(assetsDir, chosen);
        } catch (IOException ex) {
            return AssetInventory.unreadable(assetsDir, chosen, ex.getMessage());
        }
    }

    public static Optional<byte[]> readEntry(Path jar, String entryName) {
        if (jar == null || entryName == null) {
            return Optional.empty();
        }
        try (JarFile jarFile = new JarFile(jar.toFile())) {
            JarEntry entry = jarFile.getJarEntry(stripLeadingSlash(entryName));
            if (entry == null) {
                return Optional.empty();
            }
            try (InputStream in = jarFile.getInputStream(entry)) {
                return Optional.of(in.readAllBytes());
            }
        } catch (IOException ex) {
            return Optional.empty();
        }
    }

    public static Optional<byte[]> readSlice(Path jar, String entryName, int skipOffset, int readLength) {
        Optional<byte[]> all = readEntry(jar, entryName);
        if (all.isEmpty()) {
            return Optional.empty();
        }
        byte[] bytes = all.get();
        int skip = Math.max(0, skipOffset);
        if (skip >= bytes.length) {
            return Optional.empty();
        }
        int length = readLength <= 0 ? bytes.length - skip : Math.min(readLength, bytes.length - skip);
        if (skip == 0 && length == bytes.length) {
            return all;
        }
        return Optional.of(Arrays.copyOfRange(bytes, skip, skip + length));
    }

    private static AssetInventory readJar(Path assetsDir, Path jar) throws IOException {
        try (JarFile jarFile = new JarFile(jar.toFile())) {
            Manifest manifest = jarFile.getManifest();
            Attributes attrs = manifest != null ? manifest.getMainAttributes() : new Attributes();
            String name = attr(attrs, "MIDlet-Name");
            String version = attr(attrs, "MIDlet-Version");
            String vendor = attr(attrs, "MIDlet-Vendor");
            int entries = 0;
            boolean icon = false;
            boolean chinese = false;
            var it = jarFile.entries();
            while (it.hasMoreElements()) {
                JarEntry entry = it.nextElement();
                if (entry.isDirectory()) {
                    continue;
                }
                entries++;
                String entryName = stripLeadingSlash(entry.getName()).toLowerCase(Locale.ROOT);
                if (entryName.equals("icon.png")) {
                    icon = true;
                }
                if (entryName.equals("lang.zh-cn") || entryName.equals("lang.zh_cn")) {
                    chinese = true;
                }
            }
            return AssetInventory.found(assetsDir, jar, name, version, vendor, entries, icon, chinese);
        }
    }

    private static Path chooseJar(List<Path> jars) {
        if (jars.size() == 1) {
            return jars.get(0);
        }
        List<Path> bounce = new ArrayList<>();
        for (Path jar : jars) {
            try (JarFile jarFile = new JarFile(jar.toFile())) {
                Manifest manifest = jarFile.getManifest();
                String name = manifest == null ? "" : attr(manifest.getMainAttributes(), "MIDlet-Name");
                if (name.toLowerCase(Locale.ROOT).contains(BOUNCE_TALES)) {
                    bounce.add(jar);
                }
            } catch (IOException ignored) {
                // counted later as unreadable only if this jar is chosen
            }
        }
        if (bounce.size() == 1) {
            return bounce.get(0);
        }
        return null;
    }

    private static List<Path> listJars(Path assetsDir) throws IOException {
        List<Path> jars = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(assetsDir)) {
            for (Path path : stream) {
                if (!Files.isRegularFile(path)) {
                    continue;
                }
                String name = path.getFileName().toString().toLowerCase(Locale.ROOT);
                if (name.endsWith(".jar")) {
                    jars.add(path);
                }
            }
        }
        jars.sort(Path::compareTo);
        return jars;
    }

    private static List<String> names(List<Path> jars) {
        List<String> names = new ArrayList<>();
        for (Path jar : jars) {
            names.add(jar.getFileName().toString());
        }
        return names;
    }

    private static String attr(Attributes attrs, String key) {
        String value = attrs.getValue(key);
        return value == null ? "" : value.trim();
    }

    private static String stripLeadingSlash(String name) {
        if (name.startsWith("/") || name.startsWith("\\")) {
            return name.substring(1);
        }
        return name;
    }
}

package io.github.zh667.bouncetales.logic;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Names of media entries inside a bundled or user-selected JAR. Does not keep image or MIDI bytes.
 */
public final class JarCatalog {
    private final Path jar;
    private final List<String> images;
    private final List<String> midis;
    private final List<String> langs;
    private final boolean hasPackedIndex;
    private final boolean hasCampaign01;

    private JarCatalog(
            Path jar,
            List<String> images,
            List<String> midis,
            List<String> langs,
            boolean hasPackedIndex,
            boolean hasCampaign01) {
        this.jar = jar;
        this.images = List.copyOf(images);
        this.midis = List.copyOf(midis);
        this.langs = List.copyOf(langs);
        this.hasPackedIndex = hasPackedIndex;
        this.hasCampaign01 = hasCampaign01;
    }

    public static JarCatalog empty() {
        return new JarCatalog(null, List.of(), List.of(), List.of(), false, false);
    }

    public static JarCatalog open(Path jar) throws IOException {
        Objects.requireNonNull(jar, "jar");
        List<String> images = new ArrayList<>();
        List<String> midis = new ArrayList<>();
        List<String> langs = new ArrayList<>();
        boolean packed = false;
        boolean campaign = false;
        try (JarFile jarFile = new JarFile(jar.toFile())) {
            var entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.isDirectory()) {
                    continue;
                }
                String name = stripLeadingSlash(entry.getName());
                String lower = name.toLowerCase(Locale.ROOT);
                if (lower.endsWith(".png")) {
                    images.add(name);
                } else if (lower.endsWith(".mid") || lower.endsWith(".midi")) {
                    midis.add(name);
                } else if (lower.startsWith("lang.")) {
                    langs.add(name);
                } else if (lower.equals("a")) {
                    packed = true;
                } else if (lower.equals(ChapterId.MISTY_MORNING.jarEntry())) {
                    campaign = true;
                }
            }
        }
        images.sort(Comparator.naturalOrder());
        midis.sort(Comparator.naturalOrder());
        langs.sort(Comparator.naturalOrder());
        return new JarCatalog(jar, images, midis, langs, packed, campaign);
    }

    public Optional<Path> jar() {
        return Optional.ofNullable(jar);
    }

    public List<String> images() {
        return images;
    }

    public List<String> midis() {
        return midis;
    }

    public List<String> langs() {
        return langs;
    }

    public boolean hasPackedIndex() {
        return hasPackedIndex;
    }

    public boolean hasCampaign01() {
        return hasCampaign01;
    }

    public Optional<String> preferredLang() {
        for (String name : langs) {
            if (name.equalsIgnoreCase("lang.zh-CN") || name.equalsIgnoreCase("lang.zh_cn")) {
                return Optional.of(name);
            }
        }
        for (String name : langs) {
            if (name.equalsIgnoreCase("lang.xx")) {
                return Optional.of(name);
            }
        }
        return langs.isEmpty() ? Optional.empty() : Optional.of(langs.get(0));
    }

    public boolean hasMedia() {
        return !images.isEmpty() || !midis.isEmpty() || !langs.isEmpty() || hasPackedIndex || hasCampaign01;
    }

    public String toLogLine() {
        return "catalog: png="
                + images.size()
                + " midi="
                + midis.size()
                + " lang="
                + langs.size()
                + " packedIndex="
                + hasPackedIndex
                + " campaign01="
                + hasCampaign01;
    }

    private static String stripLeadingSlash(String name) {
        if (name.startsWith("/") || name.startsWith("\\")) {
            return name.substring(1);
        }
        return name;
    }
}

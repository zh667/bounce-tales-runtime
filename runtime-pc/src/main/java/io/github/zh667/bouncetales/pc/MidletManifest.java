package io.github.zh667.bouncetales.pc;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/** Reads MIDlet-1 and related attributes from a user JAR. */
public final class MidletManifest {
    private final String midletClass;
    private final String midletName;
    private final String version;
    private final String vendor;
    private final String nokiaPlatform;
    private final Map<String, String> properties;

    private MidletManifest(
            String midletClass,
            String midletName,
            String version,
            String vendor,
            String nokiaPlatform,
            Map<String, String> properties) {
        this.midletClass = midletClass;
        this.midletName = midletName;
        this.version = version;
        this.vendor = vendor;
        this.nokiaPlatform = nokiaPlatform;
        this.properties = Map.copyOf(properties);
    }

    public static Optional<MidletManifest> read(Path jar) {
        try (JarFile jarFile = new JarFile(jar.toFile())) {
            Manifest manifest = jarFile.getManifest();
            if (manifest == null) {
                return Optional.empty();
            }
            Attributes attrs = manifest.getMainAttributes();
            Map<String, String> all = new LinkedHashMap<>();
            for (Map.Entry<Object, Object> entry : attrs.entrySet()) {
                all.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
            }
            String spec = attrs.getValue("MIDlet-1");
            if (spec == null || spec.isBlank()) {
                return Optional.empty();
            }
            String[] parts = spec.split(",");
            String className = parts[parts.length - 1].trim();
            if (className.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(new MidletManifest(
                    className,
                    or(attrs.getValue("MIDlet-Name"), className),
                    or(attrs.getValue("MIDlet-Version"), ""),
                    or(attrs.getValue("MIDlet-Vendor"), ""),
                    or(attrs.getValue("Nokia-Platform"), "Nokia*"),
                    all));
        } catch (IOException ex) {
            return Optional.empty();
        }
    }

    public String midletClass() {
        return midletClass;
    }

    public String midletName() {
        return midletName;
    }

    public String version() {
        return version;
    }

    public String vendor() {
        return vendor;
    }

    public String nokiaPlatform() {
        return nokiaPlatform;
    }

    public Map<String, String> properties() {
        return properties;
    }

    public String toLogLine() {
        return "midlet: class=" + midletClass + " name=" + midletName + " version=" + version;
    }

    private static String or(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }
}

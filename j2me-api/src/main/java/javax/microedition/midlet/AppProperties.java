package javax.microedition.midlet;

import java.util.LinkedHashMap;
import java.util.Map;

/** Manifest / JAD properties visible to {@link MIDlet#getAppProperty(String)}. */
public final class AppProperties {
    private static final Map<String, String> VALUES = new LinkedHashMap<>();

    private AppProperties() {}

    public static synchronized void clear() {
        VALUES.clear();
    }

    public static synchronized void put(String key, String value) {
        if (key != null && value != null) {
            VALUES.put(key, value);
        }
    }

    public static synchronized String get(String key) {
        if (key == null) {
            return null;
        }
        return VALUES.get(key);
    }
}

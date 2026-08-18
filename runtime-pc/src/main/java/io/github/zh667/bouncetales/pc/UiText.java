package io.github.zh667.bouncetales.pc;

import io.github.zh667.bouncetales.logic.GameAction;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Properties;

final class UiText {
    private final Properties bundle;

    UiText(Locale locale) {
        this.bundle = load(locale);
    }

    static UiText forDefaultLocale() {
        Locale locale = Locale.getDefault();
        if ("en".equalsIgnoreCase(locale.getLanguage())) {
            return new UiText(Locale.ENGLISH);
        }
        return new UiText(Locale.SIMPLIFIED_CHINESE);
    }

    String title() {
        return text("window.title");
    }

    String hostLine() {
        return text("host.line");
    }

    String helpHeading() {
        return text("help.heading");
    }

    String idle() {
        return text("status.idle");
    }

    String pressed(GameAction action) {
        return String.format(text("status.pressed"), label(action));
    }

    String binding(GameAction action) {
        return text("keys." + action.name().toLowerCase(Locale.ROOT)) + "    " + label(action);
    }

    String label(GameAction action) {
        return text("action." + action.name().toLowerCase(Locale.ROOT));
    }

    private String text(String key) {
        String value = bundle.getProperty(key);
        if (value == null) {
            throw new IllegalStateException("Missing UI string: " + key);
        }
        return value;
    }

    private static Properties load(Locale locale) {
        Properties defaults = loadFile("/i18n/messages.properties");
        Properties overlay = new Properties(defaults);
        String tag = locale.toLanguageTag().replace('-', '_');
        String language = locale.getLanguage();
        if ("zh".equals(language)) {
            overlay.putAll(loadFile("/i18n/messages_zh_CN.properties"));
        } else if ("en".equals(language)) {
            overlay.putAll(loadFile("/i18n/messages_en.properties"));
        } else if (!"root".equalsIgnoreCase(tag)) {
            tryLoadOptional(overlay, "/i18n/messages_" + tag + ".properties");
        }
        return overlay;
    }

    private static void tryLoadOptional(Properties target, String path) {
        if (UiText.class.getResource(path) != null) {
            target.putAll(loadFile(path));
        }
    }

    private static Properties loadFile(String path) {
        InputStream in = UiText.class.getResourceAsStream(path);
        if (in == null) {
            throw new IllegalStateException("Missing resource " + path);
        }
        Properties properties = new Properties();
        try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            properties.load(reader);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
        return properties;
    }
}

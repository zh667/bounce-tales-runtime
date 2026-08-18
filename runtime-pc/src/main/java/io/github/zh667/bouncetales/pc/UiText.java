package io.github.zh667.bouncetales.pc;

import io.github.zh667.bouncetales.logic.AssetInventory;
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

    String label(GameAction action) {
        return text("action." + action.name().toLowerCase(Locale.ROOT));
    }

    String binding(GameAction action) {
        return text("keys." + action.name().toLowerCase(Locale.ROOT)) + "    " + label(action);
    }

    String assetsHeading() {
        return text("assets.heading");
    }

    String assetsStatus(AssetInventory inventory) {
        return switch (inventory.status()) {
            case MISSING_DIR -> String.format(text("assets.missing"), inventory.directory());
            case EMPTY -> String.format(text("assets.empty"), inventory.directory());
            case JAR_FOUND -> String.format(
                    text("assets.found"),
                    inventory.fileName().orElse("?"),
                    inventory.midletName().orElse("?"),
                    inventory.midletVersion().orElse("?"),
                    inventory.entryCount());
            case AMBIGUOUS -> String.format(text("assets.ambiguous"), inventory.directory());
            case UNREADABLE -> String.format(
                    text("assets.unreadable"),
                    inventory.fileName().orElse("?"),
                    inventory.error().orElse("?"));
        };
    }

    String midletFailed(String detail) {
        return String.format(text("midlet.failed"), detail == null ? "?" : detail);
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
        String language = locale.getLanguage();
        if ("zh".equals(language)) {
            overlay.putAll(loadFile("/i18n/messages_zh_CN.properties"));
        } else if ("en".equals(language)) {
            overlay.putAll(loadFile("/i18n/messages_en.properties"));
        }
        return overlay;
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

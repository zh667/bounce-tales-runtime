package io.github.zh667.bouncetales.pc;

import io.github.zh667.bouncetales.logic.AssetInventory;
import io.github.zh667.bouncetales.logic.GameAction;
import io.github.zh667.bouncetales.logic.PackedKind;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
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

    String assetsHeading() {
        return text("assets.heading");
    }

    String assetsHint() {
        return text("assets.hint");
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

    String assetsDetails(AssetInventory inventory) {
        if (!inventory.ready()) {
            return "";
        }
        return String.format(
                text("assets.details"),
                inventory.vendor().orElse("?"),
                yesNo(inventory.hasIcon()),
                yesNo(inventory.hasChineseLang()));
    }

    String imageEmpty() {
        return text("media.image.empty");
    }

    String imageLine(Workbench workbench) {
        int total = workbench.catalog.images().size();
        if (total == 0) {
            return text("media.image.none");
        }
        return String.format(
                text("media.image.line"),
                workbench.imageIndex() + 1,
                total,
                workbench.imageName().orElse("?"));
    }

    String midiLine(Workbench workbench) {
        int total = workbench.catalog.midis().size();
        if (total == 0) {
            return text("media.midi.none");
        }
        return String.format(
                text("media.midi.line"),
                workbench.midiIndex() + 1,
                total,
                workbench.midiName().orElse("?"),
                midiStatus(workbench.midi.status()));
    }

    String langLine(Workbench workbench) {
        if (workbench.lang.size() == 0) {
            return text("media.lang.none");
        }
        String name = workbench.langName.isBlank() ? "?" : workbench.langName;
        return String.format(text("media.lang.line"), name, workbench.lang.size(), workbench.lang.sample());
    }

    String packedLine(Workbench workbench) {
        if (workbench.packed.fileCount() == 0) {
            return text("media.packed.none");
        }
        return String.format(
                text("media.packed.line"),
                workbench.packed.fileCount(),
                workbench.packed.batchCount(),
                workbench.packed.countKind(PackedKind.LEVEL));
    }

    String saveLine(boolean savedThisSession, Path directory) {
        return String.format(
                text(savedThisSession ? "media.save.ok" : "media.save.idle"), directory);
    }

    String workbenchHint() {
        return text("media.hint");
    }

    private String midiStatus(MidiPlayer.Status status) {
        return switch (status) {
            case IDLE -> text("media.midi.idle");
            case READY -> text("media.midi.ready");
            case PLAYING -> text("media.midi.playing");
            case UNAVAILABLE -> text("media.midi.unavailable");
            case FAILED -> text("media.midi.failed");
        };
    }

    private String yesNo(boolean value) {
        return text(value ? "word.yes" : "word.no");
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

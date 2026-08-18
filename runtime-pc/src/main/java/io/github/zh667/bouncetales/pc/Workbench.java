package io.github.zh667.bouncetales.pc;

import io.github.zh667.bouncetales.logic.AssetLocator;
import io.github.zh667.bouncetales.logic.JarCatalog;
import io.github.zh667.bouncetales.logic.LangTable;
import io.github.zh667.bouncetales.logic.PackedIndex;
import io.github.zh667.bouncetales.logic.SaveStore;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Optional;
import javax.imageio.ImageIO;

/**
 * Session state for the desktop media workbench: catalog, preview image, MIDI, lang, save.
 */
final class Workbench {
    static final String SLOT = "workbench";

    final JarCatalog catalog;
    final LangTable lang;
    final PackedIndex packed;
    final SaveStore saves;
    final MidiPlayer midi = new MidiPlayer();
    final String langName;

    private int imageIndex;
    private int midiIndex;
    private BufferedImage image;

    Workbench(JarCatalog catalog, LangTable lang, PackedIndex packed, SaveStore saves, String langName) {
        this.catalog = catalog;
        this.lang = lang;
        this.packed = packed;
        this.saves = saves;
        this.langName = langName;
        restore();
        reloadImage();
        reloadMidi();
    }

    static Workbench open(Path jar, SaveStore saves) throws IOException {
        JarCatalog catalog = JarCatalog.open(jar);
        String langName = catalog.preferredLang().orElse("");
        LangTable lang = catalog.preferredLang()
                .flatMap(name -> AssetLocator.readEntry(jar, name))
                .map(LangTable::tryParse)
                .orElseGet(LangTable::empty);
        PackedIndex packed = catalog.hasPackedIndex()
                ? AssetLocator.readEntry(jar, "a").map(PackedIndex::tryParse).orElseGet(PackedIndex::empty)
                : PackedIndex.empty();
        return new Workbench(catalog, lang, packed, saves, langName);
    }

    static Workbench empty(SaveStore saves) {
        return new Workbench(JarCatalog.empty(), LangTable.empty(), PackedIndex.empty(), saves, "");
    }

    int imageIndex() {
        return imageIndex;
    }

    int midiIndex() {
        return midiIndex;
    }

    Optional<BufferedImage> image() {
        return Optional.ofNullable(image);
    }

    Optional<String> imageName() {
        return nameAt(catalog.images(), imageIndex);
    }

    Optional<String> midiName() {
        return nameAt(catalog.midis(), midiIndex);
    }

    void nextImage() {
        if (catalog.images().isEmpty()) {
            return;
        }
        imageIndex = (imageIndex + 1) % catalog.images().size();
        reloadImage();
    }

    void previousImage() {
        if (catalog.images().isEmpty()) {
            return;
        }
        imageIndex = (imageIndex - 1 + catalog.images().size()) % catalog.images().size();
        reloadImage();
    }

    void nextMidi() {
        if (catalog.midis().isEmpty()) {
            return;
        }
        midiIndex = (midiIndex + 1) % catalog.midis().size();
        reloadMidi();
    }

    void toggleMidi() {
        midi.toggle();
    }

    void stopMidi() {
        midi.stop();
    }

    void poll() {
        midi.poll();
    }

    void close() {
        midi.close();
    }

    boolean save() {
        try {
            saves.write(SLOT, encode());
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    private void restore() {
        try {
            Optional<byte[]> raw = saves.read(SLOT);
            if (raw.isEmpty()) {
                return;
            }
            for (String line : new String(raw.get(), StandardCharsets.UTF_8).split("\\R")) {
                int eq = line.indexOf('=');
                if (eq <= 0) {
                    continue;
                }
                String key = line.substring(0, eq).trim();
                String value = line.substring(eq + 1).trim();
                if ("image".equals(key)) {
                    imageIndex = clamp(parseIndex(value), catalog.images().size());
                } else if ("midi".equals(key)) {
                    midiIndex = clamp(parseIndex(value), catalog.midis().size());
                }
            }
        } catch (IOException ignored) {
            // keep defaults
        }
    }

    private void reloadImage() {
        image = nameAt(catalog.images(), imageIndex)
                .flatMap(name -> catalog.jar().flatMap(jar -> AssetLocator.readEntry(jar, name)))
                .map(Workbench::decodePng)
                .orElse(null);
    }

    private void reloadMidi() {
        midi.stop();
        byte[] bytes = nameAt(catalog.midis(), midiIndex)
                .flatMap(name -> catalog.jar().flatMap(jar -> AssetLocator.readEntry(jar, name)))
                .orElse(null);
        midi.load(bytes);
    }

    private byte[] encode() {
        String body = "image=" + imageIndex + "\nmidi=" + midiIndex + "\n";
        return body.getBytes(StandardCharsets.UTF_8);
    }

    private static Optional<String> nameAt(java.util.List<String> names, int index) {
        if (names.isEmpty() || index < 0 || index >= names.size()) {
            return Optional.empty();
        }
        return Optional.of(names.get(index));
    }

    private static int parseIndex(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private static int clamp(int index, int size) {
        if (size <= 0) {
            return 0;
        }
        if (index < 0) {
            return 0;
        }
        if (index >= size) {
            return size - 1;
        }
        return index;
    }

    private static BufferedImage decodePng(byte[] bytes) {
        try {
            return ImageIO.read(new ByteArrayInputStream(bytes));
        } catch (IOException ex) {
            return null;
        }
    }
}

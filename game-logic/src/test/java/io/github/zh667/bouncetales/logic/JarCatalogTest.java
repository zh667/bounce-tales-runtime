package io.github.zh667.bouncetales.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class JarCatalogTest {
    @TempDir
    Path temp;

    @Test
    void listsPngMidiLangAndPackedIndexWithoutKeepingBytes() throws IOException {
        Path jar = temp.resolve("demo.jar");
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        try (OutputStream raw = Files.newOutputStream(jar);
                JarOutputStream out = new JarOutputStream(raw, manifest)) {
            put(out, "icon.png", new byte[] {1, 2, 3});
            put(out, "sprites/ball.png", new byte[] {4});
            put(out, "music/theme.mid", new byte[] {5, 6});
            put(out, "lang.zh-CN", "x".getBytes(StandardCharsets.UTF_8));
            put(out, "lang.xx", "y".getBytes(StandardCharsets.UTF_8));
            put(out, "a", new byte[] {0, 0});
        }
        JarCatalog catalog = JarCatalog.open(jar);
        assertEquals(2, catalog.images().size());
        assertEquals(List.of("icon.png", "sprites/ball.png").toString(), catalog.images().toString());
        assertEquals(1, catalog.midis().size());
        assertEquals("music/theme.mid", catalog.midis().get(0));
        assertEquals("lang.zh-CN", catalog.preferredLang().orElseThrow());
        assertTrue(catalog.hasPackedIndex());
        assertTrue(catalog.hasMedia());
        assertTrue(catalog.toLogLine().contains("png=2"));
    }

    @Test
    void emptyCatalogHasNoMedia() {
        JarCatalog catalog = JarCatalog.empty();
        assertFalse(catalog.hasMedia());
        assertTrue(catalog.jar().isEmpty());
        assertTrue(catalog.preferredLang().isEmpty());
    }

    private static void put(JarOutputStream out, String name, byte[] bytes) throws IOException {
        out.putNextEntry(new ZipEntry(name));
        out.write(bytes);
        out.closeEntry();
    }
}

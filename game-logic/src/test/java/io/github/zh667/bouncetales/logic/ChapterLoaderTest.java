package io.github.zh667.bouncetales.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ChapterLoaderTest {
    @TempDir
    Path temp;

    @Test
    void loadsBfEntryFromPlaceholderJar() throws IOException {
        Path jar = temp.resolve("demo.jar");
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        try (OutputStream raw = Files.newOutputStream(jar);
                JarOutputStream out = new JarOutputStream(raw, manifest)) {
            out.putNextEntry(new ZipEntry("bf"));
            out.write(RlefFixtures.platformChapter());
            out.closeEntry();
        }
        RlefLevel level = ChapterLoader.load(jar, PackedIndex.empty(), ChapterId.MISTY_MORNING).orElseThrow();
        assertEquals(1, level.terrain().size());
        assertTrue(level.playable());
    }
}

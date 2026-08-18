package io.github.zh667.bouncetales.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class AssetLocatorTest {
    @TempDir
    Path temp;

    @Test
    void missingDirectory() {
        Path missing = temp.resolve("no-such-assets");
        AssetInventory inventory = AssetLocator.scan(missing);
        assertEquals(AssetInventory.Status.MISSING_DIR, inventory.status());
        assertFalse(inventory.ready());
        assertTrue(inventory.toLogLine().startsWith("assets: MISSING_DIR"));
    }

    @Test
    void emptyDirectoryIgnoresReadme() throws IOException {
        Files.writeString(temp.resolve("README.md"), "keep", StandardCharsets.UTF_8);
        Files.writeString(temp.resolve(".gitkeep"), "", StandardCharsets.UTF_8);
        AssetInventory inventory = AssetLocator.scan(temp);
        assertEquals(AssetInventory.Status.EMPTY, inventory.status());
        assertFalse(inventory.ready());
    }

    @Test
    void loadsPlaceholderJarWithoutOriginalArt() throws IOException {
        Path jar = writeJar("bounce-placeholder.jar", "Bounce Tales", "2.0.14", "Nokia");
        AssetInventory inventory = AssetLocator.scan(temp);
        assertEquals(AssetInventory.Status.JAR_FOUND, inventory.status());
        assertTrue(inventory.ready());
        assertEquals(jar, inventory.jar().orElseThrow());
        assertEquals("Bounce Tales", inventory.midletName().orElseThrow());
        assertEquals("2.0.14", inventory.midletVersion().orElseThrow());
        assertEquals("Nokia", inventory.vendor().orElseThrow());
        assertTrue(inventory.hasIcon());
        assertTrue(inventory.hasChineseLang());
        assertTrue(inventory.entryCount() >= 2);
        assertTrue(AssetLocator.readEntry(jar, "lang.zh-CN").isPresent());
    }

    @Test
    void prefersBounceTalesWhenMultipleJarsExist() throws IOException {
        writeJar("other.jar", "Something Else", "1.0", "Demo");
        Path bounce = writeJar("tales.jar", "Bounce Tales", "2.0.14", "Nokia");
        AssetInventory inventory = AssetLocator.scan(temp);
        assertEquals(AssetInventory.Status.JAR_FOUND, inventory.status());
        assertEquals(bounce.getFileName().toString(), inventory.fileName().orElseThrow());
    }

    @Test
    void ambiguousWhenTwoBounceJarsExist() throws IOException {
        writeJar("a.jar", "Bounce Tales", "2.0.14", "Nokia");
        writeJar("b.jar", "Bounce Tales", "2.0.14", "Nokia");
        AssetInventory inventory = AssetLocator.scan(temp);
        assertEquals(AssetInventory.Status.AMBIGUOUS, inventory.status());
        assertEquals(2, inventory.extraJars().size());
    }

    @Test
    void skipsPackagedHostJarSittingBesideTheGame() throws IOException {
        Path game = writeJar("bounce-tales.jar", "Bounce Tales", "2.0.14", "Nokia");
        Path host = temp.resolve("bounce-tales-runtime.jar");
        Manifest manifest = new Manifest();
        Attributes attrs = manifest.getMainAttributes();
        attrs.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        attrs.put(Attributes.Name.MAIN_CLASS, "io.github.zh667.bouncetales.pc.DesktopRuntime");
        try (OutputStream raw = Files.newOutputStream(host);
                JarOutputStream out = new JarOutputStream(raw, manifest)) {
            out.putNextEntry(new ZipEntry("placeholder.txt"));
            out.write("host".getBytes(StandardCharsets.UTF_8));
            out.closeEntry();
        }
        AssetInventory inventory = AssetLocator.scan(temp);
        assertEquals(AssetInventory.Status.JAR_FOUND, inventory.status());
        assertEquals(game.getFileName().toString(), inventory.fileName().orElseThrow());
        assertTrue(AssetLocator.isHostRuntimeJar(host));
    }

    @Test
    void unreadableJar() throws IOException {
        Path junk = temp.resolve("broken.jar");
        Files.writeString(junk, "not a zip", StandardCharsets.UTF_8);
        AssetInventory inventory = AssetLocator.scan(temp);
        assertEquals(AssetInventory.Status.UNREADABLE, inventory.status());
        assertTrue(inventory.error().isPresent());
    }

    private Path writeJar(String fileName, String midlet, String version, String vendor) throws IOException {
        Path jar = temp.resolve(fileName);
        Manifest manifest = new Manifest();
        Attributes attrs = manifest.getMainAttributes();
        attrs.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        attrs.putValue("MIDlet-Name", midlet);
        attrs.putValue("MIDlet-Version", version);
        attrs.putValue("MIDlet-Vendor", vendor);
        try (OutputStream raw = Files.newOutputStream(jar);
                JarOutputStream out = new JarOutputStream(raw, manifest)) {
            out.putNextEntry(new ZipEntry("icon.png"));
            out.write(new byte[] {1, 2, 3, 4});
            out.closeEntry();
            out.putNextEntry(new ZipEntry("lang.zh-CN"));
            out.write("placeholder".getBytes(StandardCharsets.UTF_8));
            out.closeEntry();
        }
        return jar;
    }
}

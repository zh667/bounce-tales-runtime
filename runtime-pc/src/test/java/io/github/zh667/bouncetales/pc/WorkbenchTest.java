package io.github.zh667.bouncetales.pc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zh667.bouncetales.logic.LangTable;
import io.github.zh667.bouncetales.logic.SaveStore;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class WorkbenchTest {
    private static final byte[] PNG_1X1 = Base64.getDecoder()
            .decode("iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8BQDwAEhQGAhKmMIQAAAABJRU5ErkJggg==");

    @TempDir
    Path temp;

    @Test
    void loadsSyntheticJarAndRestoresSaveSlot() throws IOException {
        Path jar = writeJar();
        SaveStore store = new SaveStore(temp.resolve("saves"));
        store.write(Workbench.SLOT, "image=1\nmidi=0\n".getBytes(java.nio.charset.StandardCharsets.UTF_8));
        Workbench workbench = Workbench.open(jar, store);
        assertEquals(2, workbench.catalog.images().size());
        assertEquals(1, workbench.imageIndex());
        assertEquals("sprites/ball.png", workbench.imageName().orElseThrow());
        assertTrue(workbench.image().isPresent());
        assertEquals("薄雾早晨", workbench.lang.sample());
        assertEquals(1, workbench.packed.fileCount());
        workbench.nextImage();
        assertEquals(0, workbench.imageIndex());
        assertTrue(workbench.save());
        Workbench again = Workbench.open(jar, store);
        assertEquals(0, again.imageIndex());
        workbench.close();
        again.close();
    }

    private Path writeJar() throws IOException {
        Path jar = temp.resolve("demo.jar");
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        try (OutputStream raw = Files.newOutputStream(jar);
                JarOutputStream out = new JarOutputStream(raw, manifest)) {
            put(out, "icon.png", PNG_1X1);
            put(out, "sprites/ball.png", PNG_1X1);
            put(out, "lang.zh-CN", langBytes("薄雾早晨", "继续"));
            put(out, "a", packedBytes());
        }
        return jar;
    }

    private static void put(JarOutputStream out, String name, byte[] bytes) throws IOException {
        out.putNextEntry(new ZipEntry(name));
        out.write(bytes);
        out.closeEntry();
    }

    private static byte[] langBytes(String... messages) throws IOException {
        int count = messages.length;
        byte[][] encoded = new byte[count][];
        int pos = count * 2;
        int[] offsets = new int[count];
        for (int i = 0; i < count; i++) {
            offsets[i] = pos;
            ByteArrayOutputStream one = new ByteArrayOutputStream();
            try (DataOutputStream stream = new DataOutputStream(one)) {
                stream.writeUTF(messages[i]);
            }
            encoded[i] = one.toByteArray();
            pos += encoded[i].length;
        }
        ByteArrayOutputStream file = new ByteArrayOutputStream();
        try (DataOutputStream out = new DataOutputStream(file)) {
            for (int offset : offsets) {
                out.writeShort(offset);
            }
            for (byte[] chunk : encoded) {
                out.write(chunk);
            }
        }
        assertEquals(2, LangTable.parse(file.toByteArray()).size());
        return file.toByteArray();
    }

    private static byte[] packedBytes() throws IOException {
        ByteArrayOutputStream raw = new ByteArrayOutputStream();
        try (DataOutputStream out = new DataOutputStream(raw)) {
            out.writeShort(1);
            out.writeUTF("icon.png");
            out.writeInt(-1);
            out.writeInt(8);
            out.writeShort(0);
            out.writeShort(0);
        }
        return raw.toByteArray();
    }
}

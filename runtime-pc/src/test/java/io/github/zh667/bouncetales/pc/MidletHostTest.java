package io.github.zh667.bouncetales.pc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nokia.mid.ui.FullCanvas;
import io.github.zh667.bouncetales.logic.AssetInventory;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import javax.microedition.lcdui.Canvas;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class MidletHostTest {
    @Test
    void readsMidletOneClassFromSyntheticJar(@TempDir Path temp) throws Exception {
        Path jar = writeMidletJar(temp.resolve("probe.jar"));
        MidletManifest manifest = MidletManifest.read(jar).orElseThrow();
        assertEquals("RMIDlet", manifest.midletClass());
        assertEquals("Bounce Tales", manifest.midletName());
        assertEquals("2.0.14", manifest.version());
        assertTrue(manifest.toLogLine().contains("RMIDlet"));
    }

    @Test
    void headlessStartDoesNotNeedOriginalJar(@TempDir Path temp) throws Exception {
        writeMidletJar(temp.resolve("probe.jar"));
        AssetInventory inventory = DesktopRuntime.start(true, temp);
        assertTrue(inventory.ready());
        assertEquals("RMIDlet", MidletManifest.read(inventory.jar().orElseThrow()).orElseThrow().midletClass());
    }

    @Test
    void installPlatformUsesNokiaNotPc(@TempDir Path temp) throws Exception {
        Path jar = writeMidletJar(temp.resolve("probe.jar"));
        String previous = System.getProperty("microedition.platform");
        try {
            MidletHost.installPlatform(MidletManifest.read(jar).orElseThrow());
            assertEquals("NokiaN73", System.getProperty("microedition.platform"));
        } finally {
            if (previous == null) {
                System.clearProperty("microedition.platform");
            } else {
                System.setProperty("microedition.platform", previous);
            }
        }
    }

    @Test
    void keymapUsesNokiaSoftkeys() {
        assertEquals(FullCanvas.KEY_UP_ARROW, MidletKeyMap.keyCode(KeyEvent.VK_W));
        assertEquals(FullCanvas.KEY_SOFTKEY3, MidletKeyMap.keyCode(KeyEvent.VK_ENTER));
        assertEquals(FullCanvas.KEY_SOFTKEY2, MidletKeyMap.keyCode(KeyEvent.VK_BACK_SPACE));
        assertEquals(Canvas.KEY_STAR, MidletKeyMap.keyCode(KeyEvent.VK_Q));
    }

    private static Path writeMidletJar(Path jar) throws IOException {
        Manifest manifest = new Manifest();
        Attributes attrs = manifest.getMainAttributes();
        attrs.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        attrs.putValue("MIDlet-Name", "Bounce Tales");
        attrs.putValue("MIDlet-Version", "2.0.14");
        attrs.putValue("MIDlet-Vendor", "Test");
        attrs.putValue("MIDlet-1", "Bounce Tales, /icon.png, RMIDlet");
        attrs.putValue("Nokia-Platform", "Nokia*");
        try (OutputStream raw = Files.newOutputStream(jar);
                JarOutputStream out = new JarOutputStream(raw, manifest)) {
            out.putNextEntry(new ZipEntry("icon.png"));
            out.write(new byte[] {1, 2, 3});
            out.closeEntry();
        }
        return jar;
    }
}

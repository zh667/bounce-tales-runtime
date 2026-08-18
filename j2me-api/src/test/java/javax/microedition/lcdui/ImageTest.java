package javax.microedition.lcdui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ImageTest {
    @Test
    void createImageFromPngBytes() throws Exception {
        byte[] png = pngBytes();
        Image image = Image.createImage(png, 0, png.length);
        assertEquals(2, image.getWidth());
        assertEquals(2, image.getHeight());
    }

    @Test
    void createImageFromClasspathUsesContextClassLoader(@TempDir Path temp) throws Exception {
        Path jar = temp.resolve("pics.jar");
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        try (JarOutputStream out = new JarOutputStream(Files.newOutputStream(jar), manifest)) {
            out.putNextEntry(new ZipEntry("dot.png"));
            out.write(pngBytes());
            out.closeEntry();
        }
        ClassLoader previous = Thread.currentThread().getContextClassLoader();
        try (URLClassLoader loader = new URLClassLoader(new java.net.URL[] {jar.toUri().toURL()}, previous)) {
            Thread.currentThread().setContextClassLoader(loader);
            Image image = Image.createImage("/dot.png");
            assertEquals(2, image.getWidth());
        } finally {
            Thread.currentThread().setContextClassLoader(previous);
        }
    }

    @Test
    void missingResourceThrows() {
        assertThrows(IOException.class, () -> Image.createImage("/no-such-image.png"));
    }

    private static byte[] pngBytes() throws IOException {
        BufferedImage raster = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        raster.setRGB(0, 0, 0xFF0000);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(raster, "png", out);
        return out.toByteArray();
    }
}

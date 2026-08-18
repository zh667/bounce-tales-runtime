package io.github.zh667.bouncetales.pc;

import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Loads the user JAR so packed-resource {@code skip} works and language tables
 * reopen per message. Zip {@code skip()} can return 0, which misaligns PNG/MIDI
 * slices. {@code ByteArrayInputStream.mark} would also break original
 * {@code StringManager} ({@code mark(512)} is too small).
 */
final class MidletClassLoader extends URLClassLoader {
    MidletClassLoader(URL jar, ClassLoader parent) {
        super(new URL[] {jar}, parent);
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        InputStream in = super.getResourceAsStream(name);
        if (in == null) {
            return null;
        }
        try (InputStream stream = in) {
            return new ResourceStream(stream.readAllBytes());
        } catch (IOException ex) {
            return super.getResourceAsStream(name);
        }
    }

    static final class ResourceStream extends FilterInputStream {
        ResourceStream(byte[] data) {
            super(new ByteArrayInputStream(data));
        }

        @Override
        public boolean markSupported() {
            return false;
        }
    }
}

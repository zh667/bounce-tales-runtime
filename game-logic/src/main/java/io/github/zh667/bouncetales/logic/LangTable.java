package io.github.zh667.bouncetales.logic;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Bounce Tales {@code lang.*} table: big-endian u16 offsets, then Java modified UTF-8 strings.
 */
public final class LangTable {
    private final List<String> messages;

    private LangTable(List<String> messages) {
        this.messages = List.copyOf(messages);
    }

    public static LangTable empty() {
        return new LangTable(List.of());
    }

    public static LangTable tryParse(byte[] bytes) {
        try {
            return parse(bytes);
        } catch (IOException | RuntimeException ex) {
            return empty();
        }
    }

    public static LangTable parse(byte[] bytes) throws IOException {
        if (bytes == null || bytes.length < 2) {
            throw new IOException("lang table too short");
        }
        try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes))) {
            int firstOffset = in.readUnsignedShort();
            if (firstOffset < 2 || (firstOffset & 1) != 0) {
                throw new IOException("invalid lang offset table");
            }
            int count = firstOffset / 2;
            int[] offsets = new int[count];
            offsets[0] = firstOffset;
            for (int i = 1; i < count; i++) {
                offsets[i] = in.readUnsignedShort();
            }
            List<String> messages = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                messages.add(readUtfAt(bytes, offsets[i]));
            }
            return new LangTable(messages);
        }
    }

    public int size() {
        return messages.size();
    }

    public Optional<String> message(int id) {
        if (id < 0 || id >= messages.size()) {
            return Optional.empty();
        }
        return Optional.of(messages.get(id));
    }

    public String sample() {
        for (String message : messages) {
            if (message != null && !message.isBlank()) {
                return message;
            }
        }
        return "";
    }

    private static String readUtfAt(byte[] bytes, int offset) throws IOException {
        if (offset < 0 || offset >= bytes.length) {
            throw new IOException("lang string offset out of range");
        }
        try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes, offset, bytes.length - offset))) {
            return Objects.requireNonNullElse(in.readUTF(), "");
        }
    }
}

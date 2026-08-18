package io.github.zh667.bouncetales.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.junit.jupiter.api.Test;

class LangTableTest {
    @Test
    void roundTripsModifiedUtf8IncludingChinese() throws IOException {
        LangTable table = LangTable.parse(encode("薄雾早晨", "继续", ""));
        assertEquals(3, table.size());
        assertEquals("薄雾早晨", table.message(0).orElseThrow());
        assertEquals("继续", table.message(1).orElseThrow());
        assertEquals("", table.message(2).orElseThrow());
        assertEquals("薄雾早晨", table.sample());
        assertTrue(table.message(99).isEmpty());
    }

    @Test
    void tryParseReturnsEmptyOnGarbage() {
        assertEquals(0, LangTable.tryParse(new byte[] {1, 2, 3}).size());
        assertEquals(0, LangTable.tryParse(null).size());
    }

    static byte[] encode(String... messages) throws IOException {
        int count = messages.length;
        byte[][] encoded = new byte[count][];
        int pos = count * 2;
        int[] offsets = new int[count];
        for (int i = 0; i < count; i++) {
            offsets[i] = pos;
            ByteArrayOutputStream one = new ByteArrayOutputStream();
            try (DataOutputStream out = new DataOutputStream(one)) {
                out.writeUTF(messages[i]);
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
        return file.toByteArray();
    }
}

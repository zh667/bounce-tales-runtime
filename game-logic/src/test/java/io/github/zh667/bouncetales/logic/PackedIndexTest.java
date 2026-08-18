package io.github.zh667.bouncetales.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.junit.jupiter.api.Test;

class PackedIndexTest {
    @Test
    void parsesSyntheticIndex() throws IOException {
        PackedIndex index = PackedIndex.parse(encode());
        assertEquals(2, index.fileCount());
        assertEquals("img/ball.png", index.files().get(0).path());
        assertEquals(12, index.files().get(0).skipOffset());
        assertEquals(34, index.files().get(0).readLength());
        assertTrue(index.files().get(0).exists());
        assertEquals(2, index.batchCount());
        assertEquals(1, index.countKind(PackedKind.IMAGE));
        assertEquals(1, index.countKind(PackedKind.LEVEL));
        assertEquals(1, index.residents().size());
        assertEquals(7, index.residents().get(0).type());
        assertTrue(index.toLogLine().contains("levels=1"));
    }

    @Test
    void tryParseReturnsEmptyOnGarbage() {
        PackedIndex index = PackedIndex.tryParse(new byte[] {0, 9, 1, 2});
        assertEquals(0, index.fileCount());
        assertEquals(0, index.batchCount());
    }

    static byte[] encode() throws IOException {
        ByteArrayOutputStream raw = new ByteArrayOutputStream();
        try (DataOutputStream out = new DataOutputStream(raw)) {
            out.writeShort(2);
            out.writeUTF("img/ball.png");
            out.writeInt(12);
            out.writeInt(34);
            out.writeUTF("levels/one.bin");
            out.writeInt(-1);
            out.writeInt(8);
            out.writeShort(2);
            out.writeByte(2);
            out.writeByte(1);
            out.writeShort(10);
            out.writeShort(11);
            out.writeByte(8);
            out.writeByte(0);
            out.writeShort(20);
            out.writeShort(1);
            out.writeShort(7);
            out.writeShort(3);
        }
        return raw.toByteArray();
    }
}

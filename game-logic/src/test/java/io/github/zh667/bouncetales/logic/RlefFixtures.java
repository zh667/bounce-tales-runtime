package io.github.zh667.bouncetales.logic;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

final class RlefFixtures {
    private RlefFixtures() {
    }

    static byte[] platformChapter() throws IOException {
        int[] xs = {-120, 120, 120, -120};
        int[] ys = {0, 0, 24, 24};
        int[] faces = {0, 1, 2, 0, 2, 3};
        byte[] geom = geometryPayload(0, -1, 0, 0, xs, ys, faces, 0xFF226644);
        byte[] root = transformPayload(-1, -1, 0, 0);
        byte[] player = transformPayload(0, 0, 0, 70);
        ByteArrayOutputStream raw = new ByteArrayOutputStream();
        try (DataOutputStream out = new DataOutputStream(raw)) {
            out.writeInt(RlefLevel.SIGNATURE);
            out.writeInt(RlefLevel.VERSION);
            out.writeShort(3);
            out.writeShort(0);
            out.writeShort(0);
            writeObject(out, 0, root);
            writeObject(out, 4, geom);
            writeObject(out, 8, player);
            out.writeByte(RlefKind.END);
        }
        return raw.toByteArray();
    }

    private static void writeObject(DataOutputStream out, int type, byte[] payload) throws IOException {
        out.writeByte(type);
        out.writeShort(payload.length);
        out.write(payload);
    }

    private static byte[] transformPayload(int parent, int prev, int x, int y) throws IOException {
        ByteArrayOutputStream raw = new ByteArrayOutputStream();
        try (DataOutputStream out = new DataOutputStream(raw)) {
            out.writeShort(parent);
            out.writeShort(prev);
            out.writeByte(1);
            out.writeShort(x);
            out.writeShort(y);
            out.writeInt(0);
        }
        return raw.toByteArray();
    }

    private static byte[] geometryPayload(
            int parent, int prev, int x, int y, int[] xs, int[] ys, int[] faces, int rgb) throws IOException {
        int bits = 9;
        byte[] packedX = BitPack.writeSigned(xs, bits, 0);
        byte[] packedY = BitPack.writeSigned(ys, bits, 0);
        byte[] packedF = BitPack.writeSigned(faces, 3, 0);
        ByteArrayOutputStream raw = new ByteArrayOutputStream();
        try (DataOutputStream out = new DataOutputStream(raw)) {
            out.writeShort(parent);
            out.writeShort(prev);
            out.writeByte(1);
            out.writeShort(x);
            out.writeShort(y);
            out.writeInt(0);
            out.writeShort(xs.length);
            out.writeShort(faces.length);
            out.writeInt(rgb);
            out.writeByte(bits);
            out.writeShort(0);
            out.write(packedX);
            out.writeShort(0);
            out.write(packedY);
            out.writeByte(3);
            out.write(packedF);
            out.writeShort(-1);
        }
        return raw.toByteArray();
    }
}

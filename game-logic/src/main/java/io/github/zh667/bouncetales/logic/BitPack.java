package io.github.zh667.bouncetales.logic;

import java.io.ByteArrayOutputStream;

/**
 * LSB-first signed bit packing used by RLEF vertex and index buffers.
 */
public final class BitPack {
    public record Result(int[] values, int nextOffset) {}

    private BitPack() {
    }

    public static Result readSigned(byte[] src, int offset, int count, int bits, int base) {
        if (src == null) {
            throw new IllegalArgumentException("src");
        }
        if (count < 0 || bits < 1 || bits > 31) {
            throw new IllegalArgumentException("count/bits");
        }
        int[] target = new int[count];
        int bitBuffer = 0;
        int bufIdx = 0;
        int index = 0;
        int pos = offset;
        int signBit = 1 << (bits - 1);
        int mask = (1 << bits) - 1;
        while (index < count) {
            if (pos >= src.length) {
                throw new IllegalArgumentException("bit pack overflow at " + pos);
            }
            int oldBitsWithNewByte = bitBuffer | (src[pos++] << bufIdx);
            bufIdx += 8;
            bitBuffer = oldBitsWithNewByte & ((1 << bufIdx) - 1);
            while (bits <= bufIdx && index < count) {
                int maskedBits = bitBuffer & mask;
                if ((maskedBits & signBit) != 0) {
                    maskedBits |= ~mask;
                }
                target[index++] = maskedBits + base;
                bufIdx -= bits;
                bitBuffer >>>= bits;
            }
        }
        return new Result(target, pos);
    }

    public static byte[] writeSigned(int[] values, int bits, int base) {
        if (values == null || bits < 1 || bits > 31) {
            throw new IllegalArgumentException("values/bits");
        }
        int mask = (1 << bits) - 1;
        int buffer = 0;
        int filled = 0;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (int value : values) {
            int stored = (value - base) & mask;
            buffer |= stored << filled;
            filled += bits;
            while (filled >= 8) {
                out.write(buffer & 0xFF);
                buffer >>>= 8;
                filled -= 8;
            }
        }
        if (filled > 0) {
            out.write(buffer & 0xFF);
        }
        return out.toByteArray();
    }
}

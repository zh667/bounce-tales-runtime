package io.github.zh667.bouncetales.logic;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class BitPackTest {
    @Test
    void roundTripsSignedValues() {
        int[] values = {-98, 0, 199, 496, -815};
        byte[] packed = BitPack.writeSigned(values, 11, 199);
        BitPack.Result result = BitPack.readSigned(packed, 0, values.length, 11, 199);
        assertArrayEquals(values, result.values());
        assertEquals(packed.length, result.nextOffset());
    }
}

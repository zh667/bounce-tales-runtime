package io.github.zh667.bouncetales.logic;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SaveStoreTest {
    @TempDir
    Path temp;

    @Test
    void roundTripsSlotBytes() throws IOException {
        SaveStore store = new SaveStore(temp);
        byte[] payload = "slot-one".getBytes(StandardCharsets.UTF_8);
        store.write("workbench", payload);
        assertTrue(store.exists("workbench"));
        assertArrayEquals(payload, store.read("workbench").orElseThrow());
    }

    @Test
    void rejectsPathTraversalSlots() {
        SaveStore store = new SaveStore(temp);
        assertThrows(IllegalArgumentException.class, () -> store.write("../escape", new byte[] {1}));
        assertFalse(store.exists("../escape"));
    }

    @Test
    void missingSlotIsEmpty() throws IOException {
        SaveStore store = new SaveStore(temp);
        assertTrue(store.read("missing").isEmpty());
        assertFalse(store.exists("missing"));
    }
}

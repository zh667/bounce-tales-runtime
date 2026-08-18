package javax.microedition.rms;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class RecordStoreTest {
    @AfterEach
    void reset() {
        RecordStore.resetOpenStores();
        System.clearProperty("bounce.save.dir");
    }

    @Test
    void missingStoreThrowsWhenNotCreated(@TempDir Path temp) {
        System.setProperty("bounce.save.dir", temp.toString());
        assertThrows(RecordStoreException.class, () -> RecordStore.openRecordStore("game", false));
    }

    @Test
    void roundTripRecord(@TempDir Path temp) throws Exception {
        System.setProperty("bounce.save.dir", temp.toString());
        RecordStore store = RecordStore.openRecordStore("game", true);
        store.addRecord(null, 0, 0);
        store.setRecord(1, new byte[] {1, 2, 9}, 0, 3);
        store.closeRecordStore();
        RecordStore.resetOpenStores();

        RecordStore again = RecordStore.openRecordStore("game", false);
        assertEquals(1, again.getNumRecords());
        assertArrayEquals(new byte[] {1, 2, 9}, again.getRecord(1));
        again.closeRecordStore();
    }
}

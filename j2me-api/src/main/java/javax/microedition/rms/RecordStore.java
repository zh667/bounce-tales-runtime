package javax.microedition.rms;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * File-backed RMS. Directory from {@code bounce.save.dir} or user home.
 */
public class RecordStore {
    private static final Map<String, RecordStore> OPEN = new HashMap<>();

    private final String name;
    private final List<byte[]> records = new ArrayList<>();

    private RecordStore(String name) {
        this.name = name;
    }

    public static synchronized RecordStore openRecordStore(String recordStoreName, boolean createIfNecessary)
            throws RecordStoreException {
        if (recordStoreName == null || recordStoreName.isBlank()) {
            throw new RecordStoreException("name");
        }
        RecordStore existing = OPEN.get(recordStoreName);
        if (existing != null) {
            return existing;
        }
        RecordStore store = new RecordStore(recordStoreName);
        store.readFile();
        if (store.records.isEmpty() && !createIfNecessary) {
            throw new RecordStoreException("missing " + recordStoreName);
        }
        OPEN.put(recordStoreName, store);
        return store;
    }

    public synchronized int getNumRecords() {
        return records.size();
    }

    public synchronized int addRecord(byte[] data, int offset, int numBytes) throws RecordStoreException {
        byte[] copy = data == null ? new byte[0] : java.util.Arrays.copyOfRange(data, offset, offset + numBytes);
        records.add(copy);
        writeFile();
        return records.size();
    }

    public synchronized void setRecord(int recordId, byte[] data, int offset, int numBytes) throws RecordStoreException {
        if (recordId < 1 || recordId > records.size()) {
            throw new RecordStoreException("id");
        }
        byte[] copy = data == null ? new byte[0] : java.util.Arrays.copyOfRange(data, offset, offset + numBytes);
        records.set(recordId - 1, copy);
        writeFile();
    }

    public synchronized byte[] getRecord(int recordId) throws RecordStoreException {
        if (recordId < 1 || recordId > records.size()) {
            throw new RecordStoreException("id");
        }
        byte[] src = records.get(recordId - 1);
        return java.util.Arrays.copyOf(src, src.length);
    }

    public synchronized void closeRecordStore() {
        OPEN.remove(name);
    }

    static synchronized void resetOpenStores() {
        OPEN.clear();
    }

    private Path file() {
        String dir = System.getProperty("bounce.save.dir");
        Path root = (dir == null || dir.isBlank())
                ? Path.of(System.getProperty("user.home"), ".bounce-tales-runtime", "saves")
                : Path.of(dir);
        String safe = name.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_-]", "_");
        return root.resolve("rms-" + safe + ".bin");
    }

    private void readFile() {
        Path path = file();
        if (!Files.isRegularFile(path)) {
            return;
        }
        try (DataInputStream in = new DataInputStream(Files.newInputStream(path))) {
            int count = in.readInt();
            records.clear();
            for (int i = 0; i < count; i++) {
                int len = in.readInt();
                records.add(in.readNBytes(len));
            }
        } catch (IOException ignored) {
            records.clear();
        }
    }

    private void writeFile() throws RecordStoreException {
        Path path = file();
        try {
            Files.createDirectories(path.getParent());
            try (DataOutputStream out = new DataOutputStream(Files.newOutputStream(path))) {
                out.writeInt(records.size());
                for (byte[] rec : records) {
                    out.writeInt(rec.length);
                    out.write(rec);
                }
            }
        } catch (IOException ex) {
            throw new RecordStoreException(ex.getMessage());
        }
    }
}

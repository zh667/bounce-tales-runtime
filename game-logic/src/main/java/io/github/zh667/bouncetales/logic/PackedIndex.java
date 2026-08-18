package io.github.zh667.bouncetales.logic;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Packed resource index (JAR entry {@code a}): file table, batches, resident headers.
 * Format is reconstructed from observed layout; tests use synthetic blobs only.
 */
public final class PackedIndex {
    public record FileRef(String path, int skipOffset, int readLength) {
        public FileRef {
            Objects.requireNonNull(path, "path");
        }

        public boolean exists() {
            return readLength != 0;
        }
    }

    public record Batch(PackedKind kind, int typeCode, short mainId, List<Short> subIds) {
        public Batch {
            Objects.requireNonNull(kind, "kind");
            subIds = List.copyOf(subIds);
        }
    }

    public record Resident(short type, short resourceId) {}

    private final List<FileRef> files;
    private final List<Batch> batches;
    private final List<Resident> residents;

    private PackedIndex(List<FileRef> files, List<Batch> batches, List<Resident> residents) {
        this.files = List.copyOf(files);
        this.batches = List.copyOf(batches);
        this.residents = List.copyOf(residents);
    }

    public static PackedIndex empty() {
        return new PackedIndex(List.of(), List.of(), List.of());
    }

    public static PackedIndex tryParse(byte[] bytes) {
        try {
            return parse(bytes);
        } catch (IOException | RuntimeException ex) {
            return empty();
        }
    }

    public static PackedIndex parse(byte[] bytes) throws IOException {
        if (bytes == null || bytes.length < 6) {
            throw new IOException("packed index too short");
        }
        try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes))) {
            int fileCount = in.readUnsignedShort();
            List<FileRef> files = new ArrayList<>(fileCount);
            for (int i = 0; i < fileCount; i++) {
                String path = in.readUTF();
                int skip = in.readInt();
                int length = in.readInt();
                files.add(new FileRef(path, skip, length));
            }
            int batchCount = in.readUnsignedShort();
            List<Batch> batches = new ArrayList<>(batchCount);
            for (int i = 0; i < batchCount; i++) {
                int typeCode = in.readUnsignedByte();
                int subCount = in.readUnsignedByte();
                short mainId = in.readShort();
                List<Short> subIds = new ArrayList<>(subCount);
                for (int s = 0; s < subCount; s++) {
                    subIds.add(in.readShort());
                }
                batches.add(new Batch(PackedKind.fromCode(typeCode), typeCode, mainId, subIds));
            }
            int residentCount = in.readUnsignedShort();
            List<Resident> residents = new ArrayList<>(residentCount);
            for (int i = 0; i < residentCount; i++) {
                residents.add(new Resident(in.readShort(), in.readShort()));
            }
            return new PackedIndex(files, batches, residents);
        }
    }

    public List<FileRef> files() {
        return files;
    }

    public List<Batch> batches() {
        return batches;
    }

    public List<Resident> residents() {
        return residents;
    }

    public int fileCount() {
        return files.size();
    }

    public int batchCount() {
        return batches.size();
    }

    public long countKind(PackedKind kind) {
        PackedKind wanted = Objects.requireNonNull(kind, "kind");
        return batches.stream().filter(batch -> batch.kind() == wanted).count();
    }

    public String toLogLine() {
        return "packed: files="
                + files.size()
                + " batches="
                + batches.size()
                + " residents="
                + residents.size()
                + " images="
                + countKind(PackedKind.IMAGE)
                + " midi="
                + countKind(PackedKind.MIDI)
                + " levels="
                + countKind(PackedKind.LEVEL);
    }
}

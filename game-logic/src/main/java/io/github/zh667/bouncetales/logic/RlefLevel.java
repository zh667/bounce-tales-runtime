package io.github.zh667.bouncetales.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Parsed RLEF chapter. Geometry is world-space; tests use synthetic files only.
 */
public final class RlefLevel {
    public static final int SIGNATURE = 0x524C4546;
    public static final int VERSION = 0x10000;

    public record Vec2(int x, int y) {}

    public record Marker(RlefKind kind, int worldX, int worldY) {}

    public record Terrain(List<Vec2> vertices, int[] triangles, int rgb) {
        public Terrain {
            vertices = List.copyOf(vertices);
            triangles = triangles == null ? new int[0] : triangles.clone();
        }
    }

    private final int objectCount;
    private final int eventCount;
    private final List<Terrain> terrain;
    private final List<Marker> markers;
    private final Optional<Vec2> playerSpawn;
    private final int minX;
    private final int minY;
    private final int maxX;
    private final int maxY;

    private RlefLevel(
            int objectCount,
            int eventCount,
            List<Terrain> terrain,
            List<Marker> markers,
            Optional<Vec2> playerSpawn,
            int minX,
            int minY,
            int maxX,
            int maxY) {
        this.objectCount = objectCount;
        this.eventCount = eventCount;
        this.terrain = List.copyOf(terrain);
        this.markers = List.copyOf(markers);
        this.playerSpawn = Objects.requireNonNull(playerSpawn);
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public static RlefLevel empty() {
        return new RlefLevel(0, 0, List.of(), List.of(), Optional.empty(), 0, 0, 0, 0);
    }

    public static RlefLevel tryParse(byte[] bytes) {
        try {
            return parse(bytes);
        } catch (RuntimeException ex) {
            return empty();
        }
    }

    public static RlefLevel parse(byte[] bytes) {
        if (bytes == null || bytes.length < 15) {
            throw new IllegalArgumentException("rlef too short");
        }
        Cursor in = new Cursor(bytes);
        if (in.readInt() != SIGNATURE) {
            throw new IllegalArgumentException("not an RLEF file");
        }
        if (in.readInt() != VERSION) {
            throw new IllegalArgumentException("unsupported RLEF version");
        }
        int objectCount = in.readUnsignedShort();
        in.readUnsignedShort();
        int eventCount = in.readUnsignedShort();
        List<LocalObject> locals = new ArrayList<>();
        while (in.hasByte()) {
            int type = in.readUnsignedByte();
            if (type == RlefKind.END) {
                break;
            }
            int size = in.readUnsignedShort();
            byte[] payload = in.readBytes(size);
            locals.add(readObject(locals.size(), type, payload));
        }
        int[] worldX = new int[locals.size()];
        int[] worldY = new int[locals.size()];
        for (int i = 0; i < locals.size(); i++) {
            LocalObject obj = locals.get(i);
            if (obj.parentId < 0 || obj.parentId >= i) {
                worldX[i] = obj.x;
                worldY[i] = obj.y;
            } else {
                worldX[i] = worldX[obj.parentId] + obj.x;
                worldY[i] = worldY[obj.parentId] + obj.y;
            }
        }
        List<Terrain> terrain = new ArrayList<>();
        List<Marker> markers = new ArrayList<>();
        Optional<Vec2> spawn = Optional.empty();
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (int i = 0; i < locals.size(); i++) {
            LocalObject obj = locals.get(i);
            int ox = worldX[i];
            int oy = worldY[i];
            if (obj.kind == RlefKind.GEOMETRY && obj.localVerts != null) {
                List<Vec2> world = new ArrayList<>(obj.localVerts.length);
                for (int v = 0; v < obj.localVerts.length; v++) {
                    int wx = obj.localVerts[v].x + ox;
                    int wy = obj.localVerts[v].y + oy;
                    world.add(new Vec2(wx, wy));
                    minX = Math.min(minX, wx);
                    minY = Math.min(minY, wy);
                    maxX = Math.max(maxX, wx);
                    maxY = Math.max(maxY, wy);
                }
                terrain.add(new Terrain(world, obj.triangles, obj.rgb));
            } else if (obj.kind == RlefKind.PLAYER) {
                spawn = Optional.of(new Vec2(ox, oy));
            } else if (obj.kind == RlefKind.EGG
                    || obj.kind == RlefKind.ENEMY
                    || obj.kind == RlefKind.TRAMPOLINE
                    || obj.kind == RlefKind.CANNON
                    || obj.kind == RlefKind.WATER) {
                markers.add(new Marker(obj.kind, ox, oy));
            }
        }
        if (terrain.isEmpty()) {
            minX = minY = maxX = maxY = 0;
        }
        return new RlefLevel(objectCount, eventCount, terrain, markers, spawn, minX, minY, maxX, maxY);
    }

    public int objectCount() {
        return objectCount;
    }

    public int eventCount() {
        return eventCount;
    }

    public List<Terrain> terrain() {
        return terrain;
    }

    public List<Marker> markers() {
        return markers;
    }

    public Optional<Vec2> playerSpawn() {
        return playerSpawn;
    }

    public int minX() {
        return minX;
    }

    public int minY() {
        return minY;
    }

    public int maxX() {
        return maxX;
    }

    public int maxY() {
        return maxY;
    }

    public boolean playable() {
        return playerSpawn.isPresent() && !terrain.isEmpty();
    }

    public String toLogLine() {
        return "chapter: objects="
                + objectCount
                + " events="
                + eventCount
                + " geoms="
                + terrain.size()
                + " markers="
                + markers.size()
                + " player="
                + playerSpawn.isPresent();
    }

    private static LocalObject readObject(int id, int type, byte[] payload) {
        Cursor in = new Cursor(payload);
        int parentId = in.readShort();
        in.readShort();
        int transformFlags = in.readUnsignedByte();
        int x = 0;
        int y = 0;
        if ((transformFlags & 7) == 7) {
            in.skip(8);
            x = in.readInt() >> 16;
            in.skip(8);
            y = in.readInt() >> 16;
        } else {
            if ((transformFlags & 1) != 0) {
                x = in.readShort();
                y = in.readShort();
            }
            if ((transformFlags & 2) != 0) {
                in.skip(4);
            }
            if ((transformFlags & 4) != 0) {
                in.skip(8);
            }
        }
        in.readInt();
        RlefKind kind = RlefKind.fromCode(type);
        LocalObject obj = new LocalObject(id, kind, parentId, x, y);
        if (kind == RlefKind.GEOMETRY) {
            try {
                parseGeometry(in, obj);
            } catch (RuntimeException ignored) {
                obj.localVerts = null;
            }
        }
        return obj;
    }

    private static void parseGeometry(Cursor in, LocalObject obj) {
        int vertexCount = in.readShort();
        int faceCount = in.readShort();
        obj.rgb = in.readInt();
        int coordBits = in.readUnsignedByte();
        int xBase = in.readShort();
        BitPack.Result xs = BitPack.readSigned(in.data, in.pos, vertexCount, coordBits, xBase);
        in.pos = xs.nextOffset();
        int yBase = in.readShort();
        BitPack.Result ys = BitPack.readSigned(in.data, in.pos, vertexCount, coordBits, yBase);
        in.pos = ys.nextOffset();
        obj.localVerts = new Vec2[vertexCount];
        for (int i = 0; i < vertexCount; i++) {
            obj.localVerts[i] = new Vec2(xs.values()[i], ys.values()[i]);
        }
        int indexBits = in.readUnsignedByte();
        BitPack.Result faces = BitPack.readSigned(in.data, in.pos, faceCount, indexBits, 0);
        in.pos = faces.nextOffset();
        obj.triangles = faces.values();
    }

    private static final class LocalObject {
        final int id;
        final RlefKind kind;
        final int parentId;
        final int x;
        final int y;
        Vec2[] localVerts;
        int[] triangles;
        int rgb;

        LocalObject(int id, RlefKind kind, int parentId, int x, int y) {
            this.id = id;
            this.kind = kind;
            this.parentId = parentId;
            this.x = x;
            this.y = y;
        }
    }

    private static final class Cursor {
        final byte[] data;
        int pos;

        Cursor(byte[] data) {
            this.data = data;
        }

        boolean hasByte() {
            return pos < data.length;
        }

        void skip(int n) {
            pos += n;
        }

        int readUnsignedByte() {
            return data[pos++] & 0xFF;
        }

        int readShort() {
            int v = (short) ((data[pos] << 8) | (data[pos + 1] & 0xFF));
            pos += 2;
            return v;
        }

        int readUnsignedShort() {
            int v = ((data[pos] & 0xFF) << 8) | (data[pos + 1] & 0xFF);
            pos += 2;
            return v;
        }

        int readInt() {
            int v = (data[pos] << 24)
                    | ((data[pos + 1] & 0xFF) << 16)
                    | ((data[pos + 2] & 0xFF) << 8)
                    | (data[pos + 3] & 0xFF);
            pos += 4;
            return v;
        }

        byte[] readBytes(int n) {
            if (n < 0 || pos + n > data.length) {
                throw new IllegalArgumentException("rlef object truncated");
            }
            byte[] slice = Arrays.copyOfRange(data, pos, pos + n);
            pos += n;
            return slice;
        }
    }
}

package outskirts.world.chunk;

import outskirts.util.vector.Vector3f;
import outskirts.util.vector.Vector3i;

public final class ChunkPos {

    public final int x;
    public final int z;

    public ChunkPos(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public static long asLong(int x, int z) {
        return (x & 0xFFFFFFFFL) << 32 | (z & 0xFFFFFFFFL);
    }

    public static long asLong(ChunkPos chunkPos) {
        return asLong(chunkPos.x, chunkPos.z);
    }

    public static ChunkPos of(long posLong) {
        return new ChunkPos((int)(posLong >> 32), (int)posLong);
    }

    public static ChunkPos of(Chunk chunk) {
        return new ChunkPos(chunk.x, chunk.z);
    }

    public static ChunkPos of(Vector3i blockPos) {
        return new ChunkPos(blockPos.x >> 4, blockPos.z >> 4);
    }

    public static ChunkPos of(Vector3f position) {
        return new ChunkPos((int)position.x >> 4, (int)position.z >> 4);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ChunkPos && ((ChunkPos) obj).x == x && ((ChunkPos) obj).z == z;
    }

    //TODO: what is this use for...
    @Override
    public int hashCode() {
        return x ^ z;
    }

    @Override
    public String toString() {
        return "[" + x + ", " + z + "]";
    }
}

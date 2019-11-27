package outskirts.world.chunk;

import outskirts.block.state.BlockState;
import outskirts.util.CollectionUtils;
import outskirts.util.Maths;
import outskirts.world.World;

public class Chunk {

    public static final int SIZE = 16;
    public static final int SIZE_Y = SIZE * 16;

    public Octree[] storageArray = new Octree[16];

    public final int x;
    public final int z;

    private World world;

    public Chunk(int x, int z, World world) {
        this.x = x;
        this.z = z;
        this.world = world;

        CollectionUtils.fill(storageArray, Octree::new);
    }

    public void setBlockState(float x, float y, float z, int depth, BlockState blockState) {

        getOctree(x, y, z, depth, Octree.GettingType.CREATING).body(blockState);
    }

    public BlockState getBlockState(float x, float y, float z, int depth, Octree.GettingType type) {
        if (y >= SIZE_Y || y < 0) //this can avoid a lots of checking
            return null;

        Octree octree = getOctree(x, y, z, depth, type);

        if (octree == null)
            return null;

        return octree.body();
    }

    private Octree getOctree(float x, float y, float z, int depth, Octree.GettingType type) {
        return Octree.findChild(
                Maths.mod(x, 16f) / 16f,
                Maths.mod(y, 16f) / 16f,
                Maths.mod(z, 16f) / 16f, depth, storageArray[Maths.floor(y / 16)], type);
    }

    public World getWorld() {
        return world;
    }
}

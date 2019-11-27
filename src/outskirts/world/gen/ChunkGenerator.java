package outskirts.world.gen;

import outskirts.block.state.BlockState;
import outskirts.init.Blocks;
import outskirts.util.logging.Log;
import outskirts.world.World;
import outskirts.world.chunk.Chunk;

public class ChunkGenerator {

    private NoiseGenerator noise = new NoiseGenerator();

    public Chunk generate(int chunkX, int chunkZ, World world) {
        Chunk chunk = new Chunk(chunkX, chunkZ, world);

        int startX = chunkX << 4;
        int startZ = chunkZ << 4;

        for (int x = startX;x < startX + 16;x++) {
            for (int y = 0;y < 64;y++) {
                for (int z = startZ;z < startZ + 16;z++) {

                    int block = (int) (noise.octavesNoise(x/15f, z/15f, 3) * 10);

//                    Log.info(block);
//                    if (y <= block)
//                        chunk.setBlockState(x, y, z, 4, Blocks.DIRT.getDefaultState());
                }
            }
        }

        return chunk;
    }

}

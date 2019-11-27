package outskirts.block;

import outskirts.client.render.chunk.ChunkModelGenerator;
import outskirts.init.BlockTextures;
import outskirts.util.vector.Vector3f;
import outskirts.world.World;
import outskirts.world.chunk.Octree;

import java.util.List;

public class BlockBrick extends Block {

    public BlockBrick() {
        setRegistryID("brick");
    }

    @Override
    public void getVertexData(World world, Vector3f blockPos, Octree octree, List<Float> positions, List<Float> textureCoords, List<Float> normals) {

        ChunkModelGenerator.putCube(
                world,
                positions, textureCoords, normals,
                blockPos.x, blockPos.y, blockPos.z,
                octree.depth(),
                octree.size() * 16f,
                BlockTextures.BRICK.OFFSET,
                BlockTextures.BRICK.SCALE
        );
    }
}

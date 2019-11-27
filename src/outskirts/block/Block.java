package outskirts.block;

import outskirts.block.state.BlockState;
import outskirts.block.state.BlockStateList;
import outskirts.client.material.TextureAtlas;
import outskirts.client.render.chunk.ChunkModelGenerator;
import outskirts.client.render.chunk.ChunkModelGeneratorCube;
import outskirts.client.render.chunk.ChunkModelGeneratorMC;
import outskirts.client.render.renderer.GuiRenderer;
import outskirts.util.Side;
import outskirts.util.SideOnly;
import outskirts.util.registry.Registrable;
import outskirts.util.registry.Registry;
import outskirts.util.vector.Vector3f;
import outskirts.world.World;
import outskirts.world.chunk.Octree;

import java.util.List;

public class Block implements Registrable {

    public static final Registry<Block> REGISTRY = new Registry.RegistrableRegistry<>();

    private String registryID;

    private BlockStateList blockStateList;

    private BlockState defaultBlockState;

    public Block() {
        this.blockStateList = createBlocksStateList();
        setDefaultState(blockStateList.getBaseState());
    }

    @SideOnly(Side.CLIENT)
    public void getVertexData(World world, Vector3f blockPos, Octree octree, List<Float> positions, List<Float> textureCoords, List<Float> normals) {

//        ChunkModelGenerator.putCube(
//                world,
//                positions, textureCoords, normals,
//                blockPos.x, blockPos.y, blockPos.z,
//                octree.depth(),
//                octree.size() * 16f,
//                GuiRenderer.DATA_RECT_TEXTURECOORDS
//        );
    }

    private BlockStateList createBlocksStateList() {
        return new BlockStateList(this);
    }

    public BlockState getDefaultState() {
        return defaultBlockState;
    }

    private void setDefaultState(BlockState defaultBlockState) {
        this.defaultBlockState = defaultBlockState;
    }

    @Override
    public String getRegistryID() {
        return registryID;
    }
}






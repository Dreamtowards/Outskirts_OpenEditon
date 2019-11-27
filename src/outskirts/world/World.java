package outskirts.world;

import outskirts.block.state.BlockState;
import outskirts.client.GameSettings;
import outskirts.entity.Entity;
import outskirts.event.Events;
import outskirts.event.world.block.BlockChangedEvent;
import outskirts.event.world.chunk.ChunkLoadedEvent;
import outskirts.physics.dynamic.DynamicsWorld;
import outskirts.util.GameTimer;
import outskirts.util.Maths;
import outskirts.util.Tickable;
import outskirts.util.Validate;
import outskirts.util.vector.Vector4f;
import outskirts.world.chunk.Chunk;
import outskirts.world.chunk.ChunkPos;
import outskirts.world.chunk.Octree;
import outskirts.world.gen.ChunkGenerator;

import javax.annotation.Nullable;
import java.util.*;

public class World implements Tickable {

    private List<Entity> entities = new ArrayList<>();

    private Map<Long, Chunk> chunks = new HashMap<>();

    private ChunkGenerator chunkGenerator = new ChunkGenerator();

    private DynamicsWorld dynamicsWorld = new DynamicsWorld();


    public List<Entity> getEntities() {
        return Collections.unmodifiableList(entities);
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
        dynamicsWorld.addCollisionObject(entity.getRigidBody()); //TODO: really?
    }




    @Nullable
    public BlockState getBlockState(float x, float y, float z, int depth, Octree.GettingType type) {
        Chunk chunk = getLoadedChunk(Maths.floor(x / 16f), Maths.floor(z / 16f));

        if (chunk == null)
            return null;

        return chunk.getBlockState(x, y, z, depth, type);
    }

    public void setBlockState(float x, float y, float z, int depth, BlockState blockState) {
        Chunk chunk = getLoadedChunk(Maths.floor(x / 16f), Maths.floor(z / 16f));
        Validate.notNull(chunk, "The chunk has not been loaded yet.");

        chunk.setBlockState(x, y, z, depth, blockState);

        Events.EVENT_BUS.post(new BlockChangedEvent(this, new Vector4f(x, y, z, depth)));
    }
    public final void setBlockState(Vector4f blockPos, BlockState blockState) {
        Validate.isTrue(Maths.frac(blockPos.w) == 0, "blockPos.w depth must be integer number");
        setBlockState(blockPos.x, blockPos.y, blockPos.z, (int)blockPos.w, blockState);
    }


    @Override
    public void onTick() {


        dynamicsWorld.stepSimulation(1f / GameTimer.TPS);
    }

    public Chunk provideChunk(int chunkX, int chunkZ) {
        Chunk chunk = getLoadedChunk(chunkX, chunkZ);
        if (chunk == null) {
            //load or gen

            chunk = chunkGenerator.generate(chunkX, chunkZ, this);

            chunks.put(ChunkPos.asLong(chunkX, chunkZ), chunk);

            Events.EVENT_BUS.post(new ChunkLoadedEvent(chunk));
        }
        return chunk;
    }

    @Nullable
    public Chunk getLoadedChunk(int chunkX, int chunkZ) {
        return chunks.get(ChunkPos.asLong(chunkX, chunkZ));
    }
}

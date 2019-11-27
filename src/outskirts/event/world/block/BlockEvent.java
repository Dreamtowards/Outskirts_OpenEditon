package outskirts.event.world.block;

import outskirts.event.world.WorldEvent;
import outskirts.util.vector.Vector3i;
import outskirts.util.vector.Vector4f;
import outskirts.world.World;

public abstract class BlockEvent extends WorldEvent {

    private Vector4f blockPos;

    public BlockEvent(World world, Vector4f blockPos) {
        super(world);
        this.blockPos = blockPos;
    }

    public Vector4f getBlockPos() {
        return blockPos;
    }
}

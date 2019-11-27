package outskirts.block.state;

import outskirts.block.Block;

public class BlockStateList {

    private Block block;

    public BlockStateList(Block block) {
        this.block = block;
    }

    public BlockState getBaseState() {
        return new BlockState(block);
    }

}

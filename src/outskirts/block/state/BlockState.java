package outskirts.block.state;

import outskirts.block.Block;

public class BlockState {

    private Block block;

    BlockState(Block block) {
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }
}

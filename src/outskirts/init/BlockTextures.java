package outskirts.init;

import outskirts.block.Block;
import outskirts.client.material.TextureAtlas;
import outskirts.client.render.renderer.GuiRenderer;
import outskirts.util.ResourceLocation;
import outskirts.util.Side;
import outskirts.util.SideOnly;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

@SideOnly(Side.CLIENT)
public class BlockTextures {

    public static final TextureAtlas TEXTURE_ATLAS = new TextureAtlas();

    public static final TextureAtlas.AtlasFragment DIRT = register("textures/blocks/dirt.png");

    public static final TextureAtlas.AtlasFragment BRICK = register("textures/blocks/brick.png");



    static void initAndBuild() {
        TEXTURE_ATLAS.buildAtlas();
    }

    private static TextureAtlas.AtlasFragment register(String resourceID) {
        try {
            BufferedImage bufferedImage = ImageIO.read(new ResourceLocation(resourceID).getInputStream());
            return TEXTURE_ATLAS.register(bufferedImage);
        } catch (IOException ex) {
            throw new RuntimeException("failed to init BlockTextures res: " + resourceID);
        }
    }

}

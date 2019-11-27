package outskirts.entity;

import outskirts.client.Loader;
import outskirts.client.material.Model;
import outskirts.client.material.Texture;
import outskirts.util.ResourceLocation;

public class EntityStall extends Entity {

    private static final Model MODEL = Loader.loadOBJ(new ResourceLocation("outskirts:models/tmpobj.obj").getInputStream());
    private static final Texture TEXTURE = Loader.loadTexture(new ResourceLocation("textures/blocks/brick.png").getInputStream());

    public EntityStall() {
        getMaterial()
                .setModel(MODEL)
                .setTexture(TEXTURE);

    }
}

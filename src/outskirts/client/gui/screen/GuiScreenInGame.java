package outskirts.client.gui.screen;

import outskirts.client.Outskirts;
import outskirts.client.gui.GuiScreen;
import outskirts.client.render.renderer.GuiRenderer;
import outskirts.client.render.renderer.ModelRenderer;
import outskirts.util.Colors;
import outskirts.util.FileUtils;
import outskirts.util.Maths;
import outskirts.util.logging.Log;
import outskirts.util.vector.Vector3f;
import outskirts.util.vector.Vector4f;
import outskirts.world.gen.NoiseGenerator;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;

public class GuiScreenInGame extends GuiScreen {

    private GuiScreenDebug screenDebug = addGui(new GuiScreenDebug());

    @Override
    public void onDraw() {


        super.onDraw();
    }

    public GuiScreenDebug getGuiScreenDebug() {
        return screenDebug;
    }
}

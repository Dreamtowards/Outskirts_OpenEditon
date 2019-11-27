package outskirts.client.render.renderer;

import outskirts.client.Loader;
import outskirts.client.Outskirts;
import outskirts.client.material.Model;
import outskirts.client.material.Texture;
import outskirts.client.render.shader.ShaderProgram;
import outskirts.util.Colors;
import outskirts.util.Maths;
import outskirts.util.ResourceLocation;
import outskirts.util.vector.Vector2f;
import outskirts.util.vector.Vector3f;
import outskirts.util.vector.Vector4f;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class GuiRenderer extends Renderer {

    private ShaderProgram shader = new ShaderProgram(
            new ResourceLocation("shaders/gui.vsh").getInputStream(),
            new ResourceLocation("shaders/gui.fsh").getInputStream()
    );

    private static final Vector2f TMP_NDC_TRANS = new Vector2f();

    public void render(Model model, Texture texture, int x, int y, int width, int height, Vector4f colorMultiply) {

        shader.useProgram();

        shader.setVector2f("offset", Maths.calculateNormalDeviceCoords(x, y, Outskirts.getWidth(), Outskirts.getHeight(), TMP_NDC_TRANS));
        shader.setVector2f("scale", (float)width / Outskirts.getWidth(), (float)height / Outskirts.getHeight());

        shader.setVector4f("colorMultiply", colorMultiply);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture.textureID());

        glBindVertexArray(model.vaoID());

        drawElementsOrArrays(model);

        glBindVertexArray(0);
    }

    public void render(Model model, Texture texture, int x, int y, int width, int height) {
        render(model, texture, x, y, width, height, Colors.WHITE);
    }


    @Override
    public ShaderProgram getShader() {
        return shader;
    }




    public static final Texture TEXTURE_WHITE = createColoredTexture(1, 1, 1, 1);

    public static final float[] DATA_RECT_TEXTURECOORDS = new float[] {
            0, 1,
            1, 1,
            1, 0,
            1, 0,
            0, 0,
            0, 1
    };

    //a full square rectangle start from NDC center to right-bottom
    public static final Model MODEL_RECT = Loader.loadModel(null, new float[]{
            0,  0, 0,
            2,  0, 0,
            2, -2, 0,
            2, -2, 0,
            0, -2, 0,
            0,  0, 0
    }, DATA_RECT_TEXTURECOORDS, new float[]{});

    private static Texture createColoredTexture(float r, float g, float b, float a) {
        BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Arrays.fill(((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData(), new Color((int) (r * 255f), (int) (g * 255f), (int) (b * 255f), (int) (a * 255f)).getRGB());
        return Loader.loadTexture(bufferedImage);
    }
}

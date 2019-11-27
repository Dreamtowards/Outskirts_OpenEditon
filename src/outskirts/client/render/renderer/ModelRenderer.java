package outskirts.client.render.renderer;

import org.lwjgl.BufferUtils;
import outskirts.client.Loader;
import outskirts.client.Outskirts;
import outskirts.client.material.Model;
import outskirts.client.material.Texture;
import outskirts.client.render.shader.ShaderProgram;
import outskirts.util.Colors;
import outskirts.util.IOUtils;
import outskirts.util.Maths;
import outskirts.util.ResourceLocation;
import outskirts.util.vector.Matrix4f;
import outskirts.util.vector.Vector3f;
import outskirts.util.vector.Vector4f;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class ModelRenderer extends Renderer {

    private ShaderProgram shader = new ShaderProgram(
            new ResourceLocation("shaders/model.vsh").getInputStream(),
            new ResourceLocation("shaders/model.fsh").getInputStream()
    );

    public void render(Model model, Texture texture, Vector3f position, Vector3f scale, Vector3f rotation, Vector4f colorMultiply, boolean viewMatrix, boolean projectionMatrix, int mode) {

        shader.useProgram();

        shader.setVector4f("colorMultiply", colorMultiply);

        shader.setMatrix4f("viewMatrix", viewMatrix ? Outskirts.renderEngine.getViewMatrix() : Matrix4f.IDENTITY);
        shader.setMatrix4f("projectionMatrix", projectionMatrix ? Outskirts.renderEngine.getProjectionMatrix() : Matrix4f.IDENTITY);

        shader.setMatrix4f("modelMatrix", Maths.createModelMatrix(position, scale, rotation));

        glBindVertexArray(model.vaoID());

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture.textureID());

        drawElementsOrArrays(model, mode);

        glBindVertexArray(0);
    }


    public void drawOutline(Vector3f position, Vector3f size) {
        render(
                MODEL_CUBE_EDGE,
                GuiRenderer.TEXTURE_WHITE,
                position,
                size, //the MODEL_CUBE_EDGE is oriented by origin, so scale==size
                Vector3f.ZERO,
                Colors.WHITE,
                true, true, GL_LINES
        );
    }

    private FloatBuffer TMP_LINE_POS_TRANS = BufferUtils.createFloatBuffer(6);

    public void drawLine(Vector3f pos1, Vector3f pos2, Vector4f color) {

        IOUtils.fillBuffer(TMP_LINE_POS_TRANS, pos1.x, pos1.y, pos1.z, pos2.x, pos2.y, pos2.z);

        glBindVertexArray(MODEL_LINE.vaoID());

        glBindBuffer(GL_ARRAY_BUFFER, MODEL_LINE.vboID()[0]);
        glBufferData(GL_ARRAY_BUFFER, TMP_LINE_POS_TRANS, GL_STATIC_DRAW);

        render(
                MODEL_LINE,
                GuiRenderer.TEXTURE_WHITE,
                Vector3f.ZERO,
                Vector3f.ONE,
                Vector3f.ZERO,
                color,
                true, true, GL_LINES
        );
    }

    @Override
    public ShaderProgram getShader() {
        return shader;
    }



    public static final Vector3f[] DATAM_CUBE_FACES_DIRECTIONS = {
            new Vector3f(-1, 0, 0),
            new Vector3f( 1, 0, 0),
            new Vector3f( 0,-1, 0),
            new Vector3f( 0, 1, 0),
            new Vector3f( 0, 0,-1),
            new Vector3f( 0, 0, 1)
    };
    /*  0,5    1
     *  +------+
     *  | \    |
     *  |    \ |
     *  +------+
     *  4      2,3
     */
    public static final float[] DATA_CUBE_POSITIONS = new float[]{
            0.0f, 1.0f, 0.0f, // -X  Face-To-Axis-Surfaces
            0.0f, 1.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            1.0f, 1.0f, 1.0f, // +X
            1.0f, 1.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            0.0f, 0.0f, 1.0f, // -Y
            1.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 1.0f, 0.0f, // +Y
            1.0f, 1.0f, 0.0f,
            1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            0.0f, 1.0f, 1.0f,
            0.0f, 1.0f, 0.0f,
            1.0f, 1.0f, 0.0f, // -Z
            0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 1.0f, // +Z
            1.0f, 1.0f, 1.0f,
            1.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 1.0f, 1.0f,
    };
    public static final float[] DATA_CUBE_TEXTURECOORDS = new float[]{
            0, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 1, // -X
            0, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 1, // +X
            0, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 1, // -Y
            0, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 1, // +Y
            0, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 1, // -Z
            0, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 1, // +Z
    };
    public static final float[] DATA_CUBE_NORMALS = new float[]{
            -1.0f, 0.0f, 0.0f, // -X  Face-To-Axis-Surfaces
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,
             1.0f, 0.0f, 0.0f, // +X
             1.0f, 0.0f, 0.0f,
             1.0f, 0.0f, 0.0f,
             1.0f, 0.0f, 0.0f,
             1.0f, 0.0f, 0.0f,
             1.0f, 0.0f, 0.0f,
             0.0f,-1.0f, 0.0f, // -Y
             0.0f,-1.0f, 0.0f,
             0.0f,-1.0f, 0.0f,
             0.0f,-1.0f, 0.0f,
             0.0f,-1.0f, 0.0f,
             0.0f,-1.0f, 0.0f,
             0.0f, 1.0f, 0.0f, // +Y
             0.0f, 1.0f, 0.0f,
             0.0f, 1.0f, 0.0f,
             0.0f, 1.0f, 0.0f,
             0.0f, 1.0f, 0.0f,
             0.0f, 1.0f, 0.0f,
             0.0f, 0.0f,-1.0f, // -Z
             0.0f, 0.0f,-1.0f,
             0.0f, 0.0f,-1.0f,
             0.0f, 0.0f,-1.0f,
             0.0f, 0.0f,-1.0f,
             0.0f, 0.0f,-1.0f,
             0.0f, 0.0f, 1.0f, // +Z
             0.0f, 0.0f, 1.0f,
             0.0f, 0.0f, 1.0f,
             0.0f, 0.0f, 1.0f,
             0.0f, 0.0f, 1.0f,
             0.0f, 0.0f, 1.0f,
    };

    public static Model MODEL_CUBE = Loader.loadModel(null, DATA_CUBE_POSITIONS, DATA_CUBE_TEXTURECOORDS, DATA_CUBE_NORMALS);

    public static Model MODEL_CUBE_EDGE = Loader.loadModel(null, new float[] {
            0, 0, 0, 0, 0, 1, //left-bottom
            1, 0, 0, 1, 0, 1, //right-bottom
            0, 0, 0, 1, 0, 0, //front-bottom
            0, 0, 1, 1, 0, 1, //back-bottom

            0, 1, 0, 0, 1, 1, //left-top
            1, 1, 0, 1, 1, 1, //right-top
            0, 1, 0, 1, 1, 0, //front-top
            0, 1, 1, 1, 1, 1, //back-top

            0, 0, 0, 0, 1, 0, //front-left
            1, 0, 0, 1, 1, 0, //front-right
            0, 0, 1, 0, 1, 1, //back-left
            1, 0, 1, 1, 1, 1  //back-right
    }, new float[] {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
    }, new float[] {});

    //tmp model, positions be update before draw
    private static final Model MODEL_LINE = Loader.loadModel(null, new float[] {
            0, 0, 0,
            0, 0, 0
    }, new float[] {
            0, 0,
            0, 0
    }, new float[]{
            0, 0, 0, 0, 0, 0
    });
}

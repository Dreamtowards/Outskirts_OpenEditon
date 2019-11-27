package outskirts.client.render.renderer;

import outskirts.client.material.Model;
import outskirts.client.render.shader.ShaderProgram;

import static org.lwjgl.opengl.GL11.*;

/**
 * Inheritance always makes more complexity, but in this case,
 * Renderer just mark/collect/classification related renderer(s), this make Renderer system more Clear and Specification
 */
public abstract class Renderer {

    public abstract ShaderProgram getShader();








    // Utility methods

    protected static String[] createUniformNameArray(String uniformName, int length) {
        String[] nameArray = new String[length];
        for (int i = 0;i < length;i++) {
            nameArray[i] = uniformName + "[" + i + "]";
        }
        return nameArray;
    }

    protected static void drawElementsOrArrays(Model model, int mode) {
        if (model.eboID() == -1) {
            glDrawArrays(mode, 0, model.vertexCount());
        } else {
            glDrawElements(mode, model.vertexCount(), GL_UNSIGNED_INT, 0);
        }
    }

    protected static void drawElementsOrArrays(Model model) {
        drawElementsOrArrays(model, GL_TRIANGLES);
    }

}

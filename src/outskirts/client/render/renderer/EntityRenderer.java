package outskirts.client.render.renderer;

import outskirts.client.Outskirts;
import outskirts.client.material.Material;
import outskirts.client.material.Model;
import outskirts.client.render.shader.ShaderProgram;
import outskirts.entity.Entity;
import outskirts.util.Maths;
import outskirts.util.ResourceLocation;
import outskirts.util.logging.Log;
import outskirts.util.vector.Matrix4f;
import outskirts.util.vector.Vector3f;
import outskirts.util.vector.Vector4f;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class EntityRenderer extends Renderer {

    private ShaderProgram shader = new ShaderProgram(
            new ResourceLocation("shaders/entity.vsh").getInputStream(),
            new ResourceLocation("shaders/entity.fsh").getInputStream()
    );

    public EntityRenderer() {
        //init Texture-Units
        shader.useProgram();
        shader.setInt("textureSampler", 0);
    }

    public void render(List<Entity> entities) {

        shader.useProgram();

        shader.setMatrix4f("projectionMatrix", Outskirts.renderEngine.getProjectionMatrix());
        shader.setMatrix4f("viewMatrix", Outskirts.renderEngine.getViewMatrix());

        shader.setVector3f("light.color", new Vector3f(1, 1, 1));
        shader.setVector3f("light.position", Outskirts.getCamera().getPosition());

        for (Entity entity : entities) {
            Material material = entity.getMaterial();

            Model model = material.getModel();

            glBindVertexArray(model.vaoID());


            shader.setMatrix4f("modelMatrix", Maths.createModelMatrix(entity.getPosition(), Vector3f.ONE, entity.getRotation()));

            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, material.getTexture().textureID());

            drawElementsOrArrays(model);
        }

        glBindVertexArray(0);
    }

    @Override
    public ShaderProgram getShader() {
        return shader;
    }
}

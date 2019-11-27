package outskirts.client.render.renderer;

import outskirts.util.logging.Log;
import outskirts.util.vector.Matrix4f;
import outskirts.world.World;

import static org.lwjgl.opengl.GL11.*;

public final class RenderEngine {

    private Matrix4f projectionMatrix;
    private Matrix4f viewMatrix;

    private EntityRenderer entityRenderer = new EntityRenderer();
    private GuiRenderer guiRenderer = new GuiRenderer();
    private FontRenderer fontRenderer = new FontRenderer();
    private ModelRenderer modelRenderer = new ModelRenderer();
    private ChunkRenderer chunkRenderer = new ChunkRenderer();

    public RenderEngine() {
        Log.info("RenderEngine initialized. GL_I: %s | %s", glGetString(GL_VENDOR), glGetString(GL_VERSION));
    }

    public void prepare() {
        glEnable(GL_DEPTH_TEST);
        glClearColor(0, 0, 0.2f, 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    public void render(World world) {

        entityRenderer.render(world.getEntities());

        chunkRenderer.render();
    }


    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public void setViewMatrix(Matrix4f viewMatrix) {
        this.viewMatrix = viewMatrix;
    }

    public void setProjectionMatrix(Matrix4f projectionMatrix) {
        this.projectionMatrix = projectionMatrix;
    }



    public EntityRenderer getEntityRenderer() {
        return entityRenderer;
    }

    public GuiRenderer getGuiRenderer() {
        return guiRenderer;
    }

    public FontRenderer getFontRenderer() {
        return fontRenderer;
    }

    public ModelRenderer getModelRenderer() {
        return modelRenderer;
    }

    public ChunkRenderer getChunkRenderer() {
        return chunkRenderer;
    }
}

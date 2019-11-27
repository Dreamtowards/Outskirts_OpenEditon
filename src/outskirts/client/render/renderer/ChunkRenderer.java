package outskirts.client.render.renderer;

import outskirts.client.Outskirts;
import outskirts.client.material.Model;
import outskirts.client.render.chunk.ChunkModelBuildWorker;
import outskirts.client.render.chunk.RenderSection;
import outskirts.client.render.shader.ShaderProgram;
import outskirts.event.EventHandler;
import outskirts.event.Events;
import outskirts.event.world.block.BlockChangedEvent;
import outskirts.event.world.chunk.ChunkLoadedEvent;
import outskirts.init.BlockTextures;
import outskirts.util.logging.Log;
import outskirts.util.vector.Matrix4f;
import outskirts.util.vector.Vector3f;
import outskirts.util.vector.Vector3i;
import outskirts.util.vector.Vector4f;
import outskirts.world.chunk.Chunk;
import outskirts.world.chunk.ChunkPos;
import outskirts.world.chunk.Octree;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class ChunkRenderer extends Renderer {

    private List<RenderSection> renderSections = new ArrayList<>();

    private ChunkModelBuildWorker buildWorker = new ChunkModelBuildWorker();

    private ShaderProgram shader;

    public ChunkRenderer() {

        Thread thread = new Thread(buildWorker, "ChunkModelBuildWorker-Thread");
        thread.setDaemon(true);
        thread.start();

        Events.EVENT_BUS.register(this);
    }

    @EventHandler
    private void onChunkLoaded(ChunkLoadedEvent event) {
        ChunkPos chunkPos = ChunkPos.of(event.getChunk());
        for (int y = 0;y < Chunk.SIZE_Y;y += Chunk.SIZE) {
            RenderSection renderSection = new RenderSection(new Vector3i(chunkPos.x << 4, y, chunkPos.z << 4));
            renderSection.markNeedsUpdate();
            renderSections.add(renderSection);
        }
    }

    @EventHandler
    private void onBlockChanged(BlockChangedEvent event) {

        markSectionUpdate(RenderSection.getPosition(event.getBlockPos()));
    }

    private void markSectionUpdate(Vector3i sectionPosition) {
        for (RenderSection renderSection : renderSections) {
            if (renderSection.getPosition().equals(sectionPosition)) {
                renderSection.markNeedsUpdate();
                break;
            }
        }
    }

    private void updateRenderSectionsBuildTasks() {
        for (RenderSection renderSection : renderSections) {

            if (renderSection.needsUpdate()) {

                buildWorker.queueBuildTask(renderSection);

                renderSection.clearNeedsUpdate();
            }
        }
    }

    public void render() {

        updateRenderSectionsBuildTasks();

        shader = Outskirts.renderEngine.getEntityRenderer().getShader();

        shader.useProgram();
        shader.setMatrix4f("modelMatrix", Matrix4f.IDENTITY);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, BlockTextures.TEXTURE_ATLAS.getAtlasTexture().textureID());

        for (RenderSection renderSection : renderSections) {

            Model model = renderSection.getModel();

            if (model == null || renderSection.isEmpty())
                continue;

            glBindVertexArray(model.vaoID());

            drawElementsOrArrays(model);
        }

        glBindVertexArray(0);
    }

    @Override
    public ShaderProgram getShader() {
        return shader;
    }
}

package outskirts.client.gui.screen;

import outskirts.client.GameSettings;
import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiScreen;
import outskirts.client.gui.GuiText;
import outskirts.client.render.renderer.GuiRenderer;
import outskirts.client.render.renderer.ModelRenderer;
import outskirts.util.Colors;
import outskirts.util.FileUtils;
import outskirts.util.RayPicker;
import outskirts.util.vector.Vector3f;
import outskirts.util.vector.Vector4f;
import outskirts.world.chunk.Octree;

import static org.lwjgl.opengl.GL11.*;

public class GuiScreenDebug extends GuiScreen {

    private GuiText text = addGui(new GuiText("TEXT")).setY(150);

    public GuiScreenDebug() {

        GuiText.updateTextBound(text);
        text.addOnMouseEnteredListener(e -> {
            e.getGui()
                    .attachTransform(0, 200, 5, TRANS_X, IG_LINEAR, -1)
                    .attachTransform(9, 200, 1, TRANS_Y, IG_POWER3, -1);
        });
    }

    @Override
    public void onDraw() {

        drawString(String.format("CameraPos: %s, Yaw: %s, Owner.Yaw: %s", Outskirts.getCamera().getPosition(), Outskirts.getCamera().getYaw(), Outskirts.getCamera().getOwnerEntity().getRotation().y), 0, 36, Colors.WHITE);

        long max = Runtime.getRuntime().maxMemory();
        long total = Runtime.getRuntime().totalMemory();
        long used = total - Runtime.getRuntime().freeMemory();

        drawString(String.format("U/T: %s / %s | JVM_Max: %s", FileUtils.toDisplaySize(used), FileUtils.toDisplaySize(total), FileUtils.toDisplaySize(max)), 0, 20, Colors.WHITE);


        drawString(String.format("P: %sms, s: %s", Outskirts.getDelta() * 1000f, 1f / Outskirts.getDelta()), 0, 52, Colors.WHITE);


        Gui.drawString("P_DEP: " + GameSettings.PICKER_DEPTH, 0, 100, Colors.WHITE);

        Gui.drawString("pLen: " + RayPicker._PICKER_LEN, 0, 100-16, Colors.WHITE);

        //render camera's chunk bound
//        if (Outskirts.getCamera().getPosition().y >= 0) {
//            Outskirts.renderEngine.getModelRenderer().drawOutline(
//                    Vector3f.unit(new Vector3f(Outskirts.getCamera().getPosition()), 16),
//                    new Vector3f(16, 16, 16)
//            );
//        }

        //block selection bound
        Vector4f blockPos = Outskirts.getRayPicker().getCurrentBlockPos();
        if (blockPos != null) {
            glEnable(GL_DEPTH_TEST);

            float size = Octree.SIZES[(int)blockPos.w] * 16f;
            Outskirts.renderEngine.getModelRenderer().drawOutline(
                    new Vector3f(blockPos.x, blockPos.y, blockPos.z),
                    new Vector3f(size, size, size)
            );

            glDisable(GL_DEPTH_TEST);
        }

        //renderCameraAxises
        float s = 0.005f;
        float l = 0.1f;
        renderCameraAxis(new Vector3f(l, s, s), Colors.UNIT_R);
        renderCameraAxis(new Vector3f(s, l, s), Colors.UNIT_G);
        renderCameraAxis(new Vector3f(s, s, l), Colors.UNIT_B);

        super.onDraw();
    }

    private static void renderCameraAxis(Vector3f scale, Vector4f color) {
        Outskirts.renderEngine.getModelRenderer().render(
                ModelRenderer.MODEL_CUBE,
                GuiRenderer.TEXTURE_WHITE,
                new Vector3f(0f, 0f, -1f),
                scale,
                Outskirts.getCamera().getRotation(),
                color,
                false,
                true,
                GL_TRIANGLES
        );
    }
}

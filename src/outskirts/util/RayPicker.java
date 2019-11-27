package outskirts.util;

import org.lwjgl.input.Keyboard;
import outskirts.client.GameSettings;
import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.util.logging.Log;
import outskirts.util.vector.Vector3f;
import outskirts.util.vector.Vector4f;
import outskirts.world.chunk.Octree;

import javax.annotation.Nullable;

// have not really implement now yet
public class RayPicker {

    public static float _PICKER_LEN = 2;

    private Vector4f currentBlockPos;

    public void update() {

        if (Outskirts.getDWheel() != 0) {
            GameSettings.PICKER_DEPTH += Math.signum(Outskirts.getDWheel());
            GameSettings.PICKER_DEPTH = Maths.clamp(GameSettings.PICKER_DEPTH, 0, 8-1);
        }

        Vector3f cameraDir = Maths.calculateEulerDirection(Outskirts.getCamera().getPitch(), Outskirts.getCamera().getYaw());

        float size = Octree.SIZES[GameSettings.PICKER_DEPTH] * 16;

        _PICKER_LEN = size * 4;

        Vector3f pickPos = Vector3f.unit(new Vector3f(Outskirts.getCamera().getPosition()).add(cameraDir.scale(_PICKER_LEN)), size);

        currentBlockPos = new Vector4f(pickPos.x, pickPos.y, pickPos.z, GameSettings.PICKER_DEPTH);
    }

    @Nullable
    public Vector4f getCurrentBlockPos() {
        return currentBlockPos;
    }
}

package outskirts.client.audio;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import outskirts.util.IOUtils;
import outskirts.util.logging.Log;

import java.nio.FloatBuffer;

import static org.lwjgl.openal.AL10.*;

public final class AudioEngine {

    public AudioEngine() {
        try
        {
            AL.create();

            Log.info("AudioEngine initialized. AL_I: %s / %s", alGetString(AL_VENDOR), alGetString(AL_VERSION));
        }
        catch (Throwable ex)
        {
            throw new RuntimeException("Failed to init OpenAL.", ex);
        }

        alDistanceModel(AL_INVERSE_DISTANCE_CLAMPED);
    }


    public void destroy() {
        AL.destroy();
    }

    public void setListenerPosition(float x, float y, float z) {
        alListener3f(AL_POSITION, x, y, z);
        alListener3f(AL_VELOCITY, 0, 0, 0);
    }

    //tmp trans buffer for ListenerOrientation
    private FloatBuffer TMP_ORI_BUF_TRANS = BufferUtils.createFloatBuffer(6);

    /**
     * always is pos.xyz, vec(0, 1, 0).xyz
     */
    public void setListenerOrientation(float atX, float atY, float atZ, float upX, float upY, float upZ) {

        IOUtils.fillBuffer(TMP_ORI_BUF_TRANS, atX, atY, atZ, upX, upY, upZ);

        alListener(AL_ORIENTATION, TMP_ORI_BUF_TRANS);
    }
}

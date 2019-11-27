package outskirts.util.ogg;

import outskirts.client.Loader;
import outskirts.util.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.lwjgl.openal.AL10.AL_FORMAT_MONO16;
import static org.lwjgl.openal.AL10.AL_FORMAT_STEREO16;

public class OggLoader {

    public static class OggData {
        public ByteBuffer data;
        public int frequency;
        public int format = -1;
    }

    public static OggData loadOGG(InputStream inputStream) throws IOException {
        OggInputStream oggInput = new OggInputStream(inputStream);

        OggData ogg = new OggData();

        if (oggInput.getChannels() == 1) {
            ogg.format = AL_FORMAT_MONO16;
        } else if (oggInput.getChannels() == 2) {
            ogg.format = AL_FORMAT_STEREO16;
        }

        ogg.frequency = oggInput.getRate();

        byte[] data = IOUtils.toByteArray(oggInput);
        ogg.data = Loader.loadBuffer(data);
        return ogg;
    }
}


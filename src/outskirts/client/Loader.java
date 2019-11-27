package outskirts.client;

import org.lwjgl.BufferUtils;
import outskirts.client.material.Model;
import outskirts.client.material.ModelData;
import outskirts.client.material.Texture;
import outskirts.util.obj.OBJFileLoader;
import outskirts.util.ogg.OggLoader;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.openal.AL10.alBufferData;
import static org.lwjgl.openal.AL10.alGenBuffers;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.glTexImage3D;
import static org.lwjgl.opengl.GL12.glTexSubImage3D;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class Loader {

    /**
     * Load VertexData to VAO
     * @param indices If not null, that will use EBO
     */
    public static Model loadModel(@Nullable int[] indices, float[] positions, float[] textureCoords, float[] normals) {
        int vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        int eboID = (indices == null) ? -1 : bindElementBuffer(indices);

        int[] vbos = new int[3];
        vbos[0] = storeDataToAttributeList(0, 3, positions);
        vbos[1] = storeDataToAttributeList(1, 2, textureCoords);
        vbos[2] = storeDataToAttributeList(2, 3, normals);

        glBindVertexArray(0);

        return new Model(vaoID, eboID, vbos, indices != null ? indices.length : positions.length / 3);
    }

    /**
     * Store IndicesData (Indices[]) to EBO
     */
    private static int bindElementBuffer(int[] indices) {
        int eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        IntBuffer indicesBuffer = loadBuffer(indices);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
        return eboID;
    }

    /**
     * Store VertexData (Vertices[]) to VBO, and setup AttributeList
     */
    private static int storeDataToAttributeList(int attributeNumber, int vertexSize, float[] data) {
        int vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);

        FloatBuffer dataBuffer = loadBuffer(data);
        glBufferData(GL_ARRAY_BUFFER, dataBuffer, GL_STATIC_DRAW);

        glVertexAttribPointer(attributeNumber, vertexSize, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(attributeNumber);
        return vboID;
    }

    public static Model loadOBJ(InputStream inputStream) {
        try {
            ModelData modelData = OBJFileLoader.loadOBJ(inputStream);
            return Loader.loadModel(modelData.indices, modelData.positions, modelData.textureCoords, modelData.normals);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to loadOBJ().", ex);
        }
    }

    public static int loadOGG(InputStream inputStream) {
        try {
            OggLoader.OggData oggData = OggLoader.loadOGG(inputStream);
            int bufferID = alGenBuffers();
            alBufferData(bufferID, oggData.format, oggData.data, oggData.frequency);
            return bufferID;
        } catch (IOException ex) {
            throw new RuntimeException("Failed to loadOGG().", ex);
        }
    }

    public static Texture loadTexture(InputStream inputStream) {
        try {
            return loadTexture(ImageIO.read(inputStream));
        } catch (IOException ex) {
            throw new RuntimeException("Failed loadTexture().", ex);
        }
    }

    public static Texture loadTexture(BufferedImage bufferedImage) {
        return loadTexture(null, bufferedImage);
    }

    public static Texture loadTexture(@Nullable Texture dest, BufferedImage bufferedImage) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        boolean create = (dest == null);

        if (create) {
            dest = new Texture(glGenTextures(), width, height);
        } else if (width > dest.getWidth() || height > dest.getHeight()) {
            throw new IllegalStateException("Failed to loadTexture(). Source-Image size(width/height) is bigger over dest-Texture's size, can't storage completely.");
        }

        bindAndInitializeTexture(GL_TEXTURE_2D, dest.textureID());

        ByteBuffer buffer = Loader.loadTextureData(bufferedImage, true);

        if (create) {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        } else {
            glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        }

        glGenerateMipmap(GL_TEXTURE_2D);

        return dest;
    }

    /**
     * TextureArray.length == srcImages.length + fromIndex
     */
    public static Texture loadTextureArray(@Nullable Texture dest, BufferedImage[] bufferedImagesArray, int fromIndex) {

        boolean create = (dest == null);

        int maxWidth = 0;
        int maxHeight = 0;

        for (BufferedImage bufferedImage : bufferedImagesArray) {
            maxWidth = Math.max(maxWidth, bufferedImage.getWidth());
            maxHeight = Math.max(maxHeight, bufferedImage.getHeight());
        }

        if (create) {
            int textureID = glGenTextures();
            dest = new Texture(textureID, maxWidth, maxHeight);
        } else if (maxWidth > dest.getWidth() || maxHeight > dest.getHeight()) {
            throw new IllegalStateException("Failed to loadTextureArray().  Source-Images max-size(maxWidth/maxHeight) is bigger over dest-TextureArray's size, can't storage completely.");
        }

        bindAndInitializeTexture(GL_TEXTURE_2D_ARRAY, dest.textureID());

        if (create) {
            glTexImage3D(GL_TEXTURE_2D_ARRAY, 0, GL_RGBA, maxWidth, maxHeight, bufferedImagesArray.length + fromIndex, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
        }

        for (int i = 0;i < bufferedImagesArray.length;i++) {

            BufferedImage bufferedImage = bufferedImagesArray[i];

            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();

            ByteBuffer pixels = Loader.loadTextureData(bufferedImage, true);

            int index = fromIndex + i;
            glTexSubImage3D(GL_TEXTURE_2D_ARRAY, 0, 0, 0, index, width, height, 1, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
        }

        glGenerateMipmap(GL_TEXTURE_2D_ARRAY);

        return dest;
    }

    private static void bindAndInitializeTexture(int target, int textureID) {

        glBindTexture(target, textureID);

        glTexParameteri(target, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(target, GL_TEXTURE_WRAP_T, GL_REPEAT);

        glTexParameteri(target, GL_TEXTURE_MAG_FILTER, GL_NEAREST); //GL_LINEAR, GL_NEAREST
        glTexParameteri(target, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

//        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_LOD_BIAS, 1f);
    }

    /**
     * load to Direct-Memory
     */
    public static FloatBuffer loadBuffer(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    public static IntBuffer loadBuffer(int[] data) {
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    public static ByteBuffer loadBuffer(byte[] data) {
        ByteBuffer buffer = BufferUtils.createByteBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    static ByteBuffer loadTextureData(BufferedImage bufferedImage, boolean flipY) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);

        for (int i = 0;i < height;i++) {
            int y = flipY ? height - i - 1 : i;

            for (int x = 0;x < width; x++) {
                int argb = bufferedImage.getRGB(x, y);
                buffer.put((byte) ((argb >> 16) & 0xFF));  //RED
                buffer.put((byte) ((argb >>  8) & 0xFF));  //GREEN
                buffer.put((byte) ((argb      ) & 0xFF));  //BLUE
                buffer.put((byte) ((argb >> 24) & 0xFF));  //ALPHA
            }
        }
        buffer.flip();

        return buffer;
    }
}

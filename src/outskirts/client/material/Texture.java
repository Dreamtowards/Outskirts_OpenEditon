package outskirts.client.material;

import static org.lwjgl.opengl.GL11.glDeleteTextures;

public class Texture {

    private int textureID;
    private int width;
    private int height;

    public Texture(int textureID, int width, int height) {
        this.textureID = textureID;
        this.width = width;
        this.height = height;
    }

    public int textureID() {
        return textureID;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }


    private void delete() {
        glDeleteTextures(textureID);
    }
}

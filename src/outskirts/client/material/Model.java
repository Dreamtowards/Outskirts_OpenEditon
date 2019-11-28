package outskirts.client.material;

import static org.lwjgl.opengl.ARBVertexArrayObject.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;

/**
 * uses a public instance field, though "faster" (both exec speed and write/call speed), "convenience"
 * but its given people a Casual feel.. like a tmp design, and feels unsafe cause its some way is a public data.
 *
 * Method given people more Trust && StrongPower than public Field in subconscious
 * those getter not "get" prefix cause the prefix have some turn down the mainly feel.
 */
public class Model {

    private int vaoID;

    /** If not EBO, (use GL_ARRAY_BUFFER) that eboID will be -1 */
    private int eboID;

    private int[] vboID;

    private int vertexCount;

    public Model(int vaoID, int eboID, int[] vboID, int vertexCount) {
        this.vaoID = vaoID;
        this.eboID = eboID;
        this.vboID = vboID;
        this.vertexCount = vertexCount;
    }

    public int vaoID() {
        return vaoID;
    }

    public int eboID() {
        return eboID;
    }

    public int[] vboID() {
        return vboID;
    }

    public int vertexCount() {
        return vertexCount;
    }

    public void delete() {

        if (eboID != -1) {
            glDeleteBuffers(eboID);
        }

        for (int vbo : vboID) {
            glDeleteBuffers(vbo);
        }

        glDeleteVertexArrays(vaoID);
    }
}

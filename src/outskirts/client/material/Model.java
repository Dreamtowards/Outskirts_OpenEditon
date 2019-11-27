package outskirts.client.material;

import static org.lwjgl.opengl.ARBVertexArrayObject.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;

public class Model {

    private int vaoID;
    /** If not EBO, that eboID will be -1 */
    private int eboID;
    /** vboIDs */
    private int[] vboID;

    private int vertexCount;

    public Model(int vaoID, int eboID, int[] vboID, int vertexCount) {
        this.vaoID = vaoID;
        this.eboID = eboID;
        this.vboID = vboID;
        this.vertexCount = vertexCount;
    }

    /**
     * Method given people more Trust && StrongPower than Field in subconscious
     */
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

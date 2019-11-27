package outskirts.world.chunk;

import outskirts.block.state.BlockState;
import outskirts.util.Validate;
import outskirts.util.vector.Vector3f;

import java.util.function.Consumer;

//does this instance data final?
public class Octree {

    public static final int LENGTH = 8;     //children count

    //bits of x|y|z
    public static final Vector3f[] CHILDREN_POS = {
            new Vector3f(0.0f, 0.0f, 0.0f),
            new Vector3f(0.0f, 0.0f, 0.5f),
            new Vector3f(0.0f, 0.5f, 0.0f),
            new Vector3f(0.0f, 0.5f, 0.5f),
            new Vector3f(0.5f, 0.0f, 0.0f),
            new Vector3f(0.5f, 0.0f, 0.5f),
            new Vector3f(0.5f, 0.5f, 0.0f),
            new Vector3f(0.5f, 0.5f, 0.5f)
    };

    public static final float[] SIZES = new float[8];

    static {
        for (int i = 0;i < SIZES.length;i++) {
            SIZES[i] = Octree.calculateSize(i);
        }
    }

    private Octree parent;

    private Octree[] children;

    private BlockState body; //self? or blockState ...

    //root octree is never been set depth
    private int depth = 0;

    public Octree parent() {
        return parent;
    }

    public Octree child(int index) {
        return children[index];
    }

    public boolean hasChildren() {
        return children != null;
    }

    public boolean hasBody() {
        return body != null;
    }

    public void setChild(int index, Octree child) {
        Validate.validState(!hasBody(), "unable to set setChild, Self is existed.");

        if (!hasChildren())
            allocChildren();
        child.parent = this;
        child.depth = Octree.calculateDepth(child);
        children[index] = child;
    }

    private void allocChildren() {
        children = new Octree[8];
        for (int i = 0;i < children.length;i++)
            setChild(i, new Octree());
    }

//    public void removeChild(int index) { //may not this simple
//        children[index].parent = null;
//        children[index] = null;
//    }



    public BlockState body() {
        return body;
    }

    public void body(BlockState blockState) {
        Validate.validState(!hasChildren(), "unable to set Self. Children is existed.");

        this.body = blockState;
    }


    /**
     * root = 1
     * dep1 = .5
     * dep2 = .25
     * dep3 = .125
     */
    public float size() {
        return SIZES[depth];
    }

    public int depth() {
        return depth;
    }

    /**
     * root = 0
     * d    = 1
     */
    private static int calculateDepth(Octree octree) {
        if (octree.parent == null)
            return 0;
        return calculateDepth(octree.parent) + 1;
    }

    private static float calculateSize(int depth) {
        return (float)Math.pow(0.5f, depth);
    }

    /**
     * x, y, z between 0-1
     */
    public static int index(float x, float y, float z) {
        return (x >= 0.5f ? 4 : 0) | (y >= 0.5f ? 2 : 0) | (z >= 0.5f ? 1 : 0);
    }

    /**
     * this will not working when depth=0 (that just return root-self and not getting anything)
     */
    public enum GettingType {
        DIRECT,   //getting octree at specified position, if not find that will return null
        CREATING, //definitely return specified-position's octree and never return null. if not find, just creating octrees until reached the specified position
        NEAREST,  //return first hasBody()'s octree on the way that reaching specified position. if not find that will return null
    }

    public static Octree findChild(float x, float y, float z, int depth, Octree root, GettingType type) {
        Octree octree = root;
        for (int i = 0;i < depth;i++) {
            float size = octree.size();
            float invSize = 1f / size;
            if (type == GettingType.NEAREST && octree.hasBody()) {
                return octree;
            }
            if (!octree.hasChildren()) {
                if (type == GettingType.CREATING) {
                    octree.allocChildren();
                } else if (type == GettingType.DIRECT || type == GettingType.NEAREST){
                    return null;
                }
            }
            octree = octree.child(index(
                    (x % size) * invSize,
                    (y % size) * invSize,
                    (z % size) * invSize
            ));
        }
        return octree;
    }

    public static void forChildren(Octree octree, Consumer<Octree> accumulator) {

        accumulator.accept(octree);

        if (octree.hasChildren()) {
            for (int i = 0; i < LENGTH; i++) {
                forChildren(octree.child(i), accumulator);
            }
        }
    }
}

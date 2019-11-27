package outskirts.physics.collision.broadphase;

import outskirts.util.vector.Vector3f;

//2 vec is more flexibly and look-better than 6 float
public class AABB {

    public Vector3f min = new Vector3f();
    public Vector3f max = new Vector3f();

    public AABB() {}

    public AABB(Vector3f p1, Vector3f p2) {
        this(p1.x, p1.y, p1.z, p2.x, p2.y, p2.z);
    }

    public AABB(float x1, float y1, float z1, float x2, float y2, float z2) {
        min.x = Math.min(x1, x2);
        min.y = Math.min(y1, y2);
        min.z = Math.min(z1, z2);
        max.x = Math.max(x1, x2);
        max.y = Math.max(y1, y2);
        max.z = Math.max(z1, z2);
    }

    public void set(AABB other) {
        min.set(other.min);
        max.set(other.max);
    }

    public boolean contains(float x, float y, float z) {
        return x > min.x && x < max.x && y > min.y && y < max.y && z > min.z && z < max.z;
    }

    public boolean containsOrEquals(float x, float y, float z) {
        return x >= min.x && x <= max.x && y >= min.y && y <= max.y && z >= min.z && z <= max.z;
    }

//    public boolean intersects(AABB other) {
//        return  min.x < other.max.x && max.x > other.min.x &&
//                min.y < other.max.y && max.y > other.min.y &&
//                min.z < other.max.z && max.z > other.min.z;
//    }

    public static boolean intersects(AABB a, AABB b) {
        return  a.min.x < b.max.x && b.min.x < a.max.x &&
                a.min.y < b.max.y && b.min.y < a.max.y &&
                a.min.z < b.max.z && b.min.z < a.max.z;
    }

    public AABB grow(Vector3f vec) {
        min.sub(vec);
        max.add(vec);
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AABB && ((AABB)obj).min.equals(this.min) && ((AABB)obj).max.equals(this.max);
    }

    @Override
    public int hashCode() {
        return min.hashCode() * 31 ^ max.hashCode();
    }

    @Override
    public String toString() {
        return "AABB["+min+", "+max+"]";
    }
}

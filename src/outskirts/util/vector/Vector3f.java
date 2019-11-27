package outskirts.util.vector;

import outskirts.util.Maths;

public class Vector3f extends Vector {

    public static final Vector3f ZERO = new Vector3f(0.0f, 0.0f, 0.0f);   //default 'value' like required-position..
    public static final Vector3f ONE  = new Vector3f(1.0f, 1.0f, 1.0f);   //default scale

    public static final Vector3f UNIT_X = new Vector3f(1.0f, 0.0f, 0.0f); //rotation axis
    public static final Vector3f UNIT_Y = new Vector3f(0.0f, 1.0f, 0.0f);
    public static final Vector3f UNIT_Z = new Vector3f(0.0f, 0.0f, 1.0f);

    public float x;
    public float y;
    public float z;

    public Vector3f() {}

    public Vector3f(Vector3f src) {
        set(src);
    }

    public Vector3f(float x, float y, float z) {
        set(x, y, z);
    }

    public Vector3f set(Vector3f src) {
        return set(src.x, src.y, src.z);
    }

    public Vector3f set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    @Override
    public float lengthSquared() {
        return x * x + y * y + z * z;
    }

    @Override
    public Vector3f scale(float scalar) {
        this.x *= scalar;
        this.y *= scalar;
        this.z *= scalar;
        return this;
    }

    @Override
    public Vector3f negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        return this;
    }

    @Override
    public Vector3f normalize() {
        return (Vector3f) Vector.normalize(this);
    }

    @Override
    public String toString() {
        return "Vector3f[" + this.x + ", " + this.y + ", " + this.z + "]";
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Vector3f && ((Vector3f) obj).x == this.x && ((Vector3f) obj).y == this.y && ((Vector3f) obj).z == this.z;
    }

    @Override
    public int hashCode() {
        long hash = 0;
        hash = 31L * hash + Float.floatToIntBits(this.x);
        hash = 31L * hash + Float.floatToIntBits(this.y);
        hash = 31L * hash + Float.floatToIntBits(this.z);
        return (int) (hash ^ hash >> 32);
    }

    public Vector3f setX(float x) {
        this.x = x;
        return this;
    }

    public Vector3f setY(float y) {
        this.y = y;
        return this;
    }

    public Vector3f setZ(float z) {
        this.z = z;
        return this;
    }

    public Vector3f add(Vector3f right) {
        return add(right.x, right.y, right.z);
    }

    public Vector3f add(float x, float y, float z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public Vector3f sub(Vector3f right) {
        return sub(right.x, right.y, right.z);
    }

    public Vector3f sub(float x, float y, float z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }

    public static Vector3f add(Vector3f left, Vector3f right, Vector3f dest) {
        if (dest == null)
            dest = new Vector3f();
        return dest.set(
                left.x + right.x,
                left.y + right.y,
                left.z + right.z
        );
    }

    public static Vector3f sub(Vector3f left, Vector3f right, Vector3f dest) {
        if (dest == null)
            dest = new Vector3f();
        return dest.set(
                left.x - right.x,
                left.y - right.y,
                left.z - right.z
        );
    }

    public static float dot(Vector3f left, Vector3f right) {
        return left.x * right.x + left.y * right.y + left.z * right.z;
    }

    public static float angle(Vector3f a, Vector3f b) {
        return Vector.angle(dot(a, b), a, b);
    }

    public static Vector3f cross(Vector3f left, Vector3f right, Vector3f dest) {
        if (dest == null)
            dest = new Vector3f();

        return dest.set(
                left.y * right.z - left.z * right.y,
                left.z * right.x - left.x * right.z,
                left.x * right.y - left.y * right.x
        );
    }





    //ext
    public Vector3f addScaled(Vector3f factor, float scalar) {
        this.x += factor.x * scalar;
        this.y += factor.y * scalar;
        this.z += factor.z * scalar;
        return this;
    }

    //ext
    public static boolean isZero(Vector3f vec) {
        return vec.x == 0 && vec.y == 0 && vec.z == 0;
    }

    //should this..? (now for block pos select
    public static Vector3f unit(Vector3f vec, float unitSize) {
        return vec.set(
                Maths.unit(vec.x, unitSize),
                Maths.unit(vec.y, unitSize),
                Maths.unit(vec.z, unitSize)
        );
    }

    //really should this..? (now for marchingCube lerp
    public static Vector3f lerp(float t, Vector3f start, Vector3f end, Vector3f dest) {
        if (dest == null)
            dest = new Vector3f();
        return dest.set(
                Maths.lerp(t, start.x, end.x),
                Maths.lerp(t, start.y, end.y),
                Maths.lerp(t, start.z, end.z)
        );
    }
}

package outskirts.util.vector;

public class Quaternion extends Vector4f {


    public Quaternion() {
        setIdentity();
    }

    public Quaternion(Quaternion src) {
        set(src);
    }

    public Quaternion(float x, float y, float z, float w) {
        set(x, y, z, w);
    }

    @Override
    public Quaternion set(Vector4f src) { //should Quaternion only..?
        return set(src.x, src.y, src.z, src.w);
    }

    @Override
    public Quaternion set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        return this;
    }

    @Override
    public Quaternion scale(float scalar) {
        this.x *= scalar;
        this.y *= scalar;
        this.z *= scalar;
        this.w *= scalar;
        return this;
    }

    @Override
    public Quaternion negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        //not affect to w
        return this;
    }

    @Override
    public Quaternion normalize() {
        return (Quaternion) Vector.normalize(this);
    }

    @Override
    public String toString() {
        return "Quaternion[" + this.x + ", " + this.y + ", " + this.z + ", " + this.w + "]";
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Quaternion && ((Quaternion)obj).x == this.x && ((Quaternion)obj).y == this.y && ((Quaternion)obj).z == this.z && ((Quaternion)obj).w == this.w;
    }

    @Override
    public Quaternion setX(float x) {
        this.x = x;
        return this;
    }

    @Override
    public Quaternion setY(float y) {
        this.y = y;
        return this;
    }

    @Override
    public Quaternion setZ(float z) {
        this.z = z;
        return this;
    }

    @Override
    public Quaternion setW(float w) {
        this.w = w;
        return this;
    }

    @Override
    public Quaternion add(Vector4f right) {
        return add(right.x, right.y, right.z, right.w);
    }

    @Override
    public Quaternion add(float x, float y, float z, float w) {
        this.x += x;
        this.y += y;
        this.z += z;
        this.w += w;
        return this;
    }

    @Override
    public Quaternion sub(Vector4f right) {
        return sub(right.x, right.y, right.z, right.w);
    }

    @Override
    public Quaternion sub(float x, float y, float z, float w) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        this.w -= w;
        return this;
    }

    public Quaternion setIdentity() {
        this.x = 0f;
        this.y = 0f;
        this.z = 0f;
        this.w = 1f;
        return this;
    }

    public Quaternion inverse() {
        float invSq = 1.0F / lengthSquared();
        this.x *= -invSq;
        this.y *= -invSq;
        this.z *= -invSq;
        this.w *=  invSq;
        return this;
    }

    public static Quaternion mul(Quaternion left, Quaternion right, Quaternion dest) {
        if (dest == null)
            dest = new Quaternion();
        return dest.set(
                left.x * right.w + left.w * right.x + left.y * right.z - left.z * right.y,
                left.y * right.w + left.w * right.y + left.z * right.x - left.x * right.z,
                left.z * right.w + left.w * right.z + left.x * right.y - left.y * right.x,
                left.w * right.w - left.x * right.x - left.y * right.y - left.z * right.z
        );
    }



    public static Quaternion fromAxisAngle(Vector4f a, Quaternion dest) {
        if (dest == null)
            dest = new Quaternion();
        float n = (float)Math.sqrt(a.x * a.x + a.y * a.y + a.z * a.z);
        float s = (float)Math.sin(a.w * 0.5f) / n;
        return dest.set(
                a.x * s,
                a.y * s,
                a.z * s,
                (float)Math.cos(a.w * 0.5f)
        );
    }

    public static Vector4f toAxisAngle(Quaternion q, Vector4f dest) {
        if (dest == null)
            dest = new Vector4f();
        if (q.w > 1.0f)
            q.normalize();
        float s = (float)Math.sqrt(1f - q.w * q.w);
        return dest.set(
                q.x / s,
                q.y / s,
                q.z / s,
                (float)Math.acos(q.w) * 2f
        );
    }

    public static Quaternion fromMatrix(Matrix3f mat, Quaternion dest) {
        return fromMatrix(mat.m00, mat.m01, mat.m02, mat.m10, mat.m11, mat.m12, mat.m20, mat.m21, mat.m22, dest);
    }

    public static Quaternion fromMatrix(Matrix4f mat, Quaternion dest) {
        return fromMatrix(mat.m00, mat.m01, mat.m02, mat.m10, mat.m11, mat.m12, mat.m20, mat.m21, mat.m22, dest);
    }

    /**
     * just like Matrix3f
     * m00 m01 m02
     * m10 m11 m12
     * m20 m21 m22
     */
    private static Quaternion fromMatrix(float m00, float m01, float m02,
                                         float m10, float m11, float m12,
                                         float m20, float m21, float m22, Quaternion dest) {
        if (dest == null)
            dest = new Quaternion();

        float tr = m00 + m11 + m12;
        float s;
        if (tr >= 0f) {
            s = (float)Math.sqrt(tr + 1f);
            dest.w = s * 0.5f;
            s = 0.5f / s;
            dest.x = (m12 - m21) * s;
            dest.y = (m20 - m02) * s;
            dest.z = (m01 - m10) * s;
        } else {
            float max = Math.max(Math.max(m00, m11), m22);
            if (max == m00) {
                s = (float)Math.sqrt(m00 - (m11 + m22) + 1f);
                dest.x = s * 0.5f;
                s = 0.5f / s;
                dest.y = (m10 + m01) * s;
                dest.z = (m02 + m20) * s;
                dest.w = (m12 - m21) * s;
            } else if (max == m11) {
                s = (float)Math.sqrt(m11 - (m22 + m00) + 1f);
                dest.y = s * 0.5f;
                s = 0.5f / s;
                dest.z = (m21 + m12) * s;
                dest.x = (m10 + m01) * s;
                dest.w = (m20 - m02) * s;
            } else {
                s = (float)Math.sqrt(m22 - (m00 + m11) + 1f);
                dest.z = s * 0.5f;
                s = 0.5f / s;
                dest.x = (m02 + m20) * s;
                dest.y = (m21 + m12) * s;
                dest.w = (m01 - m10) * s;
            }
        }
        return dest;
    }

    public static Matrix3f toMatrix(Quaternion q, Matrix3f dest) {
        float d = q.lengthSquared();
        if (d == 0.0F)
            throw new ArithmeticException("Zero length quaternion.");
        float s = 2f / d;
        float xs = q.x * s,  ys = q.y * s,  zs = q.z * s;
        float wx = q.w * xs, wy = q.w * ys, wz = q.w * zs;
        float xx = q.x * xs, xy = q.x * ys, xz = q.x * zs;
        float yy = q.y * ys, yz = q.y * zs, zz = q.z * zs;
        dest.m00 = 1f - (yy + zz);
        dest.m01 = xy - wz;
        dest.m02 = xz + wy;
        dest.m10 = xy + wz;
        dest.m11 = 1f - (xx + zz);
        dest.m12 = yz - wx;
        dest.m20 = xz - wy;
        dest.m21 = yz + wx;
        dest.m22 = 1f - (xx + yy);
        return dest;
    }
}




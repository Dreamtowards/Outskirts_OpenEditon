package outskirts.util.vector;

import java.nio.FloatBuffer;

public class Matrix3f extends Matrix {

    public static final Matrix3f IDENTITY = new Matrix3f().setIdentity();

    /**
     * m00 m01 m02
     * m10 m11 m12
     * m20 m21 m22
     */
    public float m00;
    public float m01;
    public float m02;
    public float m10;
    public float m11;
    public float m12;
    public float m20;
    public float m21;
    public float m22;

    public Matrix3f() {
        setIdentity();
    }

    public Matrix3f(Matrix3f src) {
        set(src);
    }

    public Matrix3f(float m00, float m01, float m02, float m10, float m11, float m12, float m20, float m21, float m22) {
        set(m00, m01, m02, m10, m11, m12, m20, m21, m22);
    }

    public Matrix3f set(Matrix3f src) {
        return set(src.m00, src.m01, src.m02, src.m10, src.m11, src.m12, src.m20, src.m21, src.m22);
    }

    public Matrix3f set(float m00, float m01, float m02, float m10, float m11, float m12, float m20, float m21, float m22) {
        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
        this.m20 = m20;
        this.m21 = m21;
        this.m22 = m22;
        return this;
    }

    @Override
    public Matrix3f setIdentity() {
        return set(
                1, 0, 0,
                0, 1, 0,
                0, 0, 1
        );
    }

    @Override
    public Matrix3f setZero() {
        return set(
                0, 0, 0,
                0, 0, 0,
                0, 0, 0
        );
    }

    @Override
    public Matrix3f negate() {
        this.m00 = -this.m00;
        this.m01 = -this.m01;
        this.m02 = -this.m02;
        this.m10 = -this.m10;
        this.m11 = -this.m11;
        this.m12 = -this.m12;
        this.m20 = -this.m20;
        this.m21 = -this.m21;
        this.m22 = -this.m22;
        return this;
    }

    @Override
    public Matrix3f transpose() {
        return set(
                m00, m10, m20,
                m01, m11, m21,
                m02, m12, m22
        );
    }

    @Override
    public Matrix3f invert() {
        float determinate = determinant();
        if (determinate == 0.0f)
            throw new IllegalStateException("Zero determinant matrix.");

        float invDet = 1.0F / determinate;
        float t00 =  m11 * m22 - m21 * m12;
        float t01 = -m01 * m22 + m21 * m02;
        float t02 =  m01 * m12 - m11 * m02;
        float t10 = -m10 * m22 + m20 * m12;
        float t11 =  m00 * m22 - m20 * m02;
        float t12 = -m00 * m12 + m10 * m02;
        float t20 =  m10 * m21 - m20 * m11;
        float t21 = -m00 * m21 + m20 * m01;
        float t22 =  m00 * m11 - m10 * m01;
        return set(
                t00 * invDet, t01 * invDet, t02 * invDet,
                t10 * invDet, t11 * invDet, t12 * invDet,
                t20 * invDet, t21 * invDet, t22 * invDet
        );
    }

    @Override
    public float determinant() {
        return  m00 * (m11 * m22 - m12 * m21) -
                m01 * (m10 * m22 - m12 * m20) +
                m02 * (m10 * m21 - m11 * m20);
    }

    @Override
    public String toString() {
        return  m00 + " " + m01 + " " + m02 + "\n" +
                m10 + " " + m11 + " " + m12 + "\n" +
                m20 + " " + m21 + " " + m22 + "\n";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Matrix3f) {
            Matrix3f other = (Matrix3f) obj;
            return  other.m00 == this.m00 && other.m01 == this.m01 && other.m02 == this.m02 &&
                    other.m10 == this.m10 && other.m11 == this.m11 && other.m12 == this.m12 &&
                    other.m20 == this.m20 && other.m21 == this.m21 && other.m22 == this.m22;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        long hash = 0;
        hash = 31L * hash + Float.floatToIntBits(m00);
        hash = 31L * hash + Float.floatToIntBits(m01);
        hash = 31L * hash + Float.floatToIntBits(m02);
        hash = 31L * hash + Float.floatToIntBits(m10);
        hash = 31L * hash + Float.floatToIntBits(m11);
        hash = 31L * hash + Float.floatToIntBits(m12);
        hash = 31L * hash + Float.floatToIntBits(m20);
        hash = 31L * hash + Float.floatToIntBits(m21);
        hash = 31L * hash + Float.floatToIntBits(m22);
        return (int) (hash ^ hash >> 32);
    }

    public static void store(Matrix3f matrix, FloatBuffer buffer) {
        buffer.put(matrix.m00);
        buffer.put(matrix.m01);
        buffer.put(matrix.m02);
        buffer.put(matrix.m10);
        buffer.put(matrix.m11);
        buffer.put(matrix.m12);
        buffer.put(matrix.m20);
        buffer.put(matrix.m21);
        buffer.put(matrix.m22);
    }

    public static void load(Matrix3f matrix, FloatBuffer buffer) {
        matrix.m00 = buffer.get();
        matrix.m01 = buffer.get();
        matrix.m02 = buffer.get();
        matrix.m10 = buffer.get();
        matrix.m11 = buffer.get();
        matrix.m12 = buffer.get();
        matrix.m20 = buffer.get();
        matrix.m21 = buffer.get();
        matrix.m22 = buffer.get();
    }

    public Matrix3f add(Matrix3f right) {
        return Matrix3f.add(this, right, this);
    }

    public Matrix3f sub(Matrix3f right) {
        return Matrix3f.sub(this, right, this);
    }

    public Matrix3f mul(Matrix3f right) {
        return Matrix3f.mul(this, right, this);
    }

    public static Matrix3f add(Matrix3f left, Matrix3f right, Matrix3f dest) {
        if (dest == null)
            dest = new Matrix3f();

        dest.m00 = left.m00 + right.m00;
        dest.m01 = left.m01 + right.m01;
        dest.m02 = left.m02 + right.m02;
        dest.m10 = left.m10 + right.m10;
        dest.m11 = left.m11 + right.m11;
        dest.m12 = left.m12 + right.m12;
        dest.m20 = left.m20 + right.m20;
        dest.m21 = left.m21 + right.m21;
        dest.m22 = left.m22 + right.m22;

        return dest;
    }

    public static Matrix3f sub(Matrix3f left, Matrix3f right, Matrix3f dest) {
        if (dest == null)
            dest = new Matrix3f();

        dest.m00 = left.m00 - right.m00;
        dest.m01 = left.m01 - right.m01;
        dest.m02 = left.m02 - right.m02;
        dest.m10 = left.m10 - right.m10;
        dest.m11 = left.m11 - right.m11;
        dest.m12 = left.m12 - right.m12;
        dest.m20 = left.m20 - right.m20;
        dest.m21 = left.m21 - right.m21;
        dest.m22 = left.m22 - right.m22;

        return dest;
    }

    public static Matrix3f mul(Matrix3f left, Matrix3f right, Matrix3f dest) {
        if (dest == null)
            dest = new Matrix3f();

        float m00 = left.m00 * right.m00 + left.m01 * right.m10 + left.m02 * right.m20;
        float m01 = left.m00 * right.m01 + left.m01 * right.m11 + left.m02 * right.m21;
        float m02 = left.m00 * right.m02 + left.m01 * right.m12 + left.m02 * right.m22;
        float m10 = left.m10 * right.m00 + left.m11 * right.m10 + left.m12 * right.m20;
        float m11 = left.m10 * right.m01 + left.m11 * right.m11 + left.m12 * right.m21;
        float m12 = left.m10 * right.m02 + left.m11 * right.m12 + left.m12 * right.m22;
        float m20 = left.m20 * right.m00 + left.m21 * right.m10 + left.m22 * right.m20;
        float m21 = left.m20 * right.m01 + left.m21 * right.m11 + left.m22 * right.m21;
        float m22 = left.m20 * right.m02 + left.m21 * right.m12 + left.m22 * right.m22;

        return dest.set(m00, m01, m02, m10, m11, m12, m20, m21, m22);
    }

    public static Vector3f transform(Matrix3f left, Vector3f right, Vector3f dest) {
        if (dest == null)
            dest = new Vector3f();

        float x = left.m00 * right.x + left.m01 * right.y + left.m02 * right.z;
        float y = left.m10 * right.x + left.m11 * right.y + left.m12 * right.z;
        float z = left.m20 * right.x + left.m21 * right.y + left.m22 * right.z;

        return dest.set(x, y, z);
    }
}






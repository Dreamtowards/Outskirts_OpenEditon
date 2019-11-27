package outskirts.util.vector;

import java.nio.FloatBuffer;

public class Matrix4f extends Matrix {

    public static final Matrix4f IDENTITY = new Matrix4f().setIdentity(); // e.g use for default transMatrix only-value

    /**
     * m00 m01 m02 m03
     * m10 m11 m12 m13
     * m20 m21 m22 m23
     * m30 m31 m32 m33
     */
    public float m00;
    public float m01;
    public float m02;
    public float m03;
    public float m10;
    public float m11;
    public float m12;
    public float m13;
    public float m20;
    public float m21;
    public float m22;
    public float m23;
    public float m30;
    public float m31;
    public float m32;
    public float m33;

    public Matrix4f() {
        setIdentity();
    }

    public Matrix4f(Matrix4f src) {
        set(src);
    }

    public Matrix4f(float m00, float m01, float m02, float m03, float m10, float m11, float m12, float m13, float m20, float m21, float m22, float m23, float m30, float m31, float m32, float m33) {
        set(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33);
    }

    public Matrix4f set(Matrix4f src) {
        return set(src.m00, src.m01, src.m02, src.m03, src.m10, src.m11, src.m12, src.m13, src.m20, src.m21, src.m22, src.m23, src.m30, src.m31, src.m32, src.m33);
    }

    public Matrix4f set(float m00, float m01, float m02, float m03, float m10, float m11, float m12, float m13, float m20, float m21, float m22, float m23, float m30, float m31, float m32, float m33) {
        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        this.m03 = m03;
        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
        this.m13 = m13;
        this.m20 = m20;
        this.m21 = m21;
        this.m22 = m22;
        this.m23 = m23;
        this.m30 = m30;
        this.m31 = m31;
        this.m32 = m32;
        this.m33 = m33;
        return this;
    }

    @Override
    public Matrix4f setIdentity() {
        return set(
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        );
    }

    @Override
    public Matrix4f setZero() {
        return set(
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0
        );
    }

    @Override
    public Matrix4f negate() {
        this.m00 = -this.m00;
        this.m01 = -this.m01;
        this.m02 = -this.m02;
        this.m03 = -this.m03;
        this.m10 = -this.m10;
        this.m11 = -this.m11;
        this.m12 = -this.m12;
        this.m13 = -this.m13;
        this.m20 = -this.m20;
        this.m21 = -this.m21;
        this.m22 = -this.m22;
        this.m23 = -this.m23;
        this.m30 = -this.m30;
        this.m31 = -this.m31;
        this.m32 = -this.m32;
        this.m33 = -this.m33;
        return this;
    }

    @Override
    public Matrix4f transpose() {
        return set(
                m00, m10, m20, m30,
                m01, m11, m21, m31,
                m02, m12, m22, m32,
                m03, m13, m23, m33
        );
    }

    private static float determinant3x3(float t00, float t01, float t02, float t10, float t11, float t12, float t20, float t21, float t22) {
        return t00 * (t11 * t22 - t12 * t21) - t01 * (t10 * t22 - t12 * t20) + t02 * (t10 * t21 - t11 * t20);
    }

    @Override
    public Matrix4f invert() {
        float determinant = determinant();
        if (determinant == 0.0f)
            throw new ArithmeticException("Zero determinant matrix.");
        
        float invDet = 1.0F / determinant;
        float t00 =  determinant3x3(m11, m21, m31, m12, m22, m32, m13, m23, m33);
        float t01 = -determinant3x3(m01, m21, m31, m02, m22, m32, m03, m23, m33);
        float t02 =  determinant3x3(m01, m11, m31, m02, m12, m32, m03, m13, m33);
        float t03 = -determinant3x3(m01, m11, m21, m02, m12, m22, m03, m13, m23);
        float t10 = -determinant3x3(m10, m20, m30, m12, m22, m32, m13, m23, m33);
        float t11 =  determinant3x3(m00, m20, m30, m02, m22, m32, m03, m23, m33);
        float t12 = -determinant3x3(m00, m10, m30, m02, m12, m32, m03, m13, m33);
        float t13 =  determinant3x3(m00, m10, m20, m02, m12, m22, m03, m13, m23);
        float t20 =  determinant3x3(m10, m20, m30, m11, m21, m31, m13, m23, m33);
        float t21 = -determinant3x3(m00, m20, m30, m01, m21, m31, m03, m23, m33);
        float t22 =  determinant3x3(m00, m10, m30, m01, m11, m31, m03, m13, m33);
        float t23 = -determinant3x3(m00, m10, m20, m01, m11, m21, m03, m13, m23);
        float t30 = -determinant3x3(m10, m20, m30, m11, m21, m31, m12, m22, m32);
        float t31 =  determinant3x3(m00, m20, m30, m01, m21, m31, m02, m22, m32);
        float t32 = -determinant3x3(m00, m10, m30, m01, m11, m31, m02, m12, m32);
        float t33 =  determinant3x3(m00, m10, m20, m01, m11, m21, m02, m12, m22);
        return set(
                t00 * invDet, t01 * invDet, t02 * invDet, t03 * invDet,
                t10 * invDet, t11 * invDet, t12 * invDet, t13 * invDet,
                t20 * invDet, t21 * invDet, t22 * invDet, t23 * invDet,
                t30 * invDet, t31 * invDet, t32 * invDet, t33 * invDet
        );
    }

    @Override
    public float determinant() {
        return  m00 * (m11 * (m22 * m33 - m23 * m32) - m12 * (m21 * m33 - m23 * m31) + m13 * (m21 * m32 - m22 * m31)) -
                m01 * (m10 * (m22 * m33 - m23 * m32) - m12 * (m20 * m33 - m23 * m30) + m13 * (m20 * m32 - m22 * m30)) +
                m02 * (m10 * (m21 * m33 - m23 * m31) - m11 * (m20 * m33 - m23 * m30) + m13 * (m20 * m31 - m21 * m30)) -
                m03 * (m10 * (m21 * m32 - m22 * m31) - m11 * (m20 * m32 - m22 * m30) + m12 * (m20 * m31 - m21 * m30));
    }

    @Override
    public String toString() {
        return  m00 + " " + m01 + " " + m02 + " " + m03 + "\n" +
                m10 + " " + m11 + " " + m12 + " " + m13 + "\n" +
                m20 + " " + m21 + " " + m22 + " " + m23 + "\n" +
                m30 + " " + m31 + " " + m32 + " " + m33 + "\n";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Matrix4f) {
            Matrix4f other = (Matrix4f) obj;
            return  other.m00 == this.m00 && other.m01 == this.m01 && other.m02 == this.m02 && other.m03 == this.m03 &&
                    other.m10 == this.m10 && other.m11 == this.m11 && other.m12 == this.m12 && other.m13 == this.m13 &&
                    other.m20 == this.m20 && other.m21 == this.m21 && other.m22 == this.m22 && other.m23 == this.m23 &&
                    other.m30 == this.m30 && other.m31 == this.m31 && other.m32 == this.m32 && other.m33 == this.m33;
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
        hash = 31L * hash + Float.floatToIntBits(m03);
        hash = 31L * hash + Float.floatToIntBits(m10);
        hash = 31L * hash + Float.floatToIntBits(m11);
        hash = 31L * hash + Float.floatToIntBits(m12);
        hash = 31L * hash + Float.floatToIntBits(m13);
        hash = 31L * hash + Float.floatToIntBits(m20);
        hash = 31L * hash + Float.floatToIntBits(m21);
        hash = 31L * hash + Float.floatToIntBits(m22);
        hash = 31L * hash + Float.floatToIntBits(m23);
        hash = 31L * hash + Float.floatToIntBits(m30);
        hash = 31L * hash + Float.floatToIntBits(m31);
        hash = 31L * hash + Float.floatToIntBits(m32);
        hash = 31L * hash + Float.floatToIntBits(m33);
        return (int) (hash ^ hash >> 32);
    }

    public static void store(Matrix4f matrix, FloatBuffer buffer) {
        buffer.put(matrix.m00);
        buffer.put(matrix.m01);
        buffer.put(matrix.m02);
        buffer.put(matrix.m03);
        buffer.put(matrix.m10);
        buffer.put(matrix.m11);
        buffer.put(matrix.m12);
        buffer.put(matrix.m13);
        buffer.put(matrix.m20);
        buffer.put(matrix.m21);
        buffer.put(matrix.m22);
        buffer.put(matrix.m23);
        buffer.put(matrix.m30);
        buffer.put(matrix.m31);
        buffer.put(matrix.m32);
        buffer.put(matrix.m33);
    }

    public static void load(Matrix4f matrix, FloatBuffer buffer) {
        matrix.m00 = buffer.get();
        matrix.m01 = buffer.get();
        matrix.m02 = buffer.get();
        matrix.m03 = buffer.get();
        matrix.m10 = buffer.get();
        matrix.m11 = buffer.get();
        matrix.m12 = buffer.get();
        matrix.m13 = buffer.get();
        matrix.m20 = buffer.get();
        matrix.m21 = buffer.get();
        matrix.m22 = buffer.get();
        matrix.m23 = buffer.get();
        matrix.m30 = buffer.get();
        matrix.m31 = buffer.get();
        matrix.m32 = buffer.get();
        matrix.m33 = buffer.get();
    }

    public Matrix4f add(Matrix4f right) {
        return Matrix4f.add(this, right, this);
    }

    public Matrix4f sub(Matrix4f right) {
        return Matrix4f.sub(this, right, this);
    }

    public Matrix4f mul(Matrix4f right) {
        return Matrix4f.mul(this, right, this);
    }

    public static Matrix4f add(Matrix4f left, Matrix4f right, Matrix4f dest) {
        if (dest == null)
            dest = new Matrix4f();

        dest.m00 = left.m00 + right.m00;
        dest.m01 = left.m01 + right.m01;
        dest.m02 = left.m02 + right.m02;
        dest.m03 = left.m03 + right.m03;
        dest.m10 = left.m10 + right.m10;
        dest.m11 = left.m11 + right.m11;
        dest.m12 = left.m12 + right.m12;
        dest.m13 = left.m13 + right.m13;
        dest.m20 = left.m20 + right.m20;
        dest.m21 = left.m21 + right.m21;
        dest.m22 = left.m22 + right.m22;
        dest.m23 = left.m23 + right.m23;
        dest.m30 = left.m30 + right.m30;
        dest.m31 = left.m31 + right.m31;
        dest.m32 = left.m32 + right.m32;
        dest.m33 = left.m33 + right.m33;

        return dest;
    }

    public static Matrix4f sub(Matrix4f left, Matrix4f right, Matrix4f dest) {
        if (dest == null)
            dest = new Matrix4f();

        dest.m00 = left.m00 - right.m00;
        dest.m01 = left.m01 - right.m01;
        dest.m02 = left.m02 - right.m02;
        dest.m03 = left.m03 - right.m03;
        dest.m10 = left.m10 - right.m10;
        dest.m11 = left.m11 - right.m11;
        dest.m12 = left.m12 - right.m12;
        dest.m13 = left.m13 - right.m13;
        dest.m20 = left.m20 - right.m20;
        dest.m21 = left.m21 - right.m21;
        dest.m22 = left.m22 - right.m22;
        dest.m23 = left.m23 - right.m23;
        dest.m30 = left.m30 - right.m30;
        dest.m31 = left.m31 - right.m31;
        dest.m32 = left.m32 - right.m32;
        dest.m33 = left.m33 - right.m33;

        return dest;
    }

    public static Matrix4f mul(Matrix4f left, Matrix4f right, Matrix4f dest) {
        if (dest == null)
            dest = new Matrix4f();

        float m00 = left.m00 * right.m00 + left.m01 * right.m10 + left.m02 * right.m20 + left.m03 * right.m30;
        float m01 = left.m00 * right.m01 + left.m01 * right.m11 + left.m02 * right.m21 + left.m03 * right.m31;
        float m02 = left.m00 * right.m02 + left.m01 * right.m12 + left.m02 * right.m22 + left.m03 * right.m32;
        float m03 = left.m00 * right.m03 + left.m01 * right.m13 + left.m02 * right.m23 + left.m03 * right.m33;
        float m10 = left.m10 * right.m00 + left.m11 * right.m10 + left.m12 * right.m20 + left.m13 * right.m30;
        float m11 = left.m10 * right.m01 + left.m11 * right.m11 + left.m12 * right.m21 + left.m13 * right.m31;
        float m12 = left.m10 * right.m02 + left.m11 * right.m12 + left.m12 * right.m22 + left.m13 * right.m32;
        float m13 = left.m10 * right.m03 + left.m11 * right.m13 + left.m12 * right.m23 + left.m13 * right.m33;
        float m20 = left.m20 * right.m00 + left.m21 * right.m10 + left.m22 * right.m20 + left.m23 * right.m30;
        float m21 = left.m20 * right.m01 + left.m21 * right.m11 + left.m22 * right.m21 + left.m23 * right.m31;
        float m22 = left.m20 * right.m02 + left.m21 * right.m12 + left.m22 * right.m22 + left.m23 * right.m32;
        float m23 = left.m20 * right.m03 + left.m21 * right.m13 + left.m22 * right.m23 + left.m23 * right.m33;
        float m30 = left.m30 * right.m00 + left.m31 * right.m10 + left.m32 * right.m20 + left.m33 * right.m30;
        float m31 = left.m30 * right.m01 + left.m31 * right.m11 + left.m32 * right.m21 + left.m33 * right.m31;
        float m32 = left.m30 * right.m02 + left.m31 * right.m12 + left.m32 * right.m22 + left.m33 * right.m32;
        float m33 = left.m30 * right.m03 + left.m31 * right.m13 + left.m32 * right.m23 + left.m33 * right.m33;

        return dest.set(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33);
    }

    public static Vector4f transform(Matrix4f left, Vector4f right, Vector4f dest) {
        if (dest == null)
            dest = new Vector4f();

        float x = left.m00 * right.x + left.m01 * right.y + left.m02 * right.z + left.m03 + right.w;
        float y = left.m10 * right.x + left.m11 * right.y + left.m12 * right.z + left.m13 + right.w;
        float z = left.m20 * right.x + left.m21 * right.y + left.m22 * right.z + left.m23 + right.w;
        float w = left.m30 * right.x + left.m31 * right.y + left.m32 * right.z + left.m33 + right.w;

        return dest.set(x, y, z, w);
    }

    public static Matrix4f translate(Vector3f vec, Matrix4f dest) {
        if (dest == null)
            dest = new Matrix4f();

        dest.m03 += dest.m00 * vec.x + dest.m01 * vec.y + dest.m02 * vec.z;
        dest.m13 += dest.m10 * vec.x + dest.m11 * vec.y + dest.m12 * vec.z;
        dest.m23 += dest.m20 * vec.x + dest.m21 * vec.y + dest.m22 * vec.z;
        dest.m33 += dest.m30 * vec.x + dest.m31 * vec.y + dest.m32 * vec.z;

        return dest;
    }

    public static Matrix4f scale(Vector3f vec, Matrix4f dest) {
        if (dest == null)
            dest = new Matrix4f();

        dest.m00 *= vec.x;
        dest.m10 *= vec.x;
        dest.m20 *= vec.x;
        dest.m30 *= vec.x;
        dest.m01 *= vec.y;
        dest.m11 *= vec.y;
        dest.m21 *= vec.y;
        dest.m31 *= vec.y;
        dest.m02 *= vec.z;
        dest.m12 *= vec.z;
        dest.m22 *= vec.z;
        dest.m32 *= vec.z;

        return dest;
    }

    public static Matrix4f rotate(float angle, Vector3f axis, Matrix4f dest) {
        if (dest == null)
            dest = new Matrix4f();

        float c = (float)Math.cos(angle);
        float s = (float)Math.sin(angle);
        float oneminusc = 1.0F - c;
        float xy = axis.x * axis.y;
        float yz = axis.y * axis.z;
        float xz = axis.x * axis.z;
        float xs = axis.x * s;
        float ys = axis.y * s;
        float zs = axis.z * s;

        float f00 = axis.x * axis.x * oneminusc + c;
        float f01 = xy * oneminusc - zs;
        float f02 = xz * oneminusc + ys;

        float f10 = xy * oneminusc + zs;
        float f11 = axis.y * axis.y * oneminusc + c;
        float f12 = yz * oneminusc - xs;

        float f20 = xz * oneminusc - ys;
        float f21 = yz * oneminusc + xs;
        float f22 = axis.z * axis.z * oneminusc + c;

        return dest.set(
                dest.m00 * f00 + dest.m01 * f10 + dest.m02 * f20,
                dest.m00 * f01 + dest.m01 * f11 + dest.m02 * f21,
                dest.m00 * f02 + dest.m01 * f12 + dest.m02 * f22,
                dest.m03,

                dest.m10 * f00 + dest.m11 * f10 + dest.m12 * f20,
                dest.m10 * f01 + dest.m11 * f11 + dest.m12 * f21,
                dest.m10 * f02 + dest.m11 * f12 + dest.m12 * f22,
                dest.m13,

                dest.m20 * f00 + dest.m21 * f10 + dest.m22 * f20,
                dest.m20 * f01 + dest.m21 * f11 + dest.m22 * f21,
                dest.m20 * f02 + dest.m21 * f12 + dest.m22 * f22,
                dest.m23,

                dest.m30 * f00 + dest.m31 * f10 + dest.m32 * f20,
                dest.m30 * f01 + dest.m31 * f11 + dest.m32 * f21,
                dest.m30 * f02 + dest.m31 * f12 + dest.m32 * f22,
                dest.m33
        );
    }
}

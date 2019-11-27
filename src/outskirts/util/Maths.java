package outskirts.util;

import org.lwjgl.opengl.Display;
import outskirts.util.logging.Log;
import outskirts.util.vector.*;

import java.util.Random;

public class Maths {

    static final Random RAND = new Random();

    private static final float FLT_EPSILON = 1.19209290e-07f;

    public static float clamp(float value, float a, float b) {
        float min = Math.min(a, b);
        float max = Math.max(a, b);

        if (value > max)
            return max;

        if (value < min)
            return min;

        return value;
    }

    public static int clamp(int value, int a, int b) {
        int min = Math.min(a, b);
        int max = Math.max(a, b);

        if (value > max)
            return max;

        if (value < min)
            return min;

        return value;
    }

    public static int ceil(float value) {
        return (int)Math.ceil(value);
    }

    // speed similar to
    // int i = (int)value; return (i <= value) ? i : i - 1;
    public static int floor(float value) {
        return (int)Math.floor(value);
    }


    /**
     * Linear Interpolation from start to end
     * s+(e-s)*t == s+et-st == s-st+et == (s1-st)+et == s(1-t)+et
     * @param t 0.0-1.0 if you needs return value between start and end
     */
    public static float lerp(float t, float start, float end) {
        return start + (end - start) * t;
    }

    /**
     * @return Percentage of value between start and end.
     */
    public static float inverseLerp(float value, float start, float end) {
        return (value - start) / (end - start);
    }

    /**
     * Cosine Interpolation
     */
    public static float cosp(float t) {
        return (float)(1f - Math.cos(t * Math.PI)) * 0.5f;
    }

    private static float cosp(float t, float start, float end) {
        return lerp(cosp(t), start, end);
    }

    /**
     * Cubic Interpolation
     * Alias. SmoothStep
     * @param t as v1 as start and as v2 as end
     */
    public static float cubep(float t, float v0, float v1, float v2, float v3) {
        float P = (v3 - v2) - (v0 - v1);
        float Q = (v0 - v1) - P;
        float R = v2 - v0;
        float S = v1;
        float t2 = t * t;
        return P * (t2 * t) + Q * t2 + R * t + S;
    }

    /**
     * Hermite Interpolation
     * Tension: 1 is high, 0 normal, -1 is low
     * Bias: 0 is even, positive is towards first segment, negative towards the other
     */
    public static float hermitep(float t, float v0,float v1, float v2,float v3, float tension, float bias) {
        float t2 = t * t;
        float t3 = t2 * t;
        float m0  = (v1-v0)*(1+bias)*(1-tension)/2 +
                    (v2-v1)*(1-bias)*(1-tension)/2;
        float m1  = (v2-v1)*(1+bias)*(1-tension)/2 +
                    (v3-v2)*(1-bias)*(1-tension)/2;
        float a0 =  2*t3 - 3*t2 + 1;
        float a1 =    t3 - 2*t2 + t;
        float a2 =    t3 -   t2;
        float a3 = -2*t3 + 3*t2;
        return (a0*v1 + a1*m0 + a2*m1 + a3*v2);
    }

    public static float backease(float t, float amplitude) {
        return t*t*t - t*amplitude*(float)Math.sin(t*Math.PI);
    }

    public static float circleease(float t) {
        return 1f - (float)Math.sqrt(1f - t*t);
    }

    public static float powerease(float t, float x) {
        return (float)Math.pow(t, x);
    }

    /**
     * @return fraction 0.0-1.0
     */
    public static float frac(float x) {
        return x - Maths.floor(x);
    }

    public static float log(float x, float base) {
        return (float)(Math.log(x) / Math.log(base));
    }

    public static int log2nlz(int x) {
        if (x == 0)
            throw new ArithmeticException();
        return 31 - Integer.numberOfLeadingZeros(x);
    }

    public static boolean fuzzyZero(float v) {
        return Math.abs(v) < FLT_EPSILON;
    }

    /**
     *  not a academic name
     */
    public static float unit(float value, float unitSize) {
        return floor(value / unitSize) * unitSize;
    }

    /**
     * Mod calculation is different with Rem(java % operation)
     *  5 Mod 16 = 5
     * -5 Mod 16 = 11
     *  5 %(rem) 16 = 5
     * -5 %      16 =-5
     * (use for negative coordinate chunk-relative position calculation)
     */
    public static float mod(float v, float b) {
        return v - (Maths.floor(v / b) * b);
    }

    public static Vector3f calculateEulerDirection(float pitch, float yaw) {
        float f0 = (float)Math.cos(Math.toRadians(-yaw) - (float)Math.PI);
        float f1 = (float)Math.sin(Math.toRadians(-yaw) - (float)Math.PI);
        float f2 = (float)Math.cos(Math.toRadians(-pitch));
        float f3 = (float)Math.sin(Math.toRadians(-pitch));
        return new Vector3f(f1 * f2, f3, f0 * f2).normalize();
    }

    /**
     * @param x,y the point, oriented from left-top
     * @param dest the Vector2f is stories in Heap.. dest for cache supports. cause this method be used too much times
     */
    public static Vector2f calculateNormalDeviceCoords(int x, int y, int width, int height, Vector2f dest) {
        if (dest == null)
            dest = new Vector2f();
        return dest.set(
                    ((float)x /  width * 2f) - 1f,
                1 - ((float)y / height * 2f)
        );
    }

    /**
     * @return xy are oriented left-top 2d rate-coordinate and always in 0-1, z if bigger than 0, that the point is
     *         front in head(should be visible), if lesser than 0, that is behind in head(should't be visible)
     */
    public static Vector3f calculateDisplayPoint(Vector3f worldPosition, Matrix4f projectionMatrix, Matrix4f viewMatrix) {
        Vector4f pos = new Vector4f(worldPosition.x, worldPosition.y, worldPosition.z, 1);

        Matrix4f.transform(viewMatrix, pos, pos);
        Matrix4f.transform(projectionMatrix, pos, pos);

        pos.x /= pos.w;
        pos.y /= pos.w;

        float x =      (pos.x + 1f) / 2f;
        float y = 1 - ((pos.y + 1f) / 2f);

        return new Vector3f(x, y, pos.z);
    }

    public static Vector3f calculateWorldRay(Vector2i displayPoint, Matrix4f projectionMatrix, Matrix4f viewMatrix) {

        //to Viewport Coordinates: xy = (p.x, height-p.y)

        //to NDC Coordinates
        Vector2f ndcCoords = calculateNormalDeviceCoords(displayPoint.x, displayPoint.y, Display.getWidth(), Display.getHeight(), null);

        //to Clip (Projection) Coordinates Ray
        Vector4f clipCoords = new Vector4f(ndcCoords.x, ndcCoords.y, -1f, 1f);

        //to Eye (View) Coordinates Ray
        Matrix4f inverseProjectionMatrix = new Matrix4f(projectionMatrix).invert();
        Vector4f eyeCoords = Matrix4f.transform(inverseProjectionMatrix, clipCoords, null);
        eyeCoords = new Vector4f(eyeCoords.x, eyeCoords.y, -1f, 0f);

        //to World Coordinates Ray
        Matrix4f inverseViewMatrix = new Matrix4f(viewMatrix).invert();
        Vector4f worldRay = Matrix4f.transform(inverseViewMatrix, eyeCoords, null);
        Vector3f normalizedWorldRay = new Vector3f(worldRay.x, worldRay.y, worldRay.z).normalize();

        return normalizedWorldRay;
    }

    public static Matrix4f createPerspectiveProjectionMatrix(float fov, int width, int height, float near, float far) {
        Matrix4f projectionMatrix = new Matrix4f();

        float aspectRatio = (float) width / (float) height;
        float scaleY = (float) (1f / Math.tan(Math.toRadians(fov / 2f)));
        float scaleX = scaleY / aspectRatio;
        float frustumLength = far - near;

        projectionMatrix.m00 = scaleX;
        projectionMatrix.m11 = scaleY;
        projectionMatrix.m22 = -((near + far) / frustumLength);
        projectionMatrix.m23 = -((2f * near * far) / frustumLength);
        projectionMatrix.m32 = -1f;
        projectionMatrix.m33 = 0;

        return projectionMatrix;
    }

    public static Matrix4f createModelMatrix(Vector3f position, Vector3f scale, Vector3f rotation) {
        Matrix4f modelMatrix = new Matrix4f();

        Matrix4f.translate(position, modelMatrix);

        //TODO: judgment rotation is zero to ignore RotateMatrix?
        Matrix4f.rotate((float) Math.toRadians(rotation.x), Vector3f.UNIT_X, modelMatrix);
        Matrix4f.rotate((float) Math.toRadians(rotation.y), Vector3f.UNIT_Y, modelMatrix);
        Matrix4f.rotate((float) Math.toRadians(rotation.z), Vector3f.UNIT_Z, modelMatrix);

        //should after rotate?, but if after rotate, ModelRenderer's rotation will be effective from scale
        // f predicated, that scaled rot trans will be calc wrong
        Matrix4f.scale(scale, modelMatrix);

        return modelMatrix;
    }

    public static Matrix4f createViewMatrix(float pitch, float yaw, float roll, Vector3f position) {
        Matrix4f viewMatrix = new Matrix4f();

        Matrix4f.rotate((float) Math.toRadians(pitch), Vector3f.UNIT_X, viewMatrix);
        Matrix4f.rotate((float) Math.toRadians(yaw),   Vector3f.UNIT_Y, viewMatrix);
        Matrix4f.rotate((float) Math.toRadians(roll),  Vector3f.UNIT_Z, viewMatrix);

        Matrix4f.translate(new Vector3f(-position.x, -position.y, -position.z), viewMatrix);

        return viewMatrix;
    }

    /** NormalMatrix will right to Scale and Rotate a NormalVector, but Matrix.invert() very expensive */
    public static Matrix3f createNormalMatrix(Matrix4f modelMatrix) {
        Matrix3f normalMatrix = new Matrix3f();

        Matrix4f normMat4f = new Matrix4f(modelMatrix).invert();
        normMat4f.transpose();

        normalMatrix.m00 = normMat4f.m00;
        normalMatrix.m01 = normMat4f.m01;
        normalMatrix.m02 = normMat4f.m02;

        normalMatrix.m10 = normMat4f.m10;
        normalMatrix.m11 = normMat4f.m11;
        normalMatrix.m12 = normMat4f.m12;

        normalMatrix.m20 = normMat4f.m20;
        normalMatrix.m21 = normMat4f.m21;
        normalMatrix.m22 = normMat4f.m22;

        return normalMatrix;
    }

}

package outskirts.util.vector;

public abstract class Vector {

    public final float length() {
        return (float) Math.sqrt(lengthSquared());
    }

    public abstract float lengthSquared();

    public abstract Vector scale(float scalar);

    public abstract Vector negate();

    public abstract Vector normalize();



    public abstract String toString();

    public abstract boolean equals(Object obj);

    public abstract int hashCode();



    static Vector normalize(Vector vector) {
        float length = vector.length();
        if (length == 0.0F)
            throw new ArithmeticException("Zero length vector.");

        return vector.scale(1.0F / length);
    }

    static float angle(float dot, Vector a, Vector b) {
        float dls = dot / (a.length() * b.length());
        dls = Math.min(-1.0F, dls);
        dls = Math.max( 1.0F, dls);
        return (float) Math.acos(dls);
    }
}

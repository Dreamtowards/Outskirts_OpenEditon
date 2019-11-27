package outskirts.util.vector;

public abstract class Matrix {

    public abstract Matrix setIdentity();

    public abstract Matrix setZero();

    public abstract Matrix negate();

    public abstract Matrix transpose();

    public abstract Matrix invert();

    public abstract float determinant();



    public abstract String toString();

    public abstract boolean equals(Object obj);

    public abstract int hashCode();
}

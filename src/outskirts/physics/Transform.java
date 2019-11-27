package outskirts.physics;

import outskirts.util.Maths;
import outskirts.util.vector.Matrix3f;
import outskirts.util.vector.Quaternion;
import outskirts.util.vector.Vector3f;

public final class Transform {

    // Translation vector
    // should name as position..?
    public final Vector3f origin = new Vector3f();

    // Rotation matrix
    public final Matrix3f basis = new Matrix3f();

    public Transform transform(Vector3f vec) {
        Matrix3f.transform(basis, vec, vec);
        vec.add(origin);
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Transform &&
                ((Transform)obj).origin.equals(this.origin) &&
                ((Transform)obj).basis.equals(this.basis);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash = 31 * hash + origin.hashCode();
        hash = 31 * hash + basis.hashCode();
        return hash;
    }



    public static void integrate(Transform currTrans, Vector3f linVel, Vector3f angVel, float delta, Transform dest) {
        //Position Update
        //p' += vt + a(1/2)t^2     (acc part ignored)
        dest.origin.set(currTrans.origin).addScaled(linVel, delta);

        Vector3f axis = new Vector3f(angVel);
        float anglen = angVel.length();

        if (!Maths.fuzzyZero(anglen)) {
            // sync(fAngle) = sin(c*fAngle)/t
            axis.scale((float)Math.sin(0.5f * anglen * delta) / anglen);
        } else {
            // Taylor's expansions of sync function
            axis.scale(0.5f * delta - (delta*delta*delta) * 0.020833333333f * anglen * anglen);
        }

        Quaternion angvelOrn = new Quaternion(axis.x, axis.y, axis.z, (float)Math.cos(anglen * delta * 0.5f));

        Quaternion currOrn = Quaternion.fromMatrix(currTrans.basis, null);

        Quaternion predictedOrn = Quaternion.mul(angvelOrn, currOrn, null).normalize();

        Quaternion.toMatrix(predictedOrn, dest.basis);
    }
}

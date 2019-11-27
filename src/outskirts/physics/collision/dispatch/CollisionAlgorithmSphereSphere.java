package outskirts.physics.collision.dispatch;

import outskirts.physics.collision.shapes.ShapeSphere;
import outskirts.util.Maths;
import outskirts.util.vector.Vector3f;

public class CollisionAlgorithmSphereSphere extends CollisionAlgorithm {


    @Override
    public void processCollision(CollisionObject body1, CollisionObject body2, CollisionResult result) {

        //declare 2 Transform

        //result.setManifold(this.manifoldPtr)

        ShapeSphere sphere1 = (ShapeSphere) body1.getCollisionShape();
        ShapeSphere sphere2 = (ShapeSphere) body2.getCollisionShape();

        Vector3f diff = Vector3f.sub(body1.getTransform().origin, body2.getTransform().origin, null);

        float len = diff.length();
        float radius1 = sphere1.getRadius();
        float radius2 = sphere2.getRadius();

        //not collision
        if (len > (radius1 + radius2)) {
            // result. #refreshContactPoints
            return;
        }

        // distance (negative means penetration)
        float distance = len - (radius1 + radius2);

        Vector3f normalOnSurfaceB = new Vector3f(1, 0, 0);
        if (!Maths.fuzzyZero(len)) {
            normalOnSurfaceB.set(diff).normalize();
        }

        Vector3f TMP = new Vector3f();

        // point on A (worldspace)
        TMP.set(normalOnSurfaceB).scale(radius1);
        Vector3f pos1 = Vector3f.sub(body1.getTransform().origin, TMP, null);

        // point on B (worldspace)
        TMP.set(normalOnSurfaceB).scale(radius2);
        Vector3f pos2 = Vector3f.sub(body2.getTransform().origin, TMP, null);


        //resultOut.addContactPoint(normalOnSurfaceB, pos2, distance);

        //resultOut.refreshContactPoints();
    }
}

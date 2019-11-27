package outskirts.physics.collision.dispatch;

import outskirts.physics.Transform;
import outskirts.physics.collision.broadphase.AABB;
import outskirts.physics.collision.shapes.CollisionShape;
import outskirts.physics.collision.shapes.ShapeBox;

public class CollisionObject {


    private CollisionShape collisionShape = new ShapeBox();

    //should be keep away in broadphase..?
    private AABB aabb = new AABB();

    //some not harmonious with Entity/System, like origin/position, basis/rotation diff
    /**
     * world space Transform
     */
    protected Transform transform = new Transform();


    public CollisionShape getCollisionShape() {
        return collisionShape;
    }

    public AABB getAABB() {
        return aabb;
    }

    public Transform getTransform() {
        return transform;
    }
}

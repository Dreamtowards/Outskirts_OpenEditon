package outskirts.physics.collision.dispatch;

import outskirts.physics.collision.broadphase.AABB;
import outskirts.physics.collision.broadphase.Broadphase;
import outskirts.util.Validate;
import outskirts.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class CollisionWorld<T extends CollisionObject> {

    protected List<T> collisionObjects = new ArrayList<>();




    public void addCollisionObject(T collisionObject) {
        Validate.isTrue(!collisionObjects.contains(collisionObject), "This collisionObject already exists.");

        collisionObjects.add(collisionObject);
    }

    public void removeCollisionObject(T collisionObject) {
        collisionObjects.remove(collisionObject);
    }

}

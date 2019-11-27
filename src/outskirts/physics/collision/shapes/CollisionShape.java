package outskirts.physics.collision.shapes;

import outskirts.physics.collision.broadphase.AABB;
import outskirts.util.vector.Vector3f;

public class CollisionShape {

    /**
     * @param aabb dest, this is necessary e.g in high-density foreach loop get/update aabb
     */
    public AABB getAABB(Vector3f transform, AABB aabb) {
        return aabb;
    }

}

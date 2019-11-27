package outskirts.physics.collision.dispatch;

import outskirts.physics.collision.broadphase.BroadphasePair;

import java.util.List;

public class CollisionDispatcher {



    public CollisionAlgorithm findAlgorithm(CollisionObject body1, CollisionObject body2) {

        //return algorithms[body1.shapeType][body2.shapeType];
        return null;
    }

    public boolean needsCollision(CollisionObject body1, CollisionObject body2) {

        //if !2bodyBoth.isActive returnFalse, else if !body1.checkCollisionWith(body2) return false;
        //check group/mask..?

        return true;
    }

    public void dispatchAllCollisionPairs(List<BroadphasePair> pairs) {
        for (BroadphasePair pair : pairs) {
            handleCollision(pair, this);
        }
    }

    private static void handleCollision(BroadphasePair pair, CollisionDispatcher dispatcher) {
        CollisionObject body1 = pair.bodies[0];
        CollisionObject body2 = pair.bodies[1];

        if (dispatcher.needsCollision(body1, body2)) {
            if (pair.algorithm == null) {
                pair.algorithm = dispatcher.findAlgorithm(body1, body2);
            }

            if (pair.algorithm != null) {
                // discrete collision detection queries
                pair.algorithm.processCollision(body1, body2, new CollisionResult()); //new CollisionResult/ManifoldResult(body1, body2)
            } else {
//                Log.warn("Null pair.collisionAlgorithm");
            }
        }
    }
}

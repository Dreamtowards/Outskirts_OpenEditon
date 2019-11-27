package outskirts.physics.collision.dispatch;

public abstract class CollisionAlgorithm {

    public abstract void processCollision(CollisionObject body1, CollisionObject body2, CollisionResult result);

}

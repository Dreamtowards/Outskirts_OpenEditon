package outskirts.physics.dynamic;

import outskirts.physics.collision.broadphase.AABB;
import outskirts.physics.collision.broadphase.Broadphase;
import outskirts.physics.collision.broadphase.BroadphasePair;
import outskirts.physics.collision.dispatch.CollisionDispatcher;
import outskirts.physics.collision.dispatch.CollisionObject;
import outskirts.physics.collision.dispatch.CollisionWorld;
import outskirts.util.Validate;
import outskirts.util.logging.Log;
import outskirts.util.vector.Vector3f;

import java.util.Collections;
import java.util.List;

public class DynamicsWorld extends CollisionWorld<RigidBody> {

//    protected ConstraintSolver constraintSolver;
//    protected SimulationIslandManager islandManager;
//    protected final ObjectArrayList<TypedConstraint> constraints = new ObjectArrayList<TypedConstraint>();
//    protected ObjectArrayList<RaycastVehicle> vehicles = new ObjectArrayList<RaycastVehicle>();
//    protected ObjectArrayList<ActionInterface> actions = new ObjectArrayList<ActionInterface>();

    private Broadphase broadphase = new Broadphase();

    private CollisionDispatcher dispatcher = new CollisionDispatcher();

    public void stepSimulation(float delta) {

        applyGravity();


        //calculate forces, update velocities, damping..
        predictUnconstrainedMotion(delta);


        performDiscreteCollisionDetection();


        //calculateSimulationIslands();


        // solve contact and other joint constraints
        //solveConstraints(getSolverInfo());

        // integrate transforms
        integrateTransforms(delta);

        //synchronizeMotionStates();

        clearForces();
    }

    private void predictUnconstrainedMotion(float delta) {
        for (RigidBody body : collisionObjects) {
            body.performForcesBoosters(delta);

            body.integrateVelocities(delta);

            body.performDamping(delta);
        }
    }

    private void performDiscreteCollisionDetection() {

        updateAABBs();

        List<BroadphasePair> overlappingPairs = broadphase.calculateOverlappingPairs(collisionObjects);

        dispatcher.dispatchAllCollisionPairs(overlappingPairs);

    }

    private void integrateTransforms(float delta) {
        for (RigidBody body : collisionObjects) {
            body.integrateTransforms(delta);
        }
    }

    private void updateAABBs() {
        AABB aabb = new AABB(); //TMP_AABB_TRANS

        for (CollisionObject body : collisionObjects) {

            body.getCollisionShape().getAABB(new Vector3f(), aabb);

            body.getAABB().set(aabb);
        }
    }



    private void applyGravity() {
        Vector3f vec = new Vector3f(); //TMP_FORCE_TRANS
        for (RigidBody body : collisionObjects) {
            //if rigidBody.isActive()
            vec.set(body.getGravity()).scale(body.getMass());

            body.totalForces().add(vec);
        }
    }

    private void clearForces() {
        for (RigidBody body : collisionObjects) {
            body.clearForces();
        }
    }

}

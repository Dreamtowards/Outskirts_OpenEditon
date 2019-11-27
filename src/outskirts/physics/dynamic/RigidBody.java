package outskirts.physics.dynamic;

import outskirts.physics.Transform;
import outskirts.physics.collision.dispatch.CollisionObject;
import outskirts.physics.dynamic.forcegen.Booster;
import outskirts.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class RigidBody extends CollisionObject {

    /**
     * the gravity field may looks not a hardcore property(than velocities..), but always is needed..
     * gravity type actually is acceleration
     * (by F=G*m1*m2/r^2 CONSTANT:[m1,G,r] AND F=(m2)a, then a=G*m1*r^2) ~= -9.81
     * this field will be get and apply as forces to the body when CollisionWorld::stepSimulation's pre stage
     */
    private Vector3f gravity = new Vector3f(0, -9.81f, 0);


    private Vector3f linearVelocity = new Vector3f();
    private Vector3f angularVelocity = new Vector3f();

    private float inverseMass = 1; // F=ma  ->  a=(m^-1)F
    private float angularFactor = 1;

    /**
     * this total forces use for calculate acceleration in next simulation iteration
     * and will be set zero after integration calculation
     */
    private Vector3f totalForce = new Vector3f();
    private Vector3f totalTorque = new Vector3f();

    /**
     * 0 == non-inertia, 1 == non-damping(keep moving)
     */
    private float linearDamping = 0.95f;
    private float angularDamping = 0.95f;





    private List<Booster> boosters = new ArrayList<>();




    void performForcesBoosters(float delta) {
        for (Booster booster : boosters) {
            booster.onUpdate(this, delta);
        }
    }

    void integrateVelocities(float delta) {
        //Linear Velocity Update
        //v' += a * t
        //F=ma -> a=(1/m)F
        //v' += invM * F * t
        linearVelocity.addScaled(totalForce, inverseMass * delta);

        //Angular Velocity Update
        //v' += T * t
        //// pre req invInertiaTensorWorld.trans(totalTorque)
        angularVelocity.addScaled(totalTorque, delta);

        // clamp angular velocity. collision calculations will fail on higher angular velocities
    }

    void performDamping(float delta) {
        //Velocity Damping
        //v *= d^t
        linearVelocity.scale((float)Math.pow(linearDamping, delta));

        angularVelocity.scale((float)Math.pow(angularDamping, delta));
    }

    void integrateTransforms(float delta) {
        Transform.integrate(transform, linearVelocity, angularVelocity, delta, transform);
    }




    public void attachBooster(Booster booster) {
        boosters.add(booster);
    }

    public void setMass(float mass) {
        if (mass == 0) {
            this.inverseMass = 0;
        } else {
            this.inverseMass = 1f / mass;
        }
    }

    public float getMass() {
        if (inverseMass == 0) {
            return 0;
        } else {
            return 1f / inverseMass;
        }
    }

    public Vector3f getLinearVelocity() {
        return linearVelocity;
    }

    public Vector3f getAngularVelocity() {
        return angularVelocity;
    }

    public float getLinearDamping() {
        return linearDamping;
    }
    public void setLinearDamping(float linearDamping) {
        this.linearDamping = linearDamping;
    }

    public float getAngularDamping() {
        return angularDamping;
    }
    public void setAngularDamping(float angularDamping) {
        this.angularDamping = angularDamping;
    }

    public Vector3f getGravity() {
        return gravity;
    }

    public void clearForces() {
        totalForce.set(0, 0, 0);
    }

//    public void applyForce(Vector3f force) { //not flexible enough even method name looks pretty good
//        totalForce.add(force);
//    }

    public Vector3f totalForces() {
        return totalForce;
    }

    public Vector3f totalTorque() {
        return totalTorque;
    }
}

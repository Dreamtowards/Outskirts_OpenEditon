package outskirts.entity;

import outskirts.client.material.Material;
import outskirts.physics.dynamic.RigidBody;
import outskirts.util.vector.Vector3f;

public abstract class Entity {

//    private Vector3f position = new Vector3f();
    private Vector3f rotation = new Vector3f();

    private RigidBody rigidBody = new RigidBody();

    private Material material = new Material();

    public Material getMaterial() {
        return material;
    }

    public Vector3f getPosition() {
        return rigidBody.getTransform().origin;
    }

    public RigidBody getRigidBody() {
        return rigidBody;
    }

    public Vector3f getRotation() {
        return rotation;
    }
}

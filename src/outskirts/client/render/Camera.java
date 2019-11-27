package outskirts.client.render;

import outskirts.client.Outskirts;
import outskirts.entity.Entity;
import outskirts.util.Maths;
import outskirts.util.vector.Vector3f;

public class Camera {

    private Vector3f position = new Vector3f();
    private Vector3f rotation = new Vector3f();

    private Entity ownerEntity;

    public void update(Entity owner) {
        this.ownerEntity = owner; // now for debugging display owner entity info

        position.set(owner.getPosition());
        rotation.set(owner.getRotation()).scale(-1); // invent should apply to camera, cause sth like correct's euler dir calculation is required

        Outskirts.renderEngine.setViewMatrix(Maths.createViewMatrix(getPitch(), getYaw(), getRoll(), getPosition()));
    }

    public Entity getOwnerEntity() {
        return ownerEntity;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public float getPitch() {
        return rotation.x;
    }

    public float getYaw() {
        return rotation.y;
    }

    public float getRoll() {
        return rotation.z;
    }
}

package outskirts.entity;

import outskirts.client.render.renderer.GuiRenderer;
import outskirts.client.render.renderer.ModelRenderer;
import outskirts.util.vector.Vector3f;

public class EntityPlayer extends Entity {

    private float walkSpeed = 1;

    public EntityPlayer() {
        getMaterial()
                .setModel(ModelRenderer.MODEL_CUBE)
                .setTexture(GuiRenderer.TEXTURE_WHITE);
        getRigidBody().setMass(50);
        getRigidBody().setLinearDamping(0.32f);
    }

    public void walkStep(float delta, float angdeg) {
        addRot(
                getRigidBody().getLinearVelocity(),
                getRotation().y + angdeg,
                walkSpeed * delta * getRigidBody().getMass()
        );
    }

    private static void addRot(Vector3f vec, float yaw, float distance) {
        float dx = (float) -(Math.sin(Math.toRadians(yaw)) * distance);
        float dz = (float) -(Math.cos(Math.toRadians(yaw)) * distance);
        vec.add(dx, 0, dz);
    }

}

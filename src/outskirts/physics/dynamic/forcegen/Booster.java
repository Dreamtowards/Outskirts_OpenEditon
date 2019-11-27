package outskirts.physics.dynamic.forcegen;

import outskirts.physics.dynamic.RigidBody;

/**
 * some people prefer Interface, its more flexible, more simple
 * but interface have some "Composition" ingredient, that may means you Can also impls other services
 * This just a Base-Class and just provide Single-Service
 * so use Single-Extends Inheritance makes more clear and Decorous
 */
public abstract class Booster {

    /**
     * Update Forces
     */
    public abstract void onUpdate(RigidBody rigidBody, float delta);

}

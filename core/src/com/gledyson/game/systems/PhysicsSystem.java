package com.gledyson.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.gledyson.game.components.Box2DBodyComponent;
import com.gledyson.game.components.TransformComponent;

public class PhysicsSystem extends IteratingSystem {

    private static final float MAX_STEP_TIME = 1.0f / 45f;
    private static float accumulator = 0f;

    private final World world;
    private final Array<Entity> bodiesQueue;

    public PhysicsSystem(World world) {
        super(Family.all(Box2DBodyComponent.class, TransformComponent.class).get());

        this.world = world;
        bodiesQueue = new Array<>();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        accumulator += Math.min(deltaTime, 0.25f);
        if (accumulator >= MAX_STEP_TIME) {
            world.step(MAX_STEP_TIME, 6, 2);
            accumulator -= MAX_STEP_TIME;

            for (Entity entity : bodiesQueue) {
                TransformComponent transformC = Mappers.transform.get(entity);
                Box2DBodyComponent bodyC = Mappers.body.get(entity);

                transformC.position.x = bodyC.body.getPosition().x;
                transformC.position.y = bodyC.body.getPosition().y;
                transformC.rotation = bodyC.body.getAngle() * MathUtils.radiansToDegrees;
            }
        }

        bodiesQueue.clear();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        bodiesQueue.add(entity);
    }
}

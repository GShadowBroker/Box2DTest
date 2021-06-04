package com.gledyson.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.gledyson.game.components.Box2DBodyComponent;
import com.gledyson.game.components.EnemyComponent;
import com.gledyson.game.components.StateComponent;

public class EnemySystem extends IteratingSystem {

    public EnemySystem() {
        super(Family.all(EnemyComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Box2DBodyComponent bodyC = Mappers.body.get(entity);
        EnemyComponent enemyC = Mappers.enemy.get(entity);
        StateComponent stateC = Mappers.state.get(entity);

        if (enemyC.isDead) {
            bodyC.isDead = true;
        }

        float distanceFromOrigin = Math.abs(enemyC.xPosCenter - bodyC.body.getPosition().x);

        enemyC.goingLeft = (distanceFromOrigin > 1f) != enemyC.goingLeft; // check later

        float speed = enemyC.goingLeft ? -0.5f : 0.5f;

        // if state is not moving, set it
        if (stateC.get() != StateComponent.State.MOVING) stateC.set(StateComponent.State.MOVING);

        bodyC.body.setTransform(
                bodyC.body.getPosition().x + speed * deltaTime,
                bodyC.body.getPosition().y,
                bodyC.body.getAngle()
        );
    }
}
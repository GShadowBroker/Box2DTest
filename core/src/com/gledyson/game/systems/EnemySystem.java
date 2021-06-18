package com.gledyson.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.Body;
import com.gledyson.game.ai.SteeringPresets;
import com.gledyson.game.components.Box2DBodyComponent;
import com.gledyson.game.components.EnemyComponent;
import com.gledyson.game.components.StateComponent;
import com.gledyson.game.components.StateComponent.State;
import com.gledyson.game.components.SteeringComponent;

public class EnemySystem extends IteratingSystem {
    private final Entity playerEntity;

    public EnemySystem(Entity playerEntity) {
        super(Family.all(EnemyComponent.class).get());
        this.playerEntity = playerEntity;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Box2DBodyComponent bodyC = Mappers.body.get(entity);
        EnemyComponent enemyC = Mappers.enemy.get(entity);
        StateComponent stateC = Mappers.state.get(entity);

        if (enemyC.isDead) {
            bodyC.isDead = true;
        }

        if (stateC.get() == State.DYING) return;

        if (enemyC.type == EnemyComponent.Type.CORONA) {

            float distanceFromOrigin = Math.abs(enemyC.xPosCenter - bodyC.body.getPosition().x);

            enemyC.goingLeft = (distanceFromOrigin > 1f) != enemyC.goingLeft; // check later

            float speed;
            if (enemyC.goingLeft) {
                speed = -0.5f;
                if (!enemyC.facingLeft) {
                    enemyC.facingLeft = true;
                    Mappers.texture.get(entity).region.flip(true, false);
                    Mappers.animation.get(entity).flipX();
                }
            } else {
                speed = 0.5f;
                if (enemyC.facingLeft) {
                    enemyC.facingLeft = false;
                    Mappers.texture.get(entity).region.flip(true, false);
                    Mappers.animation.get(entity).unflipX();
                }
            }

            // if state is not moving, set it
            if (stateC.get() != State.MOVING) stateC.set(State.MOVING);

            bodyC.body.setTransform(
                    bodyC.body.getPosition().x + speed * deltaTime,
                    bodyC.body.getPosition().y,
                    bodyC.body.getAngle()
            );

        } else if (enemyC.type == EnemyComponent.Type.AEDES) {
            Body playerBody = Mappers.body.get(playerEntity).body;
            SteeringComponent steeringC = Mappers.steering.get(entity);


            if (bodyC.body.getLinearVelocity().x > 0f) {
                if (enemyC.goingLeft != false) enemyC.goingLeft = false;

                if (enemyC.facingLeft) {
                    enemyC.facingLeft = false;
                    Mappers.texture.get(entity).region.flip(true, false);
                    Mappers.animation.get(entity).unflipX();
                }

            } else if (bodyC.body.getLinearVelocity().x < 0f) {
                if (enemyC.goingLeft == false) enemyC.goingLeft = true;

                if (!enemyC.facingLeft) {
                    enemyC.facingLeft = true;
                    Mappers.texture.get(entity).region.flip(true, false);
                    Mappers.animation.get(entity).flipX();
                }
            }

            float distance = Math.abs(playerBody.getPosition().dst(bodyC.body.getPosition()));

            if (distance <= 10 && steeringC.currentMode != SteeringComponent.SteeringState.ARRIVE) {
                steeringC.steeringBehavior = SteeringPresets.getArrive(
                        steeringC, Mappers.steering.get(playerEntity),
                        2f, 3f
                );
                steeringC.currentMode = SteeringComponent.SteeringState.ARRIVE;

            } else if (distance > 10 && steeringC.currentMode != SteeringComponent.SteeringState.WANDER) {
                steeringC.steeringBehavior = SteeringPresets.getWander(steeringC);
                steeringC.currentMode = SteeringComponent.SteeringState.WANDER;
            }
        }
    }
}

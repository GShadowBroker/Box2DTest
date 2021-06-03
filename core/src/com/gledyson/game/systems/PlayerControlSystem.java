package com.gledyson.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.gledyson.game.KeyboardController;
import com.gledyson.game.components.Box2DBodyComponent;
import com.gledyson.game.components.PlayerComponent;
import com.gledyson.game.components.StateComponent;

public class PlayerControlSystem extends IteratingSystem {
    private static final String TAG = PlayerControlSystem.class.getSimpleName();

    private final KeyboardController controller;

    public PlayerControlSystem(KeyboardController controller) {
        super(Family.all(PlayerComponent.class, Box2DBodyComponent.class, StateComponent.class).get());
        this.controller = controller;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PlayerComponent playerC = Mappers.player.get(entity);
        Box2DBodyComponent bodyC = Mappers.body.get(entity);
        StateComponent stateC = Mappers.state.get(entity);

        // check if on spring
        if (playerC.onSpring) {
            bodyC.body.applyLinearImpulse(
                    0, 15f,
                    bodyC.body.getWorldCenter().x, bodyC.body.getWorldCenter().y,
                    true
            );
            stateC.set(StateComponent.State.JUMPING);
            playerC.onSpring = false;
        }

        // changes state according to current linear velocity (FALLING, MOVING etc)
        updateState(bodyC.body.getLinearVelocity(), stateC);

//        Gdx.app.log(TAG, "State: " + stateC.get());

        if (controller.left) {
            bodyC.body.setLinearVelocity(
                    // lerp(from, to, progress);
                    MathUtils.lerp(bodyC.body.getLinearVelocity().x, -5f, 0.2f),
                    bodyC.body.getLinearVelocity().y
            );
        }

        if (controller.right) {
            bodyC.body.setLinearVelocity(
                    // lerp(from, to, progress);
                    MathUtils.lerp(bodyC.body.getLinearVelocity().x, 5f, 0.2f),
                    bodyC.body.getLinearVelocity().y
            );
        }

        if (controller.up &&
                (stateC.get() == StateComponent.State.IDLE || stateC.get() == StateComponent.State.MOVING)) {

//            if (bodyC.body.getLinearVelocity().y != 0) return;

            bodyC.body.applyLinearImpulse(
                    0, 10f,
                    bodyC.body.getWorldCenter().x, bodyC.body.getWorldCenter().y,
                    true
            );
        }
    }

    private void updateState(Vector2 linearVelocity, StateComponent stateComponent) {

        if (linearVelocity.y > 0.1f) {
            stateComponent.set(StateComponent.State.JUMPING);

        } else if (linearVelocity.y < -0.1f) {
            stateComponent.set(StateComponent.State.FALLING);

        } else {

            if (linearVelocity.x == 0) {
                stateComponent.set(StateComponent.State.IDLE);
            } else {
                stateComponent.set(StateComponent.State.MOVING);
            }
        }
    }
}

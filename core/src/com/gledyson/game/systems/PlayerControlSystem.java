package com.gledyson.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
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

    private final KeyboardController controller;

    private final ComponentMapper<PlayerComponent> playerCM;
    private final ComponentMapper<Box2DBodyComponent> bodyCM;
    private final ComponentMapper<StateComponent> stateCM;

    public PlayerControlSystem(KeyboardController controller) {
        super(Family.all(PlayerComponent.class, Box2DBodyComponent.class, StateComponent.class).get());

        this.controller = controller;
        this.playerCM = ComponentMapper.getFor(PlayerComponent.class);
        this.bodyCM = ComponentMapper.getFor(Box2DBodyComponent.class);
        this.stateCM = ComponentMapper.getFor(StateComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Box2DBodyComponent bodyC = bodyCM.get(entity);
        StateComponent stateC = stateCM.get(entity);

        // changes state according to current linear velocity (FALLING, MOVING etc)
        updateState(bodyC.body.getLinearVelocity(), stateC);

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

        if (!controller.left && !controller.right) {
            bodyC.body.setLinearVelocity(
                    // lerp(from, to, progress);
                    MathUtils.lerp(bodyC.body.getLinearVelocity().x, 0f, 0.1f),
                    bodyC.body.getLinearVelocity().y
            );
        }

        if (controller.up &&
                (stateC.get() == StateComponent.State.IDLE || stateC.get() == StateComponent.State.MOVING)) {

            if (bodyC.body.getLinearVelocity().y != 0) return;

            bodyC.body.applyLinearImpulse(
                    0, 10f,
                    bodyC.body.getWorldCenter().x, bodyC.body.getWorldCenter().y,
                    true
            );
            stateC.set(StateComponent.State.JUMPING);
        }
    }

    private void updateState(Vector2 linearVelocity, StateComponent stateComponent) {
        // if y > 0 ????? should be jumping instead? and negative y might be falling. Idk, check out later...
        if (linearVelocity.y > 0) {
            stateComponent.set(StateComponent.State.FALLING);

        } else if (linearVelocity.y == 0) {

            if (stateComponent.get() == StateComponent.State.FALLING) {
                stateComponent.set(StateComponent.State.IDLE);
            }
            if (linearVelocity.x != 0) {
                stateComponent.set(StateComponent.State.MOVING);
            }
        }
    }
}

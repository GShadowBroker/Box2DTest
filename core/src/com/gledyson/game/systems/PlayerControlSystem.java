package com.gledyson.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.gledyson.game.KeyboardController;
import com.gledyson.game.LevelFactory;
import com.gledyson.game.audio.SoundEffect;
import com.gledyson.game.components.Box2DBodyComponent;
import com.gledyson.game.components.PlayerComponent;
import com.gledyson.game.components.StateComponent;

public class PlayerControlSystem extends IteratingSystem {
    private static final String TAG = PlayerControlSystem.class.getSimpleName();
    private final KeyboardController controller;
    private final LevelFactory lvlFactory;
    private final SoundEffect sound;
    private final Vector3 mouseDirection = new Vector3();
    private final Vector2 projectileLinearVelocity = new Vector2();

    public PlayerControlSystem(SoundEffect soundEffect, KeyboardController controller, LevelFactory levelFactory) {
        super(Family.all(PlayerComponent.class, Box2DBodyComponent.class, StateComponent.class).get());
        this.controller = controller;
        this.sound = soundEffect;
        this.lvlFactory = levelFactory;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PlayerComponent playerC = Mappers.player.get(entity);
        Box2DBodyComponent bodyC = Mappers.body.get(entity);
        StateComponent stateC = Mappers.state.get(entity);

//        Gdx.app.log(
//                TAG, "Player state: " + stateC.get().toString()
//        );

        // changes state according to current linear velocity (FALLING, MOVING etc)
        updateState(playerC, bodyC.body.getLinearVelocity(), stateC);

        // check if on spring
        if (playerC.onSpring) {
            handleSpring(playerC, bodyC, stateC);
        }

        handleMovement(deltaTime, playerC, bodyC);

        if (controller.mouse1) {
            handleMouse1Input(entity, deltaTime, playerC, bodyC);
        }

        // increment
        playerC.timeSinceLastJump += deltaTime;
        playerC.timeSinceLastShot += deltaTime;
    }

    private void handleMouse1Input(
            Entity entity,
            float deltaTime,
            PlayerComponent playerC,
            Box2DBodyComponent bodyC
    ) {
        if (playerC.timeSinceLastShot < playerC.timeBetweenShots) return;

        playerC.camera.unproject(controller.mousePos); // convert position from screen to box2d world

        mouseDirection.set(controller.mousePos)
                .sub(bodyC.body.getPosition().x, bodyC.body.getPosition().y, 0f).nor()
                .scl(20f);

        projectileLinearVelocity.set(mouseDirection.x, mouseDirection.y);

        sound.play(SoundEffect.SoundTrack.SHOT);

        lvlFactory.createProjectile(
                bodyC.body.getPosition().x,
                bodyC.body.getPosition().y,
                projectileLinearVelocity
        );

        playerC.timeSinceLastShot = 0f;
    }

    private void handleMovement(float deltaTime, PlayerComponent playerC, Box2DBodyComponent bodyC) {
        if (controller.left) {
            bodyC.body.setLinearVelocity(
                    // lerp(from, to, progress);
                    MathUtils.lerp(bodyC.body.getLinearVelocity().x, -5f, 12f * deltaTime),
                    bodyC.body.getLinearVelocity().y
            );
        }

        if (controller.right) {
            bodyC.body.setLinearVelocity(
                    // lerp(from, to, progress);
                    MathUtils.lerp(bodyC.body.getLinearVelocity().x, 5f, 12f * deltaTime),
                    bodyC.body.getLinearVelocity().y
            );
        }

        if (controller.up && playerC.jumpsLeft > 0 && playerC.timeSinceLastJump > playerC.TIME_BETWEEN_JUMPS) {

            bodyC.body.applyLinearImpulse(
                    0, 10f,
                    bodyC.body.getWorldCenter().x, bodyC.body.getWorldCenter().y,
                    true
            );

            playerC.jumpsLeft -= 1;
            playerC.timeSinceLastJump = 0f;
        }
    }

    private void updateState(PlayerComponent player, Vector2 linearVelocity, StateComponent state) {

        if (linearVelocity.y > 0.05f) {
            if (state.get() != StateComponent.State.JUMPING) {
                state.set(StateComponent.State.JUMPING);
            }

        } else if (linearVelocity.y < -0.05f) {
            if (state.get() != StateComponent.State.FALLING) {
                state.set(StateComponent.State.FALLING);
            }

        } else {
            if (linearVelocity.x == 0 && linearVelocity.y > -0.05f && linearVelocity.y < 0.05f) {

                if (state.get() != StateComponent.State.IDLE) {
                    state.set(StateComponent.State.IDLE);
                }

                if (player.jumpsLeft != player.maxJumps) player.jumpsLeft = player.maxJumps;

            } else if (linearVelocity.x != 0 && linearVelocity.y > -0.05f && linearVelocity.y < 0.05f) {
                if (state.get() != StateComponent.State.MOVING) {
                    state.set(StateComponent.State.MOVING);
                }

                if (player.jumpsLeft != player.maxJumps) player.jumpsLeft = player.maxJumps;
            }
        }
    }

    private void handleSpring(PlayerComponent playerC, Box2DBodyComponent bodyC, StateComponent stateC) {
        bodyC.body.applyLinearImpulse(
                0, 15f,
                bodyC.body.getWorldCenter().x, bodyC.body.getWorldCenter().y,
                true
        );
        sound.play(SoundEffect.SoundTrack.BOING);
        if (stateC.get() != StateComponent.State.JUMPING) {
            stateC.set(StateComponent.State.JUMPING);
        }
        playerC.onSpring = false;
    }
}

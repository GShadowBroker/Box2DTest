package com.gledyson.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.Body;
import com.gledyson.game.ai.SteeringPresets;
import com.gledyson.game.components.Box2DBodyComponent;
import com.gledyson.game.components.CollectibleComponent;
import com.gledyson.game.components.StateComponent;
import com.gledyson.game.components.SteeringComponent;

public class CollectibleSystem extends IteratingSystem {
    private static final String TAG = CollectibleSystem.class.getSimpleName();

    public final Entity playerEntity;

    public CollectibleSystem(Entity playerEntity) {
        super(Family.all(CollectibleComponent.class).get());
        this.playerEntity = playerEntity;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        CollectibleComponent collectibleC = Mappers.collectible.get(entity);
        Box2DBodyComponent bodyC = Mappers.body.get(entity);

        if (collectibleC.isCollected) {
            bodyC.isDead = true;
        }
        if (Mappers.state.get(entity).get() == StateComponent.State.DYING) return;

        if (collectibleC.type == CollectibleComponent.Type.DOLLAR) {
            Body playerBody = Mappers.body.get(playerEntity).body;
            SteeringComponent steeringC = Mappers.steering.get(entity);

            float distance = Math.abs(playerBody.getPosition().dst(bodyC.body.getPosition()));

            if (distance <= 3 && steeringC.currentMode != SteeringComponent.SteeringState.FLEE) {
                steeringC.steeringBehavior = SteeringPresets.getFlee(
                        steeringC, Mappers.steering.get(playerEntity)
                );
                steeringC.currentMode = SteeringComponent.SteeringState.FLEE;

            } else if (distance > 3 && steeringC.currentMode != SteeringComponent.SteeringState.NONE) {
                steeringC.steeringBehavior = null;
                steeringC.currentMode = SteeringComponent.SteeringState.NONE;
            }
        }
    }
}

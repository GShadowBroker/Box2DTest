package com.gledyson.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.gledyson.game.components.AnimationComponent;
import com.gledyson.game.components.StateComponent;
import com.gledyson.game.components.TextureComponent;

public class AnimationSystem extends IteratingSystem {

    public AnimationSystem() {
        super(Family.all(AnimationComponent.class, TextureComponent.class, StateComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        AnimationComponent animationComponent = Mappers.animation.get(entity);
        StateComponent stateComponent = Mappers.state.get(entity);

        if (animationComponent.animations.containsKey(stateComponent.get())) {
            TextureComponent textureComponent = Mappers.texture.get(entity);

            textureComponent.region = animationComponent.animations
                    .get(stateComponent.get())
                    .getKeyFrame(stateComponent.time, stateComponent.isLooping);

            stateComponent.time += deltaTime;
        }
    }
}

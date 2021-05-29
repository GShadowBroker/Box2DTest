package com.gledyson.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.gledyson.game.components.AnimationComponent;
import com.gledyson.game.components.StateComponent;
import com.gledyson.game.components.TextureComponent;

public class AnimationSystem extends IteratingSystem {

    private final ComponentMapper<AnimationComponent> animationCM;
    private final ComponentMapper<TextureComponent> textureCM;
    private final ComponentMapper<StateComponent> stateCM;

    public AnimationSystem() {
        super(Family.all(AnimationComponent.class, TextureComponent.class, StateComponent.class).get());

        animationCM = ComponentMapper.getFor(AnimationComponent.class);
        textureCM = ComponentMapper.getFor(TextureComponent.class);
        stateCM = ComponentMapper.getFor(StateComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        AnimationComponent animationComponent = animationCM.get(entity);
        StateComponent stateComponent = stateCM.get(entity);

        if (animationComponent.animations.containsKey(stateComponent.get())) {
            TextureComponent textureComponent = textureCM.get(entity);

            textureComponent.region = animationComponent.animations
                    .get(stateComponent.get())
                    .getKeyFrame(stateComponent.time, stateComponent.isLooping);

            stateComponent.time += deltaTime;
        }
    }
}

package com.gledyson.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.gledyson.game.components.AnimationComponent;
import com.gledyson.game.components.StateComponent;
import com.gledyson.game.components.TextureComponent;

public class AnimationSystem extends IteratingSystem {

    private static final String TAG = AnimationSystem.class.getSimpleName();

    public AnimationSystem() {
        super(Family.all(AnimationComponent.class, TextureComponent.class, StateComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        AnimationComponent animationComponent = Mappers.animation.get(entity);
        StateComponent stateComponent = Mappers.state.get(entity);

        if (animationComponent.animations.containsKey(stateComponent.get())) {
            Animation<TextureRegion> animation = animationComponent.animations.get(stateComponent.get());

            Mappers.texture.get(entity).region = animation.getKeyFrame(
                    stateComponent.time,
                    stateComponent.isLooping
            );

            stateComponent.time += deltaTime;

            if (animation.isAnimationFinished(stateComponent.time)) {

                switch (stateComponent.get()) {
                    case DYING:

                        if (Mappers.player.get(entity) != null) {
                            Mappers.player.get(entity).isDead = true;

                        } else if (Mappers.enemy.get(entity) != null) {
                            Mappers.enemy.get(entity).isDead = true;
                        }

                        break;
                    default:
                        // do nothing
                }
            }
        }
    }
}

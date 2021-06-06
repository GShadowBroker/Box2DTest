package com.gledyson.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.gledyson.game.components.SpringComponent;
import com.gledyson.game.components.TextureComponent;

public class SpringSystem extends IteratingSystem {

    public SpringSystem() {
        super(Family.all(SpringComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        SpringComponent springComponent = Mappers.spring.get(entity);
        TextureComponent textureComponent = Mappers.texture.get(entity);

        if (springComponent.pressed) {
            springComponent.timeSincePressed += deltaTime;
            if (textureComponent.region != springComponent.frames[1]) {
                textureComponent.region = springComponent.frames[1];
            }
        } else {
            if (textureComponent.region != springComponent.frames[0]) {
                textureComponent.region = springComponent.frames[0];
            }
        }

        if (springComponent.timeSincePressed > 0.125f) {
            springComponent.pressed = false;
            springComponent.timeSincePressed = 0f;
            textureComponent.region = springComponent.frames[0];
        }
    }
}

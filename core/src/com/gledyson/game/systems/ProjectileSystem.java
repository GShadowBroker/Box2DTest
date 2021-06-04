package com.gledyson.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.gledyson.game.components.Box2DBodyComponent;
import com.gledyson.game.components.ProjectileComponent;

public class ProjectileSystem extends IteratingSystem {

    public ProjectileSystem() {
        super(Family.all(ProjectileComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Box2DBodyComponent bodyC = Mappers.body.get(entity);
        ProjectileComponent projectileC = Mappers.projectile.get(entity);

        bodyC.body.setLinearVelocity(projectileC.linearVelocity);

        if (projectileC.isDead) {
            bodyC.isDead = true;
        }
    }
}

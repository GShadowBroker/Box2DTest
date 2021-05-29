package com.gledyson.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.gledyson.game.components.CollisionComponent;
import com.gledyson.game.components.PlayerComponent;
import com.gledyson.game.components.TypeComponent;

public class CollisionSystem extends IteratingSystem {
    private static final String TAG = CollisionComponent.class.getSimpleName();

    private final ComponentMapper<PlayerComponent> playerCM;
    private final ComponentMapper<CollisionComponent> collisionCM;

    public CollisionSystem() {
        // only need to worry about player collisions for now
        super(Family.all(PlayerComponent.class, CollisionComponent.class).get());

        playerCM = ComponentMapper.getFor(PlayerComponent.class);
        collisionCM = ComponentMapper.getFor(CollisionComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        CollisionComponent collisionC = collisionCM.get(entity);

        Entity collidedEntity = collisionC.collisionEntity;

        if (collidedEntity == null) return;

        TypeComponent type = collidedEntity.getComponent(TypeComponent.class);

        if (type == null) return;

        switch (type.type) {
            case ENEMY:
                Gdx.app.log(TAG, "player hit enemy");
                break;
            case SCENERY:
                Gdx.app.log(TAG, "player hit scenery");
                break;
            case OTHER:
                Gdx.app.log(TAG, "player hit other");
                break;
            default:
                // do nothing
        }

        // collision handled. reset.
        collisionC.collisionEntity = null;
    }
}

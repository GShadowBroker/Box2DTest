package com.gledyson.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.gledyson.game.Box2DGame;
import com.gledyson.game.components.CollisionComponent;
import com.gledyson.game.components.PlayerComponent;
import com.gledyson.game.components.TypeComponent;

public class CollisionSystem extends IteratingSystem {
    private static final String TAG = CollisionSystem.class.getSimpleName();

    private final Box2DGame game;

    public CollisionSystem(Box2DGame game) {
        // only need to worry about player collisions for now
        super(Family.all(PlayerComponent.class, CollisionComponent.class).get());
        this.game = game;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        CollisionComponent collisionC = Mappers.collision.get(entity);

        Entity collidedEntity = collisionC.collisionEntity;

        if (collidedEntity == null) {
            return;
        }

        TypeComponent type = collidedEntity.getComponent(TypeComponent.class);

        if (type == null) return;

        switch (type.type) {
            case ENEMY:
                Gdx.app.log(TAG, "player hit enemy");
                Mappers.player.get(entity).isDead = true;
                int score = (int) Mappers.player.get(entity).camera.position.y;
                Gdx.app.log(TAG, "Your score: " + score);
                game.changeScreen(Box2DGame.Screen.ENDGAME);
                break;
            case SCENERY:
                Gdx.app.log(TAG, "player hit scenery");
                Mappers.player.get(entity).onPlatform = true;
                break;
            case SPRING:
                Gdx.app.log(TAG, "player hit spring");
                Mappers.player.get(entity).onSpring = true;
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

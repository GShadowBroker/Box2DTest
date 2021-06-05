package com.gledyson.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.gledyson.game.Box2DGame;
import com.gledyson.game.audio.SoundEffect;
import com.gledyson.game.components.CollisionComponent;
import com.gledyson.game.components.TypeComponent;

public class CollisionSystem extends IteratingSystem {
    private static final String TAG = CollisionSystem.class.getSimpleName();

    private final Box2DGame game;
    private final SoundEffect sound;

    public CollisionSystem(Box2DGame game, SoundEffect sound) {
        // only need to worry about player collisions for now
        super(Family.all(CollisionComponent.class, TypeComponent.class).get());
        this.game = game;
        this.sound = sound;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        CollisionComponent collisionC = Mappers.collision.get(entity);
        Entity otherEntity = collisionC.collisionEntity;

        if (otherEntity == null) return;

        TypeComponent thisType = entity.getComponent(TypeComponent.class);
        TypeComponent otherType = otherEntity.getComponent(TypeComponent.class);

        if (thisType == null || otherType == null) return;

        // TODO fix bug where a bullet hits enemy at the same time as the enemy hits the bullet before it disappears.
        if (thisType.type == TypeComponent.Type.PLAYER) {
            handlePlayerCollision(entity, otherType);
        } else if (thisType.type == TypeComponent.Type.ENEMY) {
            handleEnemyCollision(entity, otherEntity, otherType);
        } else if (thisType.type == TypeComponent.Type.PROJECTILE) {
            handleProjectileCollision(entity, otherEntity, otherType);
        }

        // collision handled. reset.
        collisionC.collisionEntity = null;
    }

    private void handlePlayerCollision(Entity entity, TypeComponent otherType) {
        switch (otherType.type) {
            case ENEMY:
                sound.play(SoundEffect.SoundTrack.DYING);
                Gdx.app.log(TAG, "player hit enemy");
                Mappers.player.get(entity).isDead = true;
                break;
            case SCENERY:
                Gdx.app.log(TAG, "player hit scenery");
                Mappers.player.get(entity).onPlatform = true;

                break;
            case SPRING:
                if (Mappers.body.get(entity).body.getLinearVelocity().y == 0f) break;
                Gdx.app.log(TAG, "player hit spring");
                Mappers.player.get(entity).onSpring = true;
                break;
            case OTHER:
                Gdx.app.log(TAG, "player hit other");
                break;
            default:
                // do nothing
        }
    }

    private void handleEnemyCollision(Entity enemy, Entity otherEntity, TypeComponent otherType) {
        switch (otherType.type) {
            case PLAYER:
                System.out.println("enemy hit player");
                break;
            case ENEMY:
                System.out.println("enemy hit enemy");
                break;
            case SCENERY:
                System.out.println("enemy hit scenery");
                break;
            case SPRING:
                System.out.println("enemy hit spring");
                break;
            case PROJECTILE:
                sound.play(SoundEffect.SoundTrack.DYING);
                Mappers.enemy.get(enemy).isDead = true;
                Mappers.projectile.get(otherEntity).isDead = true;
                game.state.setScore(game.state.getScore() + 50);
                System.out.println("enemy got shot");
                break;
            case OTHER:
                System.out.println("enemy hit other");
                break;
            default:
                System.out.println("No matching type found");
        }
    }


    private void handleProjectileCollision(Entity entity, Entity otherEntity, TypeComponent otherType) {

        switch (otherType.type) {
            case ENEMY:
                sound.play(SoundEffect.SoundTrack.DYING);
                Mappers.enemy.get(entity).isDead = true;
                Mappers.projectile.get(otherEntity).isDead = true;
                game.state.setScore(game.state.getScore() + 50);
                System.out.println("enemy got shot");
                break;
            case SCENERY:
                Mappers.projectile.get(entity).isDead = true;
                System.out.println("projectile hit scenery");
                break;
            case SPRING:
                Mappers.projectile.get(entity).isDead = true;
                System.out.println("projectile hit spring");
                break;
            case PROJECTILE:
                Mappers.projectile.get(entity).isDead = true;
                System.out.println("projectile got shot");
            case OTHER:
                Mappers.projectile.get(entity).isDead = true;
                System.out.println("projectile hit other");
                break;
            default:
                System.out.println("No matching type found");
        }
    }
}

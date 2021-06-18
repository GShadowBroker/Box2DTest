package com.gledyson.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.gledyson.game.Box2DGame;
import com.gledyson.game.audio.SoundEffect;
import com.gledyson.game.components.CollectibleComponent;
import com.gledyson.game.components.CollisionComponent;
import com.gledyson.game.components.TypeComponent;
import com.gledyson.game.physics.BodyFactory;
import com.gledyson.game.screens.MainScreen;

import static com.gledyson.game.components.StateComponent.State.DYING;

public class CollisionSystem extends IteratingSystem {
    private static final String TAG = CollisionSystem.class.getSimpleName();

    private final Box2DGame game;
    private final MainScreen mainScreen;
    private final SoundEffect sound;

    public CollisionSystem(Box2DGame game, MainScreen mainScreen, SoundEffect sound) {
        super(Family.all(CollisionComponent.class, TypeComponent.class).get());
        this.game = game;
        this.sound = sound;
        this.mainScreen = mainScreen;
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
            handlePlayerCollision(entity, otherEntity, otherType);
        } else if (thisType.type == TypeComponent.Type.ENEMY) {
            handleEnemyCollision(entity, otherEntity, otherType);
        } else if (thisType.type == TypeComponent.Type.PROJECTILE) {
            handleProjectileCollision(entity, otherEntity, otherType);
        } else if (thisType.type == TypeComponent.Type.GOAL) {
            switch (otherType.type) {
                case PLAYER:
                    mainScreen.handlePassLevel();
                    break;
            }
        }

        // collision handled. reset.
        collisionC.collisionEntity = null;
    }

    private void handlePlayerCollision(Entity entity, Entity otherEntity, TypeComponent otherType) {
        switch (otherType.type) {
            case ENEMY:
                if (Mappers.state.get(entity).get() != DYING &&
                        Mappers.state.get(otherEntity).get() != DYING
                ) {
                    Body playerBody = Mappers.body.get(entity).body;
                    Body gunBody = Mappers.body.get(Mappers.player.get(entity).gun).body;

                    sound.play(SoundEffect.SoundTrack.DYING);
                    Mappers.state.get(entity).set(DYING);
                    playerBody.setType(BodyDef.BodyType.StaticBody);
                    BodyFactory.makeAllFixturesSensors(Mappers.body.get(entity).body);

                    // transfer linear velocity from player to gun on death, so it is "flung" away
                    gunBody.setType(BodyDef.BodyType.DynamicBody);
                    BodyFactory.removeAllSensors(gunBody);
                    gunBody.applyLinearImpulse(
                            playerBody.getLinearVelocity().x, playerBody.getLinearVelocity().y + 1f,
                            gunBody.getWorldCenter().x, gunBody.getWorldCenter().y,
                            true
                    );
                }
                break;
            case SCENERY:
                Mappers.player.get(entity).onPlatform = true;
                break;
            case SPRING:
                if (Mappers.body.get(entity).body.getLinearVelocity().y == 0f) break;
                Mappers.player.get(entity).onSpring = true;
                Mappers.spring.get(otherEntity).pressed = true;
                break;
            case COLLECTIBLE:
                handleCollectible(otherEntity);
                break;
            case GOAL:
                Gdx.app.log(TAG, "CONGRATS! YOU HAVE PASSED THE LEVEL!");
                mainScreen.handlePassLevel();
                break;
            case OTHER:
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
                Mappers.collision.get(otherEntity).collisionEntity = null;

                if (Mappers.state.get(enemy).get() != DYING) {
                    sound.play(SoundEffect.SoundTrack.DYING);
                    Mappers.state.get(enemy).set(DYING);
                    BodyFactory.makeAllFixturesSensors(Mappers.body.get(enemy).body);
                    Mappers.projectile.get(otherEntity).isDead = true;
                    game.state.setScore(game.state.getScore() + 50);
                }
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
                Mappers.collision.get(otherEntity).collisionEntity = null;

                if (Mappers.state.get(otherEntity).get() != DYING) {
                    sound.play(SoundEffect.SoundTrack.DYING);
                    Mappers.state.get(otherEntity).set(DYING);
                    BodyFactory.makeAllFixturesSensors(Mappers.body.get(otherEntity).body);
                    Mappers.projectile.get(entity).isDead = true;
                    game.state.setScore(game.state.getScore() + 50);
                }
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

    private void handleCollectible(Entity otherEntity) {
        CollectibleComponent collectibleC = Mappers.collectible.get(otherEntity);
        switch (collectibleC.type) {
            case DOLLAR:
                if (!collectibleC.isCollected) {
                    collectibleC.isCollected = true;
                    sound.play(SoundEffect.SoundTrack.LOOT);
                    game.state.setScore(game.state.getScore() + 500);
                }
                break;
            case AMMO:
                Gdx.app.log(TAG, "collected ammo");
                break;
            default:
                // skip
        }
    }
}

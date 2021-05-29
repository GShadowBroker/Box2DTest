package com.gledyson.game.physics;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.gledyson.game.components.CollisionComponent;
import com.gledyson.game.screens.MainScreen;

public class Box2DContactListener implements ContactListener {
    private static final String TAG = Box2DContactListener.class.getSimpleName();

    private final MainScreen context;

    public Box2DContactListener(MainScreen context) {
        this.context = context;
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        if (fa.getBody().getUserData() instanceof Entity) {
            Entity entity = (Entity) fa.getBody().getUserData();
            entityCollision(entity, fb);
        } else if (fb.getBody().getUserData() instanceof Entity) {
            Entity entity = (Entity) fb.getBody().getUserData();
            entityCollision(entity, fa);
        }
    }

    private void entityCollision(Entity entity, Fixture fixture) {
        if (!(fixture.getBody().getUserData() instanceof Entity)) return;

        Entity collidedEntity = (Entity) fixture.getBody().getUserData();

        CollisionComponent collisorEntityComponent = entity.getComponent(CollisionComponent.class);
        CollisionComponent collidedEntityComponent = collidedEntity.getComponent(CollisionComponent.class);

        if (collisorEntityComponent != null) {
            collisorEntityComponent.collisionEntity = collidedEntity;
        } else if (collidedEntityComponent != null) {
            collidedEntityComponent.collisionEntity = entity;
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }

    private void shootUpInAir(Fixture staticFixture, Fixture otherFixture) {
        System.out.println("Adding Force");
        otherFixture.getBody().applyForceToCenter(new Vector2(-1000, -1000), true);
        context.playSound(MainScreen.SoundEffect.BOING);  //new
    }
}

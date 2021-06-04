package com.gledyson.game.physics;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.gledyson.game.components.CollisionComponent;
import com.gledyson.game.systems.Mappers;

public class Box2DContactListener implements ContactListener {
    private static final String TAG = Box2DContactListener.class.getSimpleName();

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

        Gdx.app.log(TAG, "Object " + Mappers.type.get(entity).type + " collided with " + Mappers.type.get(collidedEntity).type);

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
}

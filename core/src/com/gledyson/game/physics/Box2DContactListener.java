package com.gledyson.game.physics;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.gledyson.game.components.CollisionComponent;

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

    private void entityCollision(Entity colliderEntity, Fixture fixture) {
        if (!(fixture.getBody().getUserData() instanceof Entity)) return;

        Entity collidedEntity = (Entity) fixture.getBody().getUserData();

        CollisionComponent colliderEntityComponent = colliderEntity.getComponent(CollisionComponent.class);
        CollisionComponent collidedEntityComponent = collidedEntity.getComponent(CollisionComponent.class);

        if (colliderEntityComponent != null) {
            colliderEntityComponent.collisionEntity = collidedEntity;
        } else if (collidedEntityComponent != null) {
            collidedEntityComponent.collisionEntity = colliderEntity;
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

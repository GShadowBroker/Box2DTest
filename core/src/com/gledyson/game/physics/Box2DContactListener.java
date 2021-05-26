package com.gledyson.game.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

public class Box2DContactListener implements ContactListener {
    private static final String TAG = Box2DContactListener.class.getSimpleName();

    private final Box2DModel model;

    public Box2DContactListener(Box2DModel model) {
        this.model = model;
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        if (fa.getBody().getUserData() == "WATER" ||
                fb.getBody().getUserData() == "WATER") {
            model.isSwimming = true;
        }

        if (fa.getBody().getType() == BodyDef.BodyType.StaticBody && fa.getBody().getUserData() == "GROUND") {
            shootUpInAir(fa, fb);
        } else if (fb.getBody().getType() == BodyDef.BodyType.StaticBody && fb.getBody().getUserData() == "GROUND") {
            shootUpInAir(fb, fa);
        }
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        if (fa.getBody().getUserData() == "WATER" ||
                fb.getBody().getUserData() == "WATER") {
            model.isSwimming = false;
        }
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
        model.playSound(Box2DModel.SoundEffect.BOING);  //new
    }
}

package com.gledyson.game.physics;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

public class BodyFactory {
    private static BodyFactory factoryInstance;
    private final World world;

    public enum Material {
        STEEL, WOOD, RUBBER, STONE
    }

    private BodyFactory(World world) {
        this.world = world;
    }

    public static BodyFactory getInstance(World world) {
        if (factoryInstance == null) {
            factoryInstance = new BodyFactory(world);
        }
        return factoryInstance;
    }

    public static FixtureDef makeFixture(Material material, Shape shape) {
        FixtureDef fixture = new FixtureDef();
        fixture.shape = shape;

        switch (material) {
            case STEEL:
                fixture.density = 1f;
                fixture.friction = 0.3f;
                fixture.restitution = 0.1f;
                break;
            case WOOD:
                fixture.density = 0.5f;
                fixture.friction = 0.7f;
                fixture.restitution = 0.3f;
                break;
            case RUBBER:
                fixture.density = 1f;
                fixture.friction = 0f; // 0.3f
                fixture.restitution = 0.9f; // 1f
                break;
            case STONE:
                fixture.density = 1f;
                fixture.friction = 0.9f;
                fixture.restitution = 0.01f;
                break;
            default:
                fixture.density = 7f;
                fixture.friction = 0.5f;
                fixture.restitution = 0.3f;
        }

        return fixture;
    }

    public Body makeCircle(
            float posX, float posY, float radius,
            Material material, BodyDef.BodyType bodyType,
            boolean fixedRotation
    ) {
        // definition
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = bodyType;
        bodyDef.position.set(posX, posY);
        bodyDef.fixedRotation = fixedRotation;

        // body to attach definition to
        Body body = world.createBody(bodyDef);

        CircleShape circle = new CircleShape();
        circle.setRadius(radius / 2);
        body.createFixture(makeFixture(material, circle));

        circle.dispose();

        return body;
    }

    public Body makeCircle(float posX, float posY, float radius, Material material) {
        return makeCircle(posX, posY, radius, material, BodyDef.BodyType.DynamicBody, false);
    }

    public Body makeRectangle(
            float posX, float posY,
            float width, float height,
            Material material, BodyDef.BodyType bodyType,
            boolean fixedRotation
    ) {
        // create a definition
        BodyDef boxBodyDef = new BodyDef();
        boxBodyDef.type = bodyType;
        boxBodyDef.position.x = posX;
        boxBodyDef.position.y = posY;
        boxBodyDef.fixedRotation = fixedRotation;

        //create the body to attach said definition
        Body boxBody = world.createBody(boxBodyDef);

        PolygonShape poly = new PolygonShape();
        poly.setAsBox(width / 2, height / 2);
        boxBody.createFixture(makeFixture(material, poly));

        poly.dispose();

        return boxBody;
    }

    public Body makePoly(
            float posX, float posY, Vector2[] vertices,
            Material material, BodyDef.BodyType bodyType
    ) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(posX, posY);
        bodyDef.type = bodyType;

        Body body = world.createBody(bodyDef);

        PolygonShape poly = new PolygonShape();
        poly.set(vertices);

        body.createFixture(makeFixture(material, poly));
        poly.dispose();

        return body;
    }

    public Body makeConeSensor(Body body, float radius) {
        FixtureDef fixture = new FixtureDef();
        fixture.isSensor = true;

        PolygonShape shape = new PolygonShape();

        Vector2[] vertices = new Vector2[5];

        vertices[0] = new Vector2(0f, 0f);
        for (int i = 2; i < 6; i++) {
            float angle = i / 6.0f * 145 * MathUtils.degreesToRadians;
            vertices[i - 1] = new Vector2(
                    radius * MathUtils.cos(angle),
                    radius * MathUtils.sin(angle)
            );
        }

        shape.set(vertices);
        fixture.shape = shape;
        body.createFixture(fixture);

        shape.dispose();

        return body;
    }

    public void makeAllFixturesSensors(Body body) {
        for (Fixture fixture : body.getFixtureList()) {
            fixture.setSensor(true);
        }
    }
}

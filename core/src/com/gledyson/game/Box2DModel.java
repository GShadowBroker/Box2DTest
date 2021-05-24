package com.gledyson.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Box2DModel {
    public final World world;
    public final Box2DDebugRenderer debugRenderer;

    private Body body;
    private Body wall;
    private Body movingBody;

    public Box2DModel() {
        world = new World(new Vector2(0f, -9.8f), true);
        debugRenderer = new Box2DDebugRenderer(true, true, true, true, true, true);

        createFloor();
        createWall(10f, 0.1f);
        createWall(-10f, 0.1f);

        BodyFactory factory = BodyFactory.getInstance(world);

        factory.makeRectangle(0f, 0f, 2f, 2f,
                BodyFactory.Material.RUBBER, BodyDef.BodyType.DynamicBody,
                false
        );

        factory.makeCircle(
                0.3f, 7f, 2f,
                BodyFactory.Material.RUBBER, BodyDef.BodyType.DynamicBody
        );
        factory.makeCircle(
                -2f, 2f, 2f,
                BodyFactory.Material.STONE, BodyDef.BodyType.DynamicBody
        );
        factory.makeCircle(
                3f, 5f, 2f,
                BodyFactory.Material.WOOD, BodyDef.BodyType.DynamicBody
        );
        factory.makeCircle(
                5f, 8f, 2f,
                BodyFactory.Material.STEEL, BodyDef.BodyType.DynamicBody
        );
    }

    public void logicStep(float delta) {
        world.step(Math.min(delta, 1 / 60f), 6, 2);
    }

    public void createFloor() {
        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.type = BodyDef.BodyType.StaticBody;
        groundBodyDef.position.set(new Vector2(0f, -10f));

        wall = world.createBody(groundBodyDef);

        PolygonShape groundBox = new PolygonShape();
        groundBox.setAsBox(50f, 0.1f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = groundBox;
        fixtureDef.density = 1f;
        fixtureDef.friction = 1f;

        wall.createFixture(fixtureDef);

        // stay green!
        groundBox.dispose();
    }

    public void createWall(float posX, float posY) {
        BodyDef wallDef = new BodyDef();
        wallDef.type = BodyDef.BodyType.StaticBody;
        wallDef.position.set(new Vector2(posX, posY));

        wall = world.createBody(wallDef);

        PolygonShape wallBox = new PolygonShape();
        wallBox.setAsBox(1f, 10f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = wallBox;
        fixtureDef.density = 1f;
        fixtureDef.friction = .5f;

        wall.createFixture(fixtureDef);

        // stay green!
        wallBox.dispose();
    }
}

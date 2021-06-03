package com.gledyson.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.gledyson.game.components.Box2DBodyComponent;
import com.gledyson.game.components.CollisionComponent;
import com.gledyson.game.components.EnemyComponent;
import com.gledyson.game.components.LiquidFloorComponent;
import com.gledyson.game.components.PlayerComponent;
import com.gledyson.game.components.StateComponent;
import com.gledyson.game.components.TextureComponent;
import com.gledyson.game.components.TransformComponent;
import com.gledyson.game.components.TypeComponent;
import com.gledyson.game.physics.BodyFactory;
import com.gledyson.game.physics.Box2DContactListener;
import com.gledyson.game.systems.RenderingSystem;
import com.gledyson.game.utils.SimplexNoise;

public class LevelFactory implements Disposable {
    private static final String TAG = LevelFactory.class.getSimpleName();
    public final World world;
    private final BodyFactory factory;
    private final PooledEngine engine;
    private final SimplexNoise simplexNoise;
    private final SimplexNoise roughSimplexNoise;

    public int currentLevel = 0;
    private final TextureRegion platformTexture;
    private final TextureRegion springsTexture;
    private final TextureRegion enemyTexture;

    public LevelFactory(PooledEngine engine, TextureAtlas atlas) {
        this.engine = engine;
        this.world = new World(new Vector2(0f, -18.6f), true);
        world.setContactListener(new Box2DContactListener());

        this.platformTexture = atlas.findRegion("platform");
        this.enemyTexture = atlas.findRegion("enemy-1");
        this.springsTexture = atlas.findRegion("springs");

        this.factory = BodyFactory.getInstance(world);
        this.simplexNoise = new SimplexNoise(512, 0.85d, 1);
        this.roughSimplexNoise = new SimplexNoise(512, 0.95d, 1);
    }

    /**
     * Creates a pair of platforms per level up to yLevel
     *
     * @param yLevel the max y level
     */
    public void generateLevel(int yLevel) {
        while (yLevel > currentLevel) {
            // get noise -> sim.getNoise(x,y,z) -> 3D noise
            float noise1 = (float) simplexNoise.getNoise(1, currentLevel, 0); // plat1 should exist?
            float noise2 = (float) simplexNoise.getNoise(1, currentLevel, 100); // if so, where on x?
            float noise3 = (float) simplexNoise.getNoise(1, currentLevel, 200); // plat2 should exist?
            float noise4 = (float) simplexNoise.getNoise(1, currentLevel, 300); // if so, where on x?

            float noise5 = (float) roughSimplexNoise.getNoise(1, currentLevel, 1400); // spring should exist on plat1?
            float noise6 = (float) roughSimplexNoise.getNoise(1, currentLevel, 2500); // spring should exist on plat2?
            float noise7 = (float) roughSimplexNoise.getNoise(1, currentLevel, 2700); // enemy should exist on plat1?
            float noise8 = (float) roughSimplexNoise.getNoise(1, currentLevel, 3000); // enemy should exist on plat2?

            if (noise1 > 0.2f) {
                createPlatform(noise2 * 25f + 2f, currentLevel * 3f);

                if (Math.abs(noise5) > 0.5f) {
                    createBouncyPlatform(noise2 * 25f + 2f, currentLevel * 3f);
                } else if (Math.abs(noise7) > 0.5f) {
                    createEnemy(noise2 * 25f + 2f, currentLevel * 3f + 0.65f);
                }
            }

            if (noise3 > 0.2f) {
                createPlatform(noise4 * 25f + 2f, currentLevel * 3f);

                if (Math.abs(noise6) > 0.4f) {
                    createBouncyPlatform(noise4 * 25f + 2f, currentLevel * 3f);
                } else if (Math.abs(noise8) > 0.5f) {
                    createEnemy(noise4 * 25f + 2f, currentLevel * 3f + 0.65f);
                }
            }

            currentLevel++;
        }
    }

    public Entity createPlayer(TextureRegion playerTexture, OrthographicCamera camera) {
        Entity entity = engine.createEntity();

        // create components
        Box2DBodyComponent bodyC = engine.createComponent(Box2DBodyComponent.class);
        PlayerComponent playerC = engine.createComponent(PlayerComponent.class);
        TransformComponent transformC = engine.createComponent(TransformComponent.class);
        TextureComponent textureC = engine.createComponent(TextureComponent.class);
        CollisionComponent collisionC = engine.createComponent(CollisionComponent.class);
        TypeComponent typeC = engine.createComponent(TypeComponent.class);
        StateComponent stateC = engine.createComponent(StateComponent.class);

        // create data for components and add them
        playerC.camera = camera;
        bodyC.body = factory.makeCircle(
                12f, 5f, 1f,
                BodyFactory.Material.STONE, BodyDef.BodyType.DynamicBody,
                true);
        bodyC.body.setUserData(entity);
        transformC.position.set(12f, 5f, 0f); // z is the order of rendering

        textureC.region = playerTexture;
        typeC.type = TypeComponent.Type.PLAYER;
        stateC.set(StateComponent.State.IDLE);

        // add components to entity
        entity.add(bodyC);
        entity.add(playerC);
        entity.add(transformC);
        entity.add(textureC);
        entity.add(collisionC);
        entity.add(typeC);
        entity.add(stateC);

        // add entity to engine
        engine.addEntity(entity);

        return entity;
    }

    public void createEnemy(float posX, float posY) {
        Entity entity = engine.createEntity();

        EnemyComponent enemyC = engine.createComponent(EnemyComponent.class);
        Box2DBodyComponent bodyC = engine.createComponent(Box2DBodyComponent.class);
        TransformComponent transformC = engine.createComponent(TransformComponent.class);
        TextureComponent textureC = engine.createComponent(TextureComponent.class);
        TypeComponent typeC = engine.createComponent(TypeComponent.class);

        bodyC.body = factory.makeCircle(
                posX, posY, 1f,
                BodyFactory.Material.STONE, BodyDef.BodyType.KinematicBody,
                true);
        bodyC.body.setUserData(entity);
        transformC.position.set(posX, posY, 0f);

        enemyC.goingLeft = MathUtils.random() >= 0.5f;
        enemyC.xPosCenter = posX;

        textureC.region = enemyTexture;
        typeC.type = TypeComponent.Type.ENEMY;

        entity.add(enemyC);
        entity.add(bodyC);
        entity.add(transformC);
        entity.add(textureC);
        entity.add(typeC);

        engine.addEntity(entity);
    }

    public void createPlatform(float posX, float posY) {
        Entity entity = engine.createEntity();

        Box2DBodyComponent bodyC = engine.createComponent(Box2DBodyComponent.class);
        TextureComponent textureC = engine.createComponent(TextureComponent.class);
        TypeComponent typeC = engine.createComponent(TypeComponent.class);
        TransformComponent transformC = engine.createComponent(TransformComponent.class);

        // body
        bodyC.body = factory.makeRectangle(
                posX, posY, 1.5f, 0.5f,
                BodyFactory.Material.STONE, BodyDef.BodyType.StaticBody, true);
        bodyC.body.setUserData(entity);

        transformC.position.set(posX, posY, 0f);

        // texture
        textureC.region = platformTexture;

        // type
        typeC.type = TypeComponent.Type.SCENERY;

        // Add all
        entity.add(bodyC);
        entity.add(textureC);
        entity.add(typeC);
        entity.add(transformC);

        engine.addEntity(entity);
    }

    private void createBouncyPlatform(float posX, float posY) {
        Entity entity = engine.createEntity();

        Box2DBodyComponent bodyC = engine.createComponent(Box2DBodyComponent.class);
        TextureComponent textureC = engine.createComponent(TextureComponent.class);
        TypeComponent typeC = engine.createComponent(TypeComponent.class);
        TransformComponent transformC = engine.createComponent(TransformComponent.class);

        // body
        bodyC.body = factory.makeRectangle(
                posX, posY, 1f, 1f,
                BodyFactory.Material.STONE, BodyDef.BodyType.DynamicBody, true);

        bodyC.body.setUserData(entity);

        // texture
        textureC.region = springsTexture;

        // position
        transformC.position.set(posX, posY, 0f);

        // type
        typeC.type = TypeComponent.Type.SPRING;

        // Add all
        entity.add(bodyC);
        entity.add(textureC);
        entity.add(typeC);
        entity.add(transformC);

        engine.addEntity(entity);
    }

    public void createFloor(TextureRegion floorTexture) {
        Entity entity = engine.createEntity();

        Box2DBodyComponent bodyC = engine.createComponent(Box2DBodyComponent.class);
        TextureComponent textureC = engine.createComponent(TextureComponent.class);
        TypeComponent typeC = engine.createComponent(TypeComponent.class);

        // body
        bodyC.body = factory.makeRectangle(
                0f, 0, 100, 0.2f,
                BodyFactory.Material.STONE, BodyDef.BodyType.StaticBody, true);
        bodyC.body.setUserData(entity);

        // texture
        textureC.region = floorTexture;

        // type
        typeC.type = TypeComponent.Type.SCENERY;

        // Add all
        entity.add(bodyC);
        entity.add(textureC);
        entity.add(typeC);

        engine.addEntity(entity);
    }

    public void createLiquidFloor(TextureRegion texture) {
        Entity entity = engine.createEntity();

        LiquidFloorComponent liquidC = engine.createComponent(LiquidFloorComponent.class);
        Box2DBodyComponent bodyC = engine.createComponent(Box2DBodyComponent.class);
        TransformComponent transformC = engine.createComponent(TransformComponent.class);
        TextureComponent textureC = engine.createComponent(TextureComponent.class);
        TypeComponent typeC = engine.createComponent(TypeComponent.class);

        // body
        bodyC.body = factory.makeRectangle(
                20, -40, 100, 40,
                BodyFactory.Material.STONE, BodyDef.BodyType.KinematicBody, true);
        bodyC.body.setUserData(entity);
        factory.makeAllFixturesSensors(bodyC.body); // make it a sensor

        // position
        transformC.position.set(20, -15, 0);

        // texture
        textureC.region = texture;

        // type
        typeC.type = TypeComponent.Type.ENEMY;

        // Add all
        entity.add(bodyC);
        entity.add(textureC);
        entity.add(typeC);
        entity.add(transformC);
        entity.add(liquidC);

        engine.addEntity(entity);
    }

    public void createMapCollisions(MapObjects objects) {
        for (MapObject object : objects) {

            Gdx.app.log(TAG, "name: " + object.getName());

            if (object instanceof PolygonMapObject) {
                createPolyCollision((PolygonMapObject) object);

            } else if (object instanceof RectangleMapObject) {
                if (createRectangleCollision(object)) return;

            } else if (object instanceof EllipseMapObject) {
                Gdx.app.log(TAG, "found ellipse");
                if (createEllipseMapObject(object)) return;
            }
        }
    }

    private boolean createEllipseMapObject(MapObject object) {
        Ellipse ellipse = ((EllipseMapObject) object).getEllipse();

        if (object.getName() != null && object.getName().equals("enemy")) {

            createEnemy(
                    (ellipse.x + ellipse.width / 2f) / RenderingSystem.PPM,
                    (ellipse.y + ellipse.height / 2f) / RenderingSystem.PPM
            );
            return true;
        }
        return false;
    }

    private boolean createRectangleCollision(MapObject object) {
        Rectangle rectangle = ((RectangleMapObject) object).getRectangle();

        if (object.getName() != null && object.getName().equals("spring")) {

            createBouncyPlatform(
                    (rectangle.x + rectangle.width / 2f) / RenderingSystem.PPM,
                    (rectangle.y + rectangle.height / 2f) / RenderingSystem.PPM
            );
            return true;
        }

        Gdx.app.log(TAG, "x: " + rectangle.x / RenderingSystem.PPM + ", y: " + rectangle.y / RenderingSystem.PPM);

        factory.makeRectangle(
                (rectangle.x + rectangle.width / 2f) / RenderingSystem.PPM,
                (rectangle.y + rectangle.height / 2f) / RenderingSystem.PPM,
                rectangle.width / RenderingSystem.PPM,
                rectangle.height / RenderingSystem.PPM,
                BodyFactory.Material.STONE, BodyDef.BodyType.StaticBody, true
        );
        return false;
    }

    private void createPolyCollision(PolygonMapObject object) {
        Polygon polygon = object.getPolygon();

        float[] polyVertices = polygon.getTransformedVertices();

        for (int i = 0; i < polyVertices.length; i++) {
            Gdx.app.log(TAG, "vertice: " + polyVertices[i] / RenderingSystem.PPM);
        }

        // convert float[] vertices to vector2[]
        Vector2[] vectorVertices = new Vector2[polyVertices.length / 2];

        for (int i = 0; i < vectorVertices.length; i++) {
            vectorVertices[i] = new Vector2(
                    polyVertices[i * 2] / RenderingSystem.PPM,
                    polyVertices[(i * 2) + 1] / RenderingSystem.PPM
            );
        }

        factory.makePoly(
                0f,
                0f,
                vectorVertices,
                BodyFactory.Material.STONE,
                BodyDef.BodyType.StaticBody
        );
    }

    @Override
    public void dispose() {
        world.dispose();
    }
}

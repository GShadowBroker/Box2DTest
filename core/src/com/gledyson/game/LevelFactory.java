package com.gledyson.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.gledyson.game.components.AnimationComponent;
import com.gledyson.game.components.Box2DBodyComponent;
import com.gledyson.game.components.CollisionComponent;
import com.gledyson.game.components.EnemyComponent;
import com.gledyson.game.components.LiquidFloorComponent;
import com.gledyson.game.components.PlayerComponent;
import com.gledyson.game.components.ProjectileComponent;
import com.gledyson.game.components.SpringComponent;
import com.gledyson.game.components.StateComponent;
import com.gledyson.game.components.TextureComponent;
import com.gledyson.game.components.TransformComponent;
import com.gledyson.game.components.TypeComponent;
import com.gledyson.game.physics.BodyFactory;
import com.gledyson.game.physics.Box2DContactListener;
import com.gledyson.game.systems.RenderingSystem;

public class LevelFactory implements Disposable {
    private static final String TAG = LevelFactory.class.getSimpleName();
    public final World world;
    private final BodyFactory factory;
    private final PooledEngine engine;

    public static final Vector2 GRAVITY_VECTOR = new Vector2(0f, -18.6f);

    private final TextureAtlas atlas;

    public LevelFactory(PooledEngine engine, TextureAtlas atlas) {
        this.engine = engine;
        this.world = new World(GRAVITY_VECTOR, true);
        world.setContactListener(new Box2DContactListener());

        this.atlas = atlas;

        this.factory = BodyFactory.getInstance(world);
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
        AnimationComponent animationC = engine.createComponent(AnimationComponent.class);

        // create data for components and add them
        playerC.camera = camera;
        bodyC.body = factory.makeCircle(
                12f, 5f, 1f,
                BodyFactory.Material.STONE, BodyDef.BodyType.DynamicBody,
                true);
        bodyC.body.setUserData(entity);
        transformC.position.set(12f, 5f, 0f); // z is the order of rendering

//        textureC.region = playerTexture;
        typeC.type = TypeComponent.Type.PLAYER;
        stateC.set(StateComponent.State.IDLE);

        TextureRegion p1 = atlas.findRegion("player-1");
        TextureRegion p2 = atlas.findRegion("player-2");
        TextureRegion p3 = atlas.findRegion("player-3");
        TextureRegion p4 = atlas.findRegion("player-4");

        TextureRegion[] framesArray = {
                p1, p1, p1, p1, p1, p1, p1, p1,
                p1, p1, p1, p1, p1, p1, p1, p1,
                p1, p2, p3, p4
        };

        Animation<TextureRegion> idleAnimation = new Animation<>(0.125f, framesArray);
        idleAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);

        Array<TextureAtlas.AtlasRegion> dyingEffect = atlas.findRegions("puff");
        Animation<TextureRegion> dyingAnimation = new Animation<TextureRegion>(0.07f, dyingEffect);

        animationC.animations.put(StateComponent.State.IDLE, idleAnimation);
        animationC.animations.put(StateComponent.State.MOVING, idleAnimation);
        animationC.animations.put(StateComponent.State.JUMPING, idleAnimation);
        animationC.animations.put(StateComponent.State.FALLING, idleAnimation);
        animationC.animations.put(StateComponent.State.DYING, dyingAnimation);

        // add components to entity
        entity.add(bodyC);
        entity.add(playerC);
        entity.add(transformC);
        entity.add(textureC);
        entity.add(collisionC);
        entity.add(typeC);
        entity.add(stateC);
        entity.add(animationC);

        // add entity to engine
        engine.addEntity(entity);

        return entity;
    }

    public void createEnemy(float posX, float posY) {
        Entity entity = engine.createEntity();

        EnemyComponent enemyC = engine.createComponent(EnemyComponent.class);
        Box2DBodyComponent bodyC = engine.createComponent(Box2DBodyComponent.class);
        CollisionComponent collisionC = engine.createComponent(CollisionComponent.class);
        TransformComponent transformC = engine.createComponent(TransformComponent.class);
        TextureComponent textureC = engine.createComponent(TextureComponent.class);
        TypeComponent typeC = engine.createComponent(TypeComponent.class);
        AnimationComponent animationC = engine.createComponent(AnimationComponent.class);
        StateComponent stateC = engine.createComponent(StateComponent.class);

        bodyC.body = factory.makeCircle(
                posX, posY, 1f,
                BodyFactory.Material.STONE, BodyDef.BodyType.KinematicBody,
                true);
        bodyC.body.setUserData(entity);
        transformC.position.set(posX, posY, 0f);

        enemyC.goingLeft = MathUtils.random() >= 0.5f;
        enemyC.xPosCenter = posX;

        textureC.region = atlas.findRegion("enemy-1");

        stateC.set(StateComponent.State.IDLE);

        // animations
        TextureRegion e1 = atlas.findRegion("enemy-1");
        TextureRegion e2 = atlas.findRegion("enemy-2");
        TextureRegion e3 = atlas.findRegion("enemy-3");
        TextureRegion e4 = atlas.findRegion("enemy-4");

        TextureRegion[] framesArray = {
                e1, e1, e1, e1, e1, e1, e1,
                e1, e1, e1, e1, e1, e1, e1,
                e1, e2, e3, e4
        };

        Animation<TextureRegion> walkingAnimation = new Animation<>(0.125f, framesArray);
        walkingAnimation.setPlayMode(Animation.PlayMode.LOOP);

        Array<TextureAtlas.AtlasRegion> dyingEffect = atlas.findRegions("puff");
        Animation<TextureRegion> dyingAnimation = new Animation<TextureRegion>(0.07f, dyingEffect);

        animationC.animations.put(StateComponent.State.MOVING, walkingAnimation);
        animationC.animations.put(StateComponent.State.DYING, dyingAnimation);

        typeC.type = TypeComponent.Type.ENEMY;

        entity.add(enemyC);
        entity.add(bodyC);
        entity.add(collisionC);
        entity.add(transformC);
        entity.add(textureC);
        entity.add(typeC);
        entity.add(animationC);
        entity.add(stateC);

        engine.addEntity(entity);
    }

    public void createPlatform(float posX, float posY, float width, float height) {
        Entity entity = engine.createEntity();

        Box2DBodyComponent bodyC = engine.createComponent(Box2DBodyComponent.class);
        TypeComponent typeC = engine.createComponent(TypeComponent.class);
        TransformComponent transformC = engine.createComponent(TransformComponent.class);

        // body
        bodyC.body = factory.makeRectangle(
                posX, posY, width, height,
                BodyFactory.Material.STONE, BodyDef.BodyType.StaticBody, true);
        bodyC.body.setUserData(entity);

        transformC.position.set(posX, posY, 0f);

        // type
        typeC.type = TypeComponent.Type.SCENERY;

        // Add all
        entity.add(bodyC);
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
        SpringComponent springC = engine.createComponent(SpringComponent.class);

        // body
        bodyC.body = factory.makeRectangle(
                posX, posY, 1f, 1f,
                BodyFactory.Material.STONE, BodyDef.BodyType.DynamicBody, true);

        bodyC.body.setUserData(entity);

        // texture
        TextureRegion sp1 = atlas.findRegion("spring-1");
        TextureRegion sp2 = atlas.findRegion("spring-2");

        textureC.region = sp1;
        springC.frames[0] = sp1;
        springC.frames[1] = sp2;

        // position
        transformC.position.set(posX, posY, 0f);

        // type
        typeC.type = TypeComponent.Type.SPRING;

        // Add all
        entity.add(bodyC);
        entity.add(textureC);
        entity.add(typeC);
        entity.add(transformC);
        entity.add(springC);

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
        BodyFactory.makeAllFixturesSensors(bodyC.body); // make it a sensor

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

    public void createProjectile(float posX, float posY, Vector2 linearVelocity) {
        Entity entity = engine.createEntity();

        Box2DBodyComponent bodyComponent = engine.createComponent(Box2DBodyComponent.class);
        TransformComponent transformComponent = engine.createComponent(TransformComponent.class);
        TextureComponent textureComponent = engine.createComponent(TextureComponent.class);
        AnimationComponent animationComponent = engine.createComponent(AnimationComponent.class);
        TypeComponent typeComponent = engine.createComponent(TypeComponent.class);
        CollisionComponent collisionComponent = engine.createComponent(CollisionComponent.class);
        ProjectileComponent projectileComponent = engine.createComponent(ProjectileComponent.class);
        StateComponent stateComponent = engine.createComponent(StateComponent.class);

//        bodyComponent.body = factory.makeCircle(
//                posX, posY, 0.5f,
//                BodyFactory.Material.STONE, BodyDef.BodyType.DynamicBody, true
//        );
        bodyComponent.body = factory.makeRectangle(posX, posY, 0.5f, 0.25f,
                BodyFactory.Material.STONE, BodyDef.BodyType.DynamicBody, true);

        bodyComponent.body.setBullet(true);
        BodyFactory.makeAllFixturesSensors(bodyComponent.body);

        transformComponent.position.set(posX, posY, 0f);
        transformComponent.rotation = linearVelocity.angleDeg();

        Array<TextureAtlas.AtlasRegion> projectileFrames = atlas.findRegions("projectile");
        textureComponent.region = projectileFrames.get(0);

        Animation<TextureRegion> projectileAnimation = new Animation<TextureRegion>(0.07f, projectileFrames);

        animationComponent.animations.put(StateComponent.State.MOVING, projectileAnimation);

        projectileComponent.linearVelocity.set(linearVelocity);

        typeComponent.type = TypeComponent.Type.PROJECTILE;

        stateComponent.set(StateComponent.State.MOVING);

        bodyComponent.body.setUserData(entity);

        entity.add(textureComponent);
        entity.add(animationComponent);
        entity.add(typeComponent);
        entity.add(collisionComponent);
        entity.add(projectileComponent);
        entity.add(bodyComponent);
        entity.add(transformComponent);
        entity.add(stateComponent);

        engine.addEntity(entity);
    }

    public void createMapCollisions(MapObjects objects) {
        for (MapObject object : objects) {

            if (object instanceof PolygonMapObject) {
                createPolyCollision((PolygonMapObject) object);

            } else if (object instanceof RectangleMapObject) {
                createRectangleCollision(object);

            } else if (object instanceof EllipseMapObject) {
                createEllipseMapObject(object);
            }
        }
    }

    private void createEllipseMapObject(MapObject object) {
        if (object.getName() == null) return;

        Ellipse ellipse = ((EllipseMapObject) object).getEllipse();

        switch (object.getName()) {
            case "enemy":
                createEnemy(
                        (ellipse.x + ellipse.width / 2f) / RenderingSystem.PPM,
                        (ellipse.y + ellipse.height / 2f) / RenderingSystem.PPM
                );
                break;
            default:
                // nothing
        }
    }

    private void createRectangleCollision(MapObject object) {
        if (object.getName() == null) return;

        Rectangle rectangle = ((RectangleMapObject) object).getRectangle();

        switch (object.getName()) {
            case "spring":
                createBouncyPlatform(
                        (rectangle.x + rectangle.width / 2f) / RenderingSystem.PPM,
                        (rectangle.y + rectangle.height / 2f) / RenderingSystem.PPM
                );
                break;

            case "platform":
                createPlatform(
                        (rectangle.x + rectangle.width / 2f) / RenderingSystem.PPM,
                        (rectangle.y + rectangle.height / 2f) / RenderingSystem.PPM,
                        rectangle.width / RenderingSystem.PPM,
                        rectangle.height / RenderingSystem.PPM
                );
                break;

            default:
                factory.makeRectangle(
                        (rectangle.x + rectangle.width / 2f) / RenderingSystem.PPM,
                        (rectangle.y + rectangle.height / 2f) / RenderingSystem.PPM,
                        rectangle.width / RenderingSystem.PPM,
                        rectangle.height / RenderingSystem.PPM,
                        BodyFactory.Material.STONE, BodyDef.BodyType.StaticBody, true
                );
        }
    }

    private void createPolyCollision(PolygonMapObject object) {
        Polygon polygon = object.getPolygon();

        float[] polyVertices = polygon.getTransformedVertices();

        // convert float[] vertices to vector2[]
        Vector2[] vectorVertices = new Vector2[polyVertices.length / 2];

        for (int i = 0; i < vectorVertices.length; i++) {
            vectorVertices[i] = new Vector2(
                    polyVertices[i * 2] / RenderingSystem.PPM,
                    polyVertices[(i * 2) + 1] / RenderingSystem.PPM
            );
        }

        factory.makePolygon(
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

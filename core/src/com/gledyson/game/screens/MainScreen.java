package com.gledyson.game.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;
import com.gledyson.game.Box2DGame;
import com.gledyson.game.KeyboardController;
import com.gledyson.game.components.AnimationComponent;
import com.gledyson.game.components.Box2DBodyComponent;
import com.gledyson.game.components.CollisionComponent;
import com.gledyson.game.components.PlayerComponent;
import com.gledyson.game.components.StateComponent;
import com.gledyson.game.components.TextureComponent;
import com.gledyson.game.components.TransformComponent;
import com.gledyson.game.components.TypeComponent;
import com.gledyson.game.loaders.Box2DAssetManager;
import com.gledyson.game.physics.BodyFactory;
import com.gledyson.game.physics.Box2DContactListener;
import com.gledyson.game.systems.AnimationSystem;
import com.gledyson.game.systems.CollisionSystem;
import com.gledyson.game.systems.PhysicsDebugSystem;
import com.gledyson.game.systems.PhysicsSystem;
import com.gledyson.game.systems.PlayerControlSystem;
import com.gledyson.game.systems.RenderingSystem;

public class MainScreen implements Screen {
    private final World world;
    private final Box2DGame game;
    private final OrthographicCamera camera;
    private final KeyboardController controller;
    private final BodyFactory factory;
    private final PooledEngine engine;

    private final Vector2 GRAVITY_VECTOR = new Vector2(0f, -18.6f);

    public enum SoundEffect {
        PING, BOING
    }

    private final TextureAtlas atlas;
    private final float soundVolume;
    private final Sound pingSound;
    private final Sound boingSound;

    public boolean isSwimming = false;

    public MainScreen(Box2DGame game) {
        this.game = game;
//        camera = new OrthographicCamera(WORLD_WIDTH, WORLD_HEIGHT);
        controller = new KeyboardController();

        world = new World(GRAVITY_VECTOR, true);
        world.setContactListener(new Box2DContactListener(this));
        factory = BodyFactory.getInstance(world);

        atlas = game.assetManager.manager.get(Box2DAssetManager.GAME_ATLAS);
        pingSound = game.assetManager.manager.get(Box2DAssetManager.PING_SOUND);
        boingSound = game.assetManager.manager.get(Box2DAssetManager.BOING_SOUND);
        soundVolume = game.getPreferences().getSoundVolume();

        // create RenderingSystem
        RenderingSystem renderingSystem = new RenderingSystem(game.batch);
        camera = renderingSystem.getCamera();
        game.batch.setProjectionMatrix(camera.combined);

        // create engine
        engine = new PooledEngine();

        // add systems
        engine.addSystem(new AnimationSystem());
        engine.addSystem(renderingSystem);
        engine.addSystem(new PhysicsSystem(world));
        engine.addSystem(new PhysicsDebugSystem(world, camera));
        engine.addSystem(new CollisionSystem());
        engine.addSystem(new PlayerControlSystem(controller));

        // create game objects
        createPlayer();
        createPlatform(2, 2);
        createPlatform(2, 7);
        createPlatform(7, 2);
        createPlatform(7, 7);

        createFloor();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(controller);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        engine.update(delta);
    }

    private void createPlayer() {
        Entity entity = engine.createEntity();

        // create components
        Box2DBodyComponent bodyC = engine.createComponent(Box2DBodyComponent.class);
        PlayerComponent playerC = engine.createComponent(PlayerComponent.class);
        TransformComponent transformC = engine.createComponent(TransformComponent.class);
        TextureComponent textureC = engine.createComponent(TextureComponent.class);
        CollisionComponent collisionC = engine.createComponent(CollisionComponent.class);
        TypeComponent typeC = engine.createComponent(TypeComponent.class);
        StateComponent stateC = engine.createComponent(StateComponent.class);

        // Testing animation
        AnimationComponent animationC = engine.createComponent(AnimationComponent.class);
        TextureAtlas atlas = game.assetManager.manager.get(Box2DAssetManager.GAME_ATLAS);
        TextureRegion player1 = atlas.findRegion("player-1");
        TextureRegion player2 = atlas.findRegion("player-2");
        TextureRegion player3 = atlas.findRegion("player-3");
        TextureRegion player4 = atlas.findRegion("player-4");
        TextureRegion[] playerAnimation = new TextureRegion[]{
                player1, player1, player1, player1,
                player1, player1, player1, player1,
                player1, player1, player1, player1,
                player1, player1, player1, player1,
                player1, player1, player1, player1,
                player1, player1, player1, player1,
                player2, player3, player4, player2,
                player1, player1, player1, player1,
                player1, player1, player1, player1,
                player1, player1, player1, player1,
                player1, player1, player1, player1,
                player1, player1, player1, player1,
                player1, player1, player1, player1,
                player2, player3, player4, player2,
                player1, player1, player1, player1,
                player1, player1, player1, player1,
                player1, player1, player1, player1,
                player1, player1, player1, player1,
                player1, player1, player1, player1,
                player1, player1, player1, player1,
                player2, player3, player4, player2,
                player2, player3, player4, player2
        };
        animationC.animations.put(
                StateComponent.State.IDLE,
                new Animation<>(0.07f, playerAnimation)
        );

        // create data for components and add them
        bodyC.body = factory.makeCircle(
                10, 10, 1f,
                BodyFactory.Material.STONE, BodyDef.BodyType.DynamicBody,
                true);
        bodyC.body.setUserData(entity);
        transformC.position.set(10, 10, 0); // z is the order of rendering
        textureC.region = atlas.findRegion("player-1");
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
        entity.add(animationC);

        // add entity to engine
        engine.addEntity(entity);
    }

    private void createPlatform(float posX, float posY) {
        Entity entity = engine.createEntity();

        Box2DBodyComponent bodyC = engine.createComponent(Box2DBodyComponent.class);
        TextureComponent textureC = engine.createComponent(TextureComponent.class);
        TypeComponent typeC = engine.createComponent(TypeComponent.class);

        // body
        bodyC.body = factory.makeRectangle(
                posX, posY, 3, 0.2f,
                BodyFactory.Material.STONE, BodyDef.BodyType.StaticBody, true);
        bodyC.body.setUserData(entity);

        // texture
        textureC.region = atlas.findRegion("player-1");

        // type
        typeC.type = TypeComponent.Type.SCENERY;

        // Add all
        entity.add(bodyC);
        entity.add(textureC);
        entity.add(typeC);

        engine.addEntity(entity);
    }

    private void createFloor() {
        Entity entity = engine.createEntity();

        Box2DBodyComponent bodyC = engine.createComponent(Box2DBodyComponent.class);
        TextureComponent textureC = engine.createComponent(TextureComponent.class);
        TypeComponent typeC = engine.createComponent(TypeComponent.class);

        // body
        bodyC.body = factory.makeRectangle(
                0, 0, 100, 0.2f,
                BodyFactory.Material.STONE, BodyDef.BodyType.StaticBody, true);
        bodyC.body.setUserData(entity);

        // texture
        textureC.region = atlas.findRegion("player-1");

        // type
        typeC.type = TypeComponent.Type.SCENERY;

        // Add all
        entity.add(bodyC);
        entity.add(textureC);
        entity.add(typeC);

        engine.addEntity(entity);
    }


    public void playSound(MainScreen.SoundEffect sound) {
        switch (sound) {
            case PING:
                pingSound.play(soundVolume);
                break;
            case BOING:
                boingSound.play(soundVolume);
                break;
            default:
                // do nothing
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
    }
}

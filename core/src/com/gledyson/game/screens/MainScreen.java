package com.gledyson.game.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.gledyson.game.Box2DGame;
import com.gledyson.game.KeyboardController;
import com.gledyson.game.LevelFactory;
import com.gledyson.game.audio.SoundEffect;
import com.gledyson.game.loaders.Box2DAssetManager;
import com.gledyson.game.systems.AnimationSystem;
import com.gledyson.game.systems.CameraSystem;
import com.gledyson.game.systems.CollisionSystem;
import com.gledyson.game.systems.EnemySystem;
import com.gledyson.game.systems.PhysicsDebugSystem;
import com.gledyson.game.systems.PhysicsSystem;
import com.gledyson.game.systems.PlayerControlSystem;
import com.gledyson.game.systems.ProjectileSystem;
import com.gledyson.game.systems.RenderingSystem;
import com.gledyson.game.utils.FrameRate;

public class MainScreen implements Screen {
    private final Box2DGame game;
    private final OrthographicCamera camera;
    private final KeyboardController controller;
    private final PooledEngine engine;
    private final LevelFactory lvlFactory;
    private final FrameRate frameRate;

    private final TextureAtlas atlas;
    private final TiledMap map;
    private final OrthogonalTiledMapRenderer mapRenderer;

    private final SoundEffect sound;

    public MainScreen(Box2DGame game) {
        this.game = game;
        controller = new KeyboardController();
        frameRate = new FrameRate();
        sound = new SoundEffect(game);

        atlas = game.assetManager.manager.get(Box2DAssetManager.GAME_ATLAS);

        // Map
        map = game.assetManager.manager.get(Box2DAssetManager.LEVEL_1_MAP);
        mapRenderer = new OrthogonalTiledMapRenderer(map, RenderingSystem.PIXELS_TO_METERS);

        // create RenderingSystem
        RenderingSystem renderingSystem = new RenderingSystem(game, mapRenderer);
        camera = renderingSystem.getCamera();
        game.batch.setProjectionMatrix(camera.combined);

        // create engine
        engine = new PooledEngine();

        // create LevelFactory
        lvlFactory = new LevelFactory(engine, atlas);

        // add systems

        engine.addSystem(new AnimationSystem());
//        engine.addSystem(new LevelGenerationSystem(lvlFactory));
        engine.addSystem(new PlayerControlSystem(sound, controller, lvlFactory));
        engine.addSystem(new CollisionSystem(game, sound));

        // create game objects
        Entity player = lvlFactory.createPlayer(atlas.findRegion("player-1"), camera);

        lvlFactory.createMapCollisions(map.getLayers().get("collision").getObjects());

//        int floorWidth = (int) (40 * RenderingSystem.PPM);
//        int floorHeight = (int) (1 * RenderingSystem.PPM);
//        TextureRegion floorTexture = DFUtils.makeTextureRegion(floorWidth, floorHeight, "11331180");
//        lvlFactory.createFloor(floorTexture);

//        int liquidWidth = (int) (40 * RenderingSystem.PPM);
//        int liquidHeight = (int) (10 * RenderingSystem.PPM);
//        TextureRegion liquidTexture = DFUtils.makeTextureRegion(liquidWidth, liquidHeight, "11113380");
//        lvlFactory.createLiquidFloor(liquidTexture);

        // Add last systems. Render last.
//        engine.addSystem(new LiquidFloorSystem(player));
        engine.addSystem(new EnemySystem());
        engine.addSystem(new ProjectileSystem());
        engine.addSystem(new CameraSystem(map));
        engine.addSystem(new PhysicsSystem(lvlFactory.world, engine, map));
        engine.addSystem(new PhysicsDebugSystem(lvlFactory.world, camera));
        engine.addSystem(renderingSystem);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(controller);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        engine.update(delta);

        if (game.debugMode) {
            frameRate.update();
            frameRate.render();
        }
    }

    @Override
    public void resize(int width, int height) {
        frameRate.resize(width, height);
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
        lvlFactory.dispose();
        map.dispose();
        mapRenderer.dispose();
    }
}

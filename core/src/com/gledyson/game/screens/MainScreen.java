package com.gledyson.game.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
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
import com.gledyson.game.components.PlayerComponent;
import com.gledyson.game.loaders.Box2DAssetManager;
import com.gledyson.game.physics.BodyFactory;
import com.gledyson.game.systems.AnimationSystem;
import com.gledyson.game.systems.CameraSystem;
import com.gledyson.game.systems.CollisionSystem;
import com.gledyson.game.systems.EnemySystem;
import com.gledyson.game.systems.Mappers;
import com.gledyson.game.systems.PhysicsDebugSystem;
import com.gledyson.game.systems.PhysicsSystem;
import com.gledyson.game.systems.PlayerControlSystem;
import com.gledyson.game.systems.ProjectileSystem;
import com.gledyson.game.systems.RenderingSystem;
import com.gledyson.game.systems.SpringSystem;
import com.gledyson.game.utils.FrameRate;

public class MainScreen implements Screen {
    private static final String TAG = MainScreen.class.getSimpleName();

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

    private final PlayerComponent playerC;

    public MainScreen(Box2DGame game) {
        this(game, new PooledEngine());
    }

    public MainScreen(Box2DGame game, PooledEngine engine) {
        this.game = game;
        controller = new KeyboardController();
        frameRate = new FrameRate();
        sound = new SoundEffect(game);

        atlas = game.assetManager.manager.get(Box2DAssetManager.GAME_ATLAS);

        // Map
        map = game.assetManager.manager.get(Box2DAssetManager.LEVEL_1_MAP);
        mapRenderer = new OrthogonalTiledMapRenderer(map, RenderingSystem.PIXELS_TO_METERS);

        // create engine
        this.engine = engine;

        Gdx.app.log(TAG, "Systems: " + engine.getSystems().size());

        // create LevelFactory
        lvlFactory = new LevelFactory(engine, atlas);

        // add systems, update camera
        RenderingSystem renderingSystem = new RenderingSystem(game, mapRenderer);

        camera = renderingSystem.getCamera();
        game.batch.setProjectionMatrix(camera.combined);

        engine.addSystem(new AnimationSystem());
//        engine.addSystem(new LevelGenerationSystem(lvlFactory));
        engine.addSystem(new PlayerControlSystem(sound, controller, lvlFactory));
        engine.addSystem(new CollisionSystem(game, sound));
//        engine.addSystem(new LiquidFloorSystem(player));
        engine.addSystem(new EnemySystem());
        engine.addSystem(new ProjectileSystem());
        engine.addSystem(new SpringSystem());
        engine.addSystem(new CameraSystem(map));
        engine.addSystem(new PhysicsSystem(lvlFactory.world, engine, map));
        engine.addSystem(new PhysicsDebugSystem(lvlFactory.world, camera, game));
        engine.addSystem(renderingSystem);

        // create Entities
        Entity playerEntity = lvlFactory.createPlayer(atlas.findRegion("player-1"), camera);
        lvlFactory.createMapCollisions(map.getLayers().get("collision").getObjects());
        // save player component
        playerC = Mappers.player.get(playerEntity);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(controller);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        if (playerC.isDead) {
            handlePlayerDead();
        }

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

    public void handlePlayerDead() {
        if (game.state.getContinues() == 0) {
            clear();
            game.changeScreen(Box2DGame.Screen.ENDGAME);
            return;
        }
        game.state.die();
        clear();
        game.setScreen(new MainScreen(game, engine));
    }

    /**
     * Clears this instance before reinitializing it
     **/
    public void clear() {
        engine.removeAllEntities();
        engine.clearPools();
        for (EntitySystem system : engine.getSystems()) {
            engine.removeSystem(system);
        }
        BodyFactory.clearInstance();
        dispose();
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
        Gdx.app.log(TAG, "Disposing of MainScreen...");
        lvlFactory.dispose();
        map.dispose();
        mapRenderer.dispose();
    }
}

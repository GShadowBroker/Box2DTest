package com.gledyson.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.ScreenUtils;
import com.gledyson.game.Box2DGame;
import com.gledyson.game.KeyboardController;
import com.gledyson.game.loaders.Box2DAssetManager;
import com.gledyson.game.physics.Box2DModel;

public class MainScreen implements Screen {
    protected final Box2DGame game;
    protected final Box2DModel model;
    protected final OrthographicCamera camera;

    private final KeyboardController controller;

    public final int WORLD_WIDTH = 32;
    public final int WORLD_HEIGHT = 24;

    public final TextureAtlas atlas;

    public MainScreen(Box2DGame game) {
        this.game = game;
        this.camera = new OrthographicCamera(WORLD_WIDTH, WORLD_HEIGHT);
        this.atlas = game.assetManager.manager.get(Box2DAssetManager.GAME_ATLAS);
        this.controller = new KeyboardController();
        this.model = new Box2DModel(controller, camera, game);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(controller);
    }

    @Override
    public void render(float delta) {
        // clear screen
        ScreenUtils.clear(0, 0, 0, 1);
        // convert to world sizes
        game.batch.setProjectionMatrix(camera.combined);

        // update
        model.player.update();

        // draw graphics
        game.batch.begin();
        model.player.draw(delta);
        game.batch.end();

        // render world
        model.debugRenderer.render(model.world, camera.combined);

        model.logicStep(delta);
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

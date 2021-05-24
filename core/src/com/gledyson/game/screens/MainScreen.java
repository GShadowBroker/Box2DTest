package com.gledyson.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;
import com.gledyson.game.Box2DGame;
import com.gledyson.game.Box2DModel;

public class MainScreen implements Screen {
    protected final Box2DGame game;
    protected final Box2DModel model;
    protected final OrthographicCamera camera;

    public final int WORLD_WIDTH = 32;
    public final int WORLD_HEIGHT = 24;

    public MainScreen(Box2DGame game) {
        this.game = game;
        this.model = new Box2DModel();
        this.camera = new OrthographicCamera(WORLD_WIDTH, WORLD_HEIGHT);
    }

    @Override
    public void show() {
        // CHECK THIS LATER
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void render(float delta) {
        // clear screen
        ScreenUtils.clear(0, 0, 0, 1);

        // draw graphics

        // render world
        model.debugRenderer.render(model.world, camera.combined);

        // do world step
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

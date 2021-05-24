package com.gledyson.game.screens;

import com.badlogic.gdx.Screen;
import com.gledyson.game.Box2DGame;

public class LoadingScreen implements Screen {
    private final Box2DGame game;

    public LoadingScreen(Box2DGame game) {
        this.game = game;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        game.changeScreen(Box2DGame.Screen.MENU);
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

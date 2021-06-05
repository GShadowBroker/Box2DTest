package com.gledyson.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.gledyson.game.Box2DGame;
import com.gledyson.game.loaders.Box2DAssetManager;

public class EndScreen implements Screen {
    private final Box2DGame game;
    private final Stage stage;

    public EndScreen(Box2DGame game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        init();
        game.state.reset();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 1f);
        stage.act();
        stage.draw();
    }

    private void init() {
        Skin skin = game.assetManager.manager.get(Box2DAssetManager.SKIN_JSON);
        TextureAtlas atlas = game.assetManager.manager.get(Box2DAssetManager.GAME_ATLAS);
        TextureRegion endBackground = atlas.findRegion("brazil-bg");

        Table table = new Table();
        table.setFillParent(true);
        table.setDebug(game.debugMode);
        table.setBackground(new TiledDrawable(endBackground));

        Label gameOverText = new Label("GAME OVER!", skin);
        gameOverText.setAlignment(Align.center);

        Label totalScoreLabel = new Label("Total score:", skin);
        Label totalScoreValue = new Label(String.valueOf(game.state.getScore()), skin);

        TextButton button = new TextButton("main menu", skin);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.changeScreen(Box2DGame.Screen.MENU);
            }
        });

        table.add(gameOverText).colspan(2).fillX().uniform().center();

        table.row().padTop(32f);
        table.add(totalScoreLabel).left().fillX().padRight(16f);
        table.add(totalScoreValue).left().fillX();

        table.row().padTop(32f);
        table.add(button).colspan(2).fillX().uniform().center();

        stage.addActor(table);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
        stage.dispose();
    }
}

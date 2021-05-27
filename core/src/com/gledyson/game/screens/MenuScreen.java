package com.gledyson.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.gledyson.game.Box2DGame;
import com.gledyson.game.loaders.Box2DAssetManager;

public class MenuScreen implements Screen {
    private static final String TAG = MenuScreen.class.getSimpleName();
    private final Box2DGame game;
    private final Stage stage;

    public MenuScreen(final Box2DGame game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());
        init();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        stage.act(Math.min(delta, 1 / 30f));
        stage.draw();
    }

    private void init() {
        // Table
        Table table = new Table();
        table.setFillParent(true);
        table.setDebug(game.debugMode);

        stage.addActor(table);

        // Skins and menu buttons
        Skin skin = game.assetManager.manager.get(Box2DAssetManager.SKIN_JSON);

        TextButton newGameBtn = new TextButton("new game", skin);
        TextButton preferencesBtn = new TextButton("preferences", skin);
        TextButton exitBtn = new TextButton("exit", skin);

        // Add listeners to menu buttons
        newGameBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.changeScreen(Box2DGame.Screen.MAIN);
            }
        });

        preferencesBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.changeScreen(Box2DGame.Screen.PREFERENCES);
            }
        });

        exitBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        // Add buttons
        table.add(newGameBtn).fillX().uniform();
        table.row().pad(10, 0, 10, 0);

        table.add(preferencesBtn).fillX().uniform();
        table.row();

        table.add(exitBtn).fillX().uniform();
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

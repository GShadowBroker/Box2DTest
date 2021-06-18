package com.gledyson.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gledyson.game.loaders.Box2DAssetManager;
import com.gledyson.game.screens.EndScreen;
import com.gledyson.game.screens.LoadingScreen;
import com.gledyson.game.screens.MainScreen;
import com.gledyson.game.screens.MenuScreen;
import com.gledyson.game.screens.PreferencesScreen;

public class Box2DGame extends Game {
    public SpriteBatch batch;
    public Box2DAssetManager assetManager;
    public boolean debugMode = true;
    public boolean debugRendererActive = false;
    public AppPreferences preferences;
    public GameState state;

    public enum Screen {
        LOADING, MENU, MAIN, PREFERENCES, ENDGAME
    }

    private LoadingScreen loadingScreen;
    private MenuScreen menuScreen;
    private MainScreen mainScreen;
    private PreferencesScreen preferencesScreen;
    private EndScreen endScreen;

    public Music playingSong;

    @Override
    public void create() {
        batch = new SpriteBatch();
        assetManager = new Box2DAssetManager();
        preferences = new AppPreferences(this);
        state = new GameState();

        changeScreen(Screen.LOADING);

        assetManager.queueAddMusic();
        assetManager.manager.finishLoading();
        playingSong = assetManager.manager.get(Box2DAssetManager.BIO_UNIT_MUSIC);

        playingSong.setLooping(true);
        playingSong.setVolume(preferences.getMusicVolume());
        playingSong.play();
    }

    @Override
    public void render() {
        super.render();
    }

    public void changeScreen(Screen screen) {
        switch (screen) {
            case LOADING:
                if (loadingScreen == null) {
                    loadingScreen = new LoadingScreen(this);
                }
                setScreen(loadingScreen);
                break;
            case MENU:
                if (menuScreen == null) {
                    menuScreen = new MenuScreen(this);
                }
                setScreen(menuScreen);
                break;
            case MAIN:
                mainScreen = new MainScreen(this);
                setScreen(mainScreen);
                break;
            case PREFERENCES:
                if (preferencesScreen == null) {
                    preferencesScreen = new PreferencesScreen(this);
                }
                setScreen(preferencesScreen);
                break;
            case ENDGAME:
                if (endScreen == null) {
                    endScreen = new EndScreen(this);
                }
                setScreen(endScreen);
                break;
            default:
                // do nothing
        }
    }

    public AppPreferences getPreferences() {
        return preferences;
    }

    @Override
    public void dispose() {
        batch.dispose();
        playingSong.dispose();
        assetManager.manager.dispose();
        super.dispose();
    }
}

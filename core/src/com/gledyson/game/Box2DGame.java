package com.gledyson.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gledyson.game.screens.EndScreen;
import com.gledyson.game.screens.LoadingScreen;
import com.gledyson.game.screens.MainScreen;
import com.gledyson.game.screens.MenuScreen;
import com.gledyson.game.screens.PreferencesScreen;

public class Box2DGame extends Game {
    public SpriteBatch batch;
    public boolean debugMode = false;
    private AppPreferences preferences;

    public enum Screen {
        LOADING, MENU, MAIN, PREFERENCES, ENDGAME
    }

    private LoadingScreen loadingScreen;
    private MenuScreen menuScreen;
    private MainScreen mainScreen;
    private PreferencesScreen preferencesScreen;
    private EndScreen endScreen;

    @Override
    public void create() {
        batch = new SpriteBatch();
        preferences = new AppPreferences();
        changeScreen(Screen.LOADING);
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
                if (mainScreen == null) {
                    mainScreen = new MainScreen(this);
                }
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
        super.dispose();
    }
}

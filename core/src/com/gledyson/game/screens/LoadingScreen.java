package com.gledyson.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.gledyson.game.Box2DGame;
import com.gledyson.game.loaders.Box2DAssetManager;

public class LoadingScreen implements Screen {
    private static final String TAG = LoadingScreen.class.getSimpleName();

    private final Box2DGame game;

    // Textures
    private TextureAtlas atlas;
    private TextureRegion title;
    private TextureRegion dash;

    private Animation<TextureRegion> dashAnimation;
    private final float DASH_ANIMATION_DURATION = 0.07f;

    private float loadingProgress;
    private int currentLoadingStage = 0;

    // State timers
    private float countdown = 5f;
    private float animationStateTime;

    // loading stages
    private final int IMAGE = 0; // loading images
    private final int FONT = 1; // loading fonts
    private final int PARTICLES = 2; // loading particle effects
    private final int SOUND = 3; // loading sounds
    private final int MUSIC = 4; // loading music

    public LoadingScreen(Box2DGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        game.assetManager.queueAddLoadingImages();
        game.assetManager.manager.finishLoading();

        // Blender function => adds flame images together
        game.batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);

        atlas = game.assetManager.manager.get(Box2DAssetManager.LOADING_ATLAS);

        title = atlas.findRegion("staying-alight-logo");
        dash = atlas.findRegion("loading-dash");

        // Init animation bar
        dashAnimation = new Animation<TextureRegion>(
                DASH_ANIMATION_DURATION,
                atlas.findRegion("flames/flames")
        );
        dashAnimation.setPlayMode(Animation.PlayMode.LOOP);

        // queue game images but don't load them yet
        game.assetManager.queueAddGameImages();
        Gdx.app.log(TAG, "Loading images...");
    }

    @Override
    public void render(float delta) {
//        ScreenUtils.clear(0, 0, 0, 1);
        Gdx.gl.glClearColor(0, 0, 0, 1); //  clear the screen
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        animationStateTime += delta;

        game.batch.begin();
        game.batch.draw(title, 135, 250);
        drawLoadingBar(currentLoadingStage * 2,
                dashAnimation.getKeyFrame(animationStateTime, true));
        game.batch.end();

        updateManager(delta);
    }

    private void updateManager(float delta) {

        loadingProgress = game.assetManager.manager.getProgress();
        Gdx.app.log(TAG, (int) (loadingProgress * 100) + "%");

        if (game.assetManager.manager.update()) {
            currentLoadingStage += 1;

            switch (currentLoadingStage) {
                case FONT:
                    game.assetManager.queueAddFonts();
                    Gdx.app.log(TAG, "Loading fonts...");
                    break;
                case PARTICLES:
                    game.assetManager.queueAddParticles();
                    Gdx.app.log(TAG, "Loading particle effects...");
                    break;
                case SOUND:
                    game.assetManager.queueAddSounds();
                    Gdx.app.log(TAG, "Loading sounds...");
                    break;
                case MUSIC:
                    game.assetManager.queueAddMusic();
                    Gdx.app.log(TAG, "Loading music...");
                    break;
                case 5:
                    // we are done
                    Gdx.app.log(TAG, "Finished.");
                    break;
                default:
                    // do nothing
            }

            // ensure min 5 seconds on loading screen
            if (currentLoadingStage > 5) {
                countdown -= delta;
                currentLoadingStage = 5;
                if (countdown < 0) {
                    game.changeScreen(Box2DGame.Screen.MENU);
                }
            }
        }
    }

    public void drawLoadingBar(int currentLoadingStage, TextureRegion currentFrame) {
        for (int i = 0; i < currentLoadingStage; i++) {
            game.batch.draw(currentFrame, 50 + (i * 50), 150, 50, 50);
            game.batch.draw(dash, 35 + (i * 50), 140, 80, 80);
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
        atlas.dispose();
    }
}

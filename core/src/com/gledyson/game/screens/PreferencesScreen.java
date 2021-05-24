package com.gledyson.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.gledyson.game.Box2DGame;

public class PreferencesScreen implements Screen {
    private static final String TAG = PreferencesScreen.class.getSimpleName();

    private final Box2DGame game;
    private final Stage stage;

    // Texts and labels
    private static final String TITLE = "PREFERENCES";
    private static final String MUSIC_VOLUME = "music volume";
    private static final String MUSIC_ENABLED = "music enabled";
    private static final String SOUND_VOLUME = "sound volume";
    private static final String SOUND_ENABLED = "sound enabled";

    public PreferencesScreen(final Box2DGame game) {
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
        // Grid
        Table table = new Table();
        table.setFillParent(true);
        table.setDebug(game.debugMode);

        // Add table to stage
        stage.addActor(table);

        // Declare actors
        final Skin skin = new Skin(Gdx.files.internal("skin/quantum-horizon-ui.json"));

        final Slider musicVolumeSlider = new Slider(0f, 1f, 0.1f, false, skin);
        musicVolumeSlider.setValue(game.getPreferences().getMusicVolume());
        musicVolumeSlider.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                game.getPreferences().setMusicVolume(musicVolumeSlider.getValue());
                return false;
            }
        });

        final CheckBox musicEnabledCheckbox = new CheckBox(null, skin);
        musicEnabledCheckbox.setChecked(game.getPreferences().isMusicEnabled());
        musicEnabledCheckbox.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                boolean enabled = musicEnabledCheckbox.isChecked();
                game.getPreferences().setMusicEnabled(enabled);
                return false;
            }
        });

        final Slider soundVolumeSlider = new Slider(0f, 1f, 0.1f, false, skin);
        soundVolumeSlider.setValue(game.getPreferences().getSoundVolume());
        soundVolumeSlider.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                game.getPreferences().setSoundVolume(soundVolumeSlider.getValue());
                return false;
            }
        });

        final CheckBox soundEnabledCheckbox = new CheckBox(null, skin);
        soundEnabledCheckbox.setChecked(game.getPreferences().isSoundEnabled());
        soundEnabledCheckbox.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                boolean enabled = soundEnabledCheckbox.isChecked();
                game.getPreferences().setSoundEnabled(enabled);
                return false;
            }
        });

        final TextButton backButton = new TextButton("back", skin, "default");
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.changeScreen(Box2DGame.Screen.MENU);
            }
        });

        final Label titleLabel = new Label(TITLE, skin);
        final Label musicVolumeLabel = new Label(MUSIC_VOLUME, skin);
        final Label musicEnabledLabel = new Label(MUSIC_ENABLED, skin);
        final Label soundVolumeLabel = new Label(SOUND_VOLUME, skin);
        final Label soundEnabledLabel = new Label(SOUND_ENABLED, skin);

        // Populate table
        table.add(titleLabel).colspan(2);

        // 1st row
        table.row().pad(32f, 0f, 10f, 0f);
        table.add(musicVolumeLabel).fillX().uniform();
        table.add(musicVolumeSlider).fillX().uniform();

        // 2nd row
        table.row().pad(10f, 0f, 10f, 0f);
        table.add(musicEnabledLabel).fillX().uniform();
        table.add(musicEnabledCheckbox).fillX().uniform();

        // 3rd row
        table.row().pad(10f, 0f, 10f, 0f);
        table.add(soundVolumeLabel).fillX().uniform();
        table.add(soundVolumeSlider).fillX().uniform();

        // 4th row
        table.row().pad(10f, 0f, 10f, 0f);
        table.add(soundEnabledLabel).fillX().uniform();
        table.add(soundEnabledCheckbox).fillX().uniform();

        // 5th row
        table.row().pad(32f, 0f, 10f, 0f);
        table.add(backButton).left();
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

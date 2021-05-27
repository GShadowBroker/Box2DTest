package com.gledyson.game.loaders;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Box2DAssetManager {
    public final AssetManager manager = new AssetManager();

    // Atlas
    public static final String GAME_IMG = "game/images/game.png";
    public static final String GAME_ATLAS = "game/images/game.atlas";
    public static final String LOADING_ATLAS = "loading/loading.atlas";

    // Skin
    public static final String SKIN_JSON = "game/skin/quantum-horizon-ui.json";
    public static final String SKIN_ATLAS = "game/skin/quantum-horizon-ui.atlas";

    // Sounds
    public static final String BOING_SOUND = "game/sounds/boing.wav";
    public static final String PING_SOUND = "game/sounds/ping.wav";

    // Music
    public static final String BIO_UNIT_MUSIC = "game/music/Bio Unit - Zone.mp3";

    public void queueAddGameImages() {
        // atlas
        manager.load(GAME_ATLAS, TextureAtlas.class);

        // skin
        SkinLoader.SkinParameter skinParams = new SkinLoader.SkinParameter(SKIN_ATLAS);
        manager.load(SKIN_JSON, Skin.class, skinParams);
    }

    public void queueAddLoadingImages() {
        manager.load(LOADING_ATLAS, TextureAtlas.class);
    }

//    public void queueAddSkin() {
//        SkinLoader.SkinParameter skinParams = new SkinLoader.SkinParameter(SKIN_ATLAS);
//        manager.load(SKIN_JSON, Skin.class, skinParams);
//    }

    public void queueAddSounds() {
        manager.load(BOING_SOUND, Sound.class);
        manager.load(PING_SOUND, Sound.class);
    }

    public void queueAddMusic() {
        manager.load(BIO_UNIT_MUSIC, Music.class);
    }

    public void queueAddFonts() {
    }

    public void queueAddParticles() {
    }
}

package com.gledyson.game.loaders;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Box2DAssetManager {
    public final AssetManager manager = new AssetManager();

    // Atlas
    public static final String GAME_ATLAS = "game/images/game.atlas";
    public static final String LOADING_ATLAS = "loading/loading.atlas";

    // Skin
    public static final String SKIN_JSON = "game/skin/quantum-horizon-ui.json";
    public static final String SKIN_ATLAS = "game/skin/quantum-horizon-ui.atlas";

    // Sounds
    public static final String BOING_SOUND = "game/sounds/boing.wav";
    public static final String SHOT_SOUND = "game/sounds/shot.wav";
    public static final String MAN_DYING_SOUND = "game/sounds/man-dying.wav";
    public static final String OUT_OF_AMMO_SOUND = "game/sounds/out_of_ammo.wav";
    public static final String HANDGUN_RELOAD_SOUND = "game/sounds/handgun_reload.wav";
    public static final String LOOT_SOUND = "game/sounds/loot.wav";

    // Music
    public static final String BIO_UNIT_MUSIC = "game/music/Bio Unit - Zone.mp3";

    // Maps
    public static final String LEVEL_1_MAP = "game/maps/level_1.tmx";
    public static final String LEVEL_2_MAP = "game/maps/level_2.tmx";

    public void queueAddGameImages() {
        // atlas
        manager.load(GAME_ATLAS, TextureAtlas.class);

        // skin
        SkinLoader.SkinParameter skinParams = new SkinLoader.SkinParameter(SKIN_ATLAS);
        manager.load(SKIN_JSON, Skin.class, skinParams);

        // maps
        manager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        manager.load(LEVEL_1_MAP, TiledMap.class);
        manager.load(LEVEL_2_MAP, TiledMap.class);
    }

    public void queueAddLoadingImages() {
        manager.load(LOADING_ATLAS, TextureAtlas.class);
    }

    public void queueAddSounds() {
        manager.load(BOING_SOUND, Sound.class);
        manager.load(SHOT_SOUND, Sound.class);
        manager.load(MAN_DYING_SOUND, Sound.class);
        manager.load(OUT_OF_AMMO_SOUND, Sound.class);
        manager.load(HANDGUN_RELOAD_SOUND, Sound.class);
        manager.load(LOOT_SOUND, Sound.class);
    }

    public void queueAddMusic() {
        manager.load(BIO_UNIT_MUSIC, Music.class);
    }

    public void queueAddFonts() {
    }

    public void queueAddParticles() {
    }
}

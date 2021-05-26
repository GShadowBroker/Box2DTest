package com.gledyson.game.loaders;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

public class Box2DAssetManager {
    public final AssetManager manager = new AssetManager();

    // Textures
    public static final String PLAYER_IMG = "images/player.png";
    public static final String ENEMY_IMG = "images/enemy.png";

    // Sounds
    public static final String BOING_SOUND = "sounds/boing.wav";
    public static final String PING_SOUND = "sounds/ping.wav";

    public void queueAddImages() {
        manager.load(PLAYER_IMG, Texture.class);
        manager.load(ENEMY_IMG, Texture.class);
    }

    public void queueAddSounds() {
        manager.load(BOING_SOUND, Sound.class);
        manager.load(PING_SOUND, Sound.class);
    }
}

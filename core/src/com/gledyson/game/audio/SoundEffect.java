package com.gledyson.game.audio;

import com.badlogic.gdx.audio.Sound;
import com.gledyson.game.Box2DGame;
import com.gledyson.game.loaders.Box2DAssetManager;

public class SoundEffect {
    public enum SoundTrack {
        BOING, SHOT, DYING, OUT_OF_AMMO, HANDGUN_RELOAD, LOOT
    }

    private final float soundVolume;
    private final Sound boingSound;
    private final Sound shotSound;
    private final Sound manDyingSound;
    private final Sound outOfAmmoSound;
    private final Sound handgunReloadSound;
    private final Sound lootSound;

    public SoundEffect(Box2DGame game) {
        soundVolume = game.getPreferences().getSoundVolume();
        boingSound = game.assetManager.manager.get(Box2DAssetManager.BOING_SOUND);
        shotSound = game.assetManager.manager.get(Box2DAssetManager.SHOT_SOUND);
        manDyingSound = game.assetManager.manager.get(Box2DAssetManager.MAN_DYING_SOUND);
        outOfAmmoSound = game.assetManager.manager.get(Box2DAssetManager.OUT_OF_AMMO_SOUND);
        handgunReloadSound = game.assetManager.manager.get(Box2DAssetManager.HANDGUN_RELOAD_SOUND);
        lootSound = game.assetManager.manager.get(Box2DAssetManager.LOOT_SOUND);
    }

    public void play(SoundTrack sound) {
        switch (sound) {
            case SHOT:
                shotSound.play(soundVolume);
                break;
            case OUT_OF_AMMO:
                outOfAmmoSound.play(soundVolume);
                break;
            case BOING:
                boingSound.play(soundVolume);
                break;
            case DYING:
                manDyingSound.play(soundVolume);
                break;
            case HANDGUN_RELOAD:
                handgunReloadSound.play(soundVolume);
                break;
            case LOOT:
                lootSound.play(soundVolume);
                break;
            default:
                // do nothing
        }
    }
}

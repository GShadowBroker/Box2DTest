package com.gledyson.game.audio;

import com.badlogic.gdx.audio.Sound;
import com.gledyson.game.Box2DGame;
import com.gledyson.game.loaders.Box2DAssetManager;

public class SoundEffect {
    public enum SoundTrack {
        BOING, SHOT, DYING
    }

    private final float soundVolume;
    private final Sound boingSound;
    private final Sound shotSound;
    private final Sound manDyingSound;

    public SoundEffect(Box2DGame game) {
        soundVolume = game.getPreferences().getSoundVolume();
        boingSound = game.assetManager.manager.get(Box2DAssetManager.BOING_SOUND);
        shotSound = game.assetManager.manager.get(Box2DAssetManager.SHOT_SOUND);
        manDyingSound = game.assetManager.manager.get(Box2DAssetManager.MAN_DYING_SOUND);
    }

    public void play(SoundTrack sound) {
        switch (sound) {
            case SHOT:
                shotSound.play(soundVolume);
                break;
            case BOING:
                boingSound.play(soundVolume);
                break;
            case DYING:
                manDyingSound.play(soundVolume);
                break;
            default:
                // do nothing
        }
    }
}

package com.gledyson.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;

public class AnimationComponent implements Component, Pool.Poolable {
    public final ObjectMap<StateComponent.State, Animation<TextureRegion>> animations = new ObjectMap<>();

    public void flipX() {
        for (ObjectMap.Entry<StateComponent.State, Animation<TextureRegion>> entry : animations.entries()) {
            for (TextureRegion frame : entry.value.getKeyFrames()) {
                if (!frame.isFlipX()) frame.flip(true, false);
            }
        }
    }

    public void unflipX() {
        for (ObjectMap.Entry<StateComponent.State, Animation<TextureRegion>> entry : animations.entries()) {
            for (TextureRegion frame : entry.value.getKeyFrames()) {
                if (frame.isFlipX()) frame.flip(true, false);
            }
        }
    }

    @Override
    public void reset() {
        animations.clear();
    }
}

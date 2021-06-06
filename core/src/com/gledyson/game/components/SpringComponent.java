package com.gledyson.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Pool;

import java.util.Arrays;

public class SpringComponent implements Component, Pool.Poolable {
    public boolean pressed = false;
    public TextureRegion[] frames = new TextureRegion[2];
    public float timeSincePressed = 0f;

    @Override
    public void reset() {
        pressed = false;
        Arrays.fill(frames, null);
    }
}

package com.gledyson.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Pool;

public class PlayerComponent implements Component, Pool.Poolable {
    public OrthographicCamera camera = null;
    public boolean isDead = false;
    public boolean onPlatform = false;
    public boolean onSpring = false;

    public int maxJumps = 1;
    public final float TIME_BETWEEN_JUMPS = 0.5f;
    public int jumpsLeft = maxJumps;
    public float timeSinceLastJump = 0f;

    // projectile
    public float timeBetweenShots = 0.5f;
    public float timeSinceLastShot = 0f;

    @Override
    public void reset() {
        camera = null;
        isDead = false;
        onPlatform = false;
        onSpring = false;

        maxJumps = 1;
        jumpsLeft = maxJumps;
        timeSinceLastJump = 0f;

        timeBetweenShots = 0.5f;
        timeSinceLastShot = 0f;
    }
}

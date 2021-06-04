package com.gledyson.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

public class ProjectileComponent implements Component, Poolable {
    public Vector2 linearVelocity = new Vector2();
    public boolean isDead = false;

    @Override
    public void reset() {
        linearVelocity.set(0f, 0f);
        isDead = false;
    }
}

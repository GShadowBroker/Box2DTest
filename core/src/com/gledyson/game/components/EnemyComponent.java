package com.gledyson.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class EnemyComponent implements Component, Pool.Poolable {
    public enum Type {
        CORONA, AEDES
    }

    public boolean isDead = false;
    public float xPosCenter = -1;
    public boolean goingLeft = false;
    public boolean facingLeft = false;

    public Type type = Type.CORONA;

    @Override
    public void reset() {
        isDead = false;
        xPosCenter = -1;
        goingLeft = false;
        type = Type.CORONA;
    }
}

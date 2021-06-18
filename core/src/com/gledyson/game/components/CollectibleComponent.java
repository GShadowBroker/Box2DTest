package com.gledyson.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class CollectibleComponent implements Component, Pool.Poolable {
    public enum Type {
        DOLLAR, AMMO
    }

    public Type type = Type.DOLLAR;
    public boolean isCollected = false;

    @Override
    public void reset() {
        type = Type.DOLLAR;
        isCollected = false;
    }
}

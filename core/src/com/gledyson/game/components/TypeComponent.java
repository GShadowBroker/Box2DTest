package com.gledyson.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class TypeComponent implements Component, Pool.Poolable {
    public enum Type {
        PLAYER, ENEMY, SCENERY, SPRING, HELD_ITEM, COLLECTIBLE, PROJECTILE, GOAL, OTHER
    }

    public Type type = Type.OTHER;

    @Override
    public void reset() {
        type = Type.OTHER;
    }
}

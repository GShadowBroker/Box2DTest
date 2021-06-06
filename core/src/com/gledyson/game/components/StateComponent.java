package com.gledyson.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class StateComponent implements Component, Pool.Poolable {

    public enum State {
        IDLE, JUMPING, FALLING, MOVING, HIT, DYING
    }

    private State state = State.IDLE;
    public float time = 0f;
    public boolean isLooping = false;

    public State get() {
        return state;
    }

    public void set(State newState) {
        this.state = newState;
        this.time = 0f;
    }

    @Override
    public void reset() {
        state = State.IDLE;
        time = 0f;
        isLooping = false;
    }
}

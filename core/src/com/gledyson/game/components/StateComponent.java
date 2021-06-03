package com.gledyson.game.components;

import com.badlogic.ashley.core.Component;

public class StateComponent implements Component {
    public enum State {
        IDLE, JUMPING, FALLING, MOVING, HIT
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
}

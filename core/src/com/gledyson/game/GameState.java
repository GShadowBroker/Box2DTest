package com.gledyson.game;

import com.badlogic.gdx.Gdx;

public class GameState {
    private int score = 0;
    private int level = 0;
    private int continues = 1;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getLevel() {
        return level;
    }

    public void passLevel() {
        this.level += 1;
    }

    public int getContinues() {
        return continues;
    }

    public void incrementContinues(int amount) {
        continues += amount;
    }

    public void die() {
        continues -= 1;
    }

    public void reset() {
        Gdx.app.log("GameState", "resetting game state...");
        score = 0;
        level = 0;
        continues = 1;
    }
}

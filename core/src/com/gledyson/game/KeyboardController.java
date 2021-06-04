package com.gledyson.game;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;

public class KeyboardController implements InputProcessor {
    private static final String TAG = KeyboardController.class.getSimpleName();
    public boolean up, down, left, right;

    public boolean mouse1, mouse2, mouse3;
    public boolean isDragged;
    public final Vector3 mousePos = new Vector3(0f, 0f, 0f);

    @Override
    public boolean keyDown(int keycode) {
        boolean keyProcessed = false;
        switch (keycode) {
            case Input.Keys.LEFT:
                left = true;
                keyProcessed = true;
                break;
            case Input.Keys.RIGHT:
                right = true;
                keyProcessed = true;
                break;
            case Input.Keys.UP:
                up = true;
                keyProcessed = true;
                break;
            case Input.Keys.DOWN:
                down = true;
                keyProcessed = true;
                break;
            default:
                // do nothing
        }
        return keyProcessed;
    }

    @Override
    public boolean keyUp(int keycode) {
        boolean keyProcessed = false;
        switch (keycode) {
            case Input.Keys.LEFT:
                left = false;
                keyProcessed = true;
                break;
            case Input.Keys.RIGHT:
                right = false;
                keyProcessed = true;
                break;
            case Input.Keys.UP:
                up = false;
                keyProcessed = true;
                break;
            case Input.Keys.DOWN:
                down = false;
                keyProcessed = true;
                break;
            default:
                // do nothing
        }
        return keyProcessed;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == 0) {
            mouse1 = true;
        } else if (button == 1) {
            mouse2 = true;
        } else if (button == 2) {
            mouse3 = true;
        }
        mousePos.set(screenX, screenY, 0f);
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        isDragged = false;
        if (button == 0) {
            mouse1 = false;
        } else if (button == 1) {
            mouse2 = false;
        } else if (button == 2) {
            mouse3 = false;
        }
        mousePos.set(screenX, screenY, 0f);
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        isDragged = true;
        mousePos.set(screenX, screenY, 0f);
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        mousePos.set(screenX, screenY, 0f);
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}

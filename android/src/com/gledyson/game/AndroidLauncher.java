package com.gledyson.game;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useGyroscope = false;
        config.useAccelerometer = false;
        config.useCompass = false;
        config.hideStatusBar = true;
        initialize(new Box2DGame(), config);
    }
}

package com.gledyson.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ObjectMap;

public class AnimationComponent implements Component {
    public final ObjectMap<StateComponent.State, Animation<TextureRegion>> animations = new ObjectMap<>();
}

package com.gledyson.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.gledyson.game.components.Box2DBodyComponent;
import com.gledyson.game.components.LiquidFloorComponent;
import com.gledyson.game.components.TransformComponent;

public class LiquidFloorSystem extends IteratingSystem {
    private static final String TAG = LiquidFloorSystem.class.getSimpleName();

    private final Entity player;

    public LiquidFloorSystem(Entity player) {
        super(Family.all(LiquidFloorComponent.class).get());
        this.player = player;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Box2DBodyComponent bodyC = Mappers.body.get(entity);
        float currentYLevel = player.getComponent(TransformComponent.class).position.y;

        bodyC.body.setTransform(
                bodyC.body.getPosition().x,
                bodyC.body.getPosition().y + 0.05f,
                bodyC.body.getAngle()
        );

        Gdx.app.log(TAG, "x: " + bodyC.body.getPosition().x + "y: " + bodyC.body.getPosition().y);
    }
}

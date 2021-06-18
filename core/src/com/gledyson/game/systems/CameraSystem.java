package com.gledyson.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.gledyson.game.components.Box2DBodyComponent;
import com.gledyson.game.components.PlayerComponent;
import com.gledyson.game.components.TransformComponent;

public class CameraSystem extends IteratingSystem {
    private static final String TAG = CameraSystem.class.getSimpleName();

    private final Vector2 playerPos = new Vector2();
    private final Vector2 cameraPos = new Vector2();
    private final Vector2 direction = new Vector2();

    private final float PROGRESS = 5f;

//    private final float mapWidth;
//    private final float mapHeight;

    public CameraSystem(TiledMap map) {
        super(Family.all(PlayerComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PlayerComponent playerC = Mappers.player.get(entity);
        TransformComponent transformC = Mappers.transform.get(entity);
        Box2DBodyComponent bodyC = Mappers.body.get(entity);

        if (playerC.camera == null) return;

        float offsetX = 0f;
        if (bodyC.body.getLinearVelocity().x > 0) {
            offsetX = 2f;
        } else if (bodyC.body.getLinearVelocity().x < 0) {
            offsetX = -2f;
        }

        // makes camera follow player
        playerPos.set(transformC.position.x + offsetX, transformC.position.y + 2f);
        cameraPos.set(playerC.camera.position.x, playerC.camera.position.y);

        float distance = playerPos.dst2(cameraPos);

        if (distance > 2.5f) {
            direction.set(playerPos).sub(cameraPos).nor().scl(2 * distance * deltaTime);
            playerC.camera.translate(direction);
        }
    }
}

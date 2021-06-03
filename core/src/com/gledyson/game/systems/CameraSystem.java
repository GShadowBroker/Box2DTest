package com.gledyson.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
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

        MapProperties props = map.getProperties();

//        int mapWidthPixels = props.get("width", Integer.class);
//        int mapHeightPixels = props.get("height", Integer.class);
//
//        int tileWidthPixels = props.get("tilewidth", Integer.class);
//        int tileHeightPixels = props.get("tileheight", Integer.class);
//
//        mapWidth = (mapWidthPixels * tileWidthPixels) / RenderingSystem.PPM;
//        mapHeight = (mapHeightPixels * tileHeightPixels) / RenderingSystem.PPM;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PlayerComponent playerC = Mappers.player.get(entity);
        TransformComponent transformC = Mappers.transform.get(entity);

        if (playerC.camera == null) return;

        // makes camera follow player
        playerPos.set(transformC.position.x, transformC.position.y);
        cameraPos.set(playerC.camera.position.x, playerC.camera.position.y);

//        Gdx.app.log(TAG, "player (" +
//                playerPos.x + ", " + playerPos.y +
//                "), camera (" + cameraPos.x +
//                ", " + cameraPos.y + ")");

        if (playerPos.dst(cameraPos) > 4f) {
            direction.set(playerPos).sub(cameraPos).nor().scl(PROGRESS * deltaTime);
            playerC.camera.translate(direction);
        }
    }
}

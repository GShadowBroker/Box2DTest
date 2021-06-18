package com.gledyson.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.gledyson.game.components.Box2DBodyComponent;
import com.gledyson.game.components.GunComponent;
import com.gledyson.game.components.PlayerComponent;
import com.gledyson.game.components.StateComponent;
import com.gledyson.game.components.TextureComponent;
import com.gledyson.game.components.TransformComponent;

public class GunSystem extends IteratingSystem {
    private static final String TAG = GunSystem.class.getSimpleName();
    private final Entity player;
    private final Vector2 mouseDirection = new Vector2();
    private final Vector3 mousePos = new Vector3();

    public GunSystem(Entity playerEntity) {
        super(Family.all(GunComponent.class).get());
        this.player = playerEntity;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (Mappers.player.get(player) == null || Mappers.player.get(player).gun != entity) return;
        if (Mappers.player.get(player).isDead) return;
        if (Mappers.state.get(player).get() == StateComponent.State.DYING) return;

        TransformComponent playerTransC = Mappers.transform.get(player);
        PlayerComponent playerC = Mappers.player.get(player);

        Box2DBodyComponent gunBodyC = Mappers.body.get(entity);
        TransformComponent gunTransC = Mappers.transform.get(entity);
        TextureComponent textureC = Mappers.texture.get(entity);

        mousePos.set(Gdx.input.getX(), Gdx.input.getY(), 0f);

        playerC.camera.unproject(mousePos); // convert position from screen to box2d world
        mouseDirection.set(mousePos.x, mousePos.y)
                .sub(playerTransC.position.x, playerTransC.position.y).nor();

        float angle = mouseDirection.angleDeg();

        gunBodyC.body.setTransform(
                0.5f * MathUtils.cosDeg(angle) + playerTransC.position.x,
                0.5f * MathUtils.sinDeg(angle) + playerTransC.position.y,
                angle * MathUtils.degreesToRadians
        );
        gunTransC.rotation = angle;

        // flip texture once if angle > 180f
        if (angle > 90f && angle <= 270f) {
            if (!textureC.region.isFlipY()) {
                textureC.region.flip(false, true);
            }
        } else {
            // if flipped, unflip it once
            if (textureC.region.isFlipY()) {
                textureC.region.flip(false, true);
            }
        }
    }
}

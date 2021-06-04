package com.gledyson.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.gledyson.game.components.Box2DBodyComponent;
import com.gledyson.game.components.TransformComponent;

public class PhysicsSystem extends IteratingSystem {
    private static final String TAG = PhysicsSystem.class.getSimpleName();

    private static final float MAX_STEP_TIME = 1.0f / 45f;
    private static float accumulator = 0f;

    private final World world;
    private final PooledEngine engine;
    private final Array<Entity> bodiesQueue;

    private final float mapWidth;
    private final float mapHeight;

    public PhysicsSystem(World world, PooledEngine engine, TiledMap map) {
        super(Family.all(Box2DBodyComponent.class, TransformComponent.class).get());

        this.world = world;
        this.engine = engine;
        bodiesQueue = new Array<>();

        MapProperties props = map.getProperties();

        int mapWidthPixels = props.get("width", Integer.class);
        int mapHeightPixels = props.get("height", Integer.class);

        int tileWidthPixels = props.get("tilewidth", Integer.class);
        int tileHeightPixels = props.get("tileheight", Integer.class);

        mapWidth = (mapWidthPixels * tileWidthPixels) / RenderingSystem.PPM;
        mapHeight = (mapHeightPixels * tileHeightPixels) / RenderingSystem.PPM;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        accumulator += Math.min(deltaTime, 0.25f);
        if (accumulator >= MAX_STEP_TIME) {
            world.step(MAX_STEP_TIME, 6, 2);
            accumulator -= MAX_STEP_TIME;

            for (Entity entity : bodiesQueue) {
                TransformComponent transformC = Mappers.transform.get(entity);
                Box2DBodyComponent bodyC = Mappers.body.get(entity);

                // if out of map bounds, remove
                if (bodyC.body.getPosition().x > mapWidth + 10f ||
                        bodyC.body.getPosition().x < -10f ||
                        bodyC.body.getPosition().y > mapHeight + 10f ||
                        bodyC.body.getPosition().y < -10f
                ) {
                    Gdx.app.log(TAG, "OUT OF BOUNDS: body killed!");
                    bodyC.isDead = true;
                }

                // if dead, remove
                if (bodyC.isDead) {
                    Gdx.app.log(TAG, "DEAD: body removed from world!");
                    world.destroyBody(bodyC.body);
                    engine.removeEntity(entity);
                }

                transformC.position.x = bodyC.body.getPosition().x;
                transformC.position.y = bodyC.body.getPosition().y;
                if (transformC.rotation == 0f) {
                    transformC.rotation = bodyC.body.getAngle() * MathUtils.radiansToDegrees;
                }
            }
        }

        bodiesQueue.clear();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        bodiesQueue.add(entity);
    }
}

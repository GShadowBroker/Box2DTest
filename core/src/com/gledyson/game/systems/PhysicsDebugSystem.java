package com.gledyson.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.gledyson.game.Box2DGame;

public class PhysicsDebugSystem extends IteratingSystem {

    private final Box2DDebugRenderer debugRenderer;
    private final World world;
    private final OrthographicCamera camera;
    private final Box2DGame game;

    public PhysicsDebugSystem(World world, OrthographicCamera camera, Box2DGame game) {
        super(Family.all().get());

        debugRenderer = new Box2DDebugRenderer();
        this.world = world;
        this.camera = camera;
        this.game = game;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (game.debugRendererActive) debugRenderer.render(world, camera.combined);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

    }
}

package com.gledyson.game.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.gledyson.game.Box2DGame;
import com.gledyson.game.KeyboardController;
import com.gledyson.game.Player;
import com.gledyson.game.loaders.Box2DAssetManager;

public class Box2DModel implements Disposable {
    private static final String TAG = Box2DModel.class.getSimpleName();
    public final World world;
    public final Box2DGame game;
    public final Box2DDebugRenderer debugRenderer;
    public final KeyboardController controller;
    private final OrthographicCamera camera;

    public final Player player;

    private final Sound pingSound;
    private final Sound boingSound;

    public enum SoundEffect {
        PING, BOING
    }

    public boolean isSwimming = false;
    private float accumulator = 0f;

    private static final Vector2 GRAVITY_VECTOR = new Vector2(0f, -9.8f);
    private static final Vector2 UNDERWATER_FORCE_VECTOR = new Vector2(0f, 40f);

    private Body ground;
    private Body body;
    private Body movingBody;

    public Box2DModel(KeyboardController controller, OrthographicCamera camera, Box2DGame game) {
        // init world
        this.world = new World(GRAVITY_VECTOR, true);
        this.game = game;
        this.debugRenderer = new Box2DDebugRenderer(true, true, true, true, true, true);
        this.camera = camera;

        // Add controller and player
        this.controller = controller;
        this.player = new Player(new Vector2(1f, 1f), world, game);

        // add contact listener
        world.setContactListener(new Box2DContactListener(this));

        // Add sounds
        game.assetManager.queueAddSounds();
        game.assetManager.manager.finishLoading();

        pingSound = game.assetManager.manager.get(Box2DAssetManager.PING_SOUND);
        boingSound = game.assetManager.manager.get(Box2DAssetManager.BOING_SOUND);

        // add objects (DEBUG ONLY)
        Body ground = createFloor();
        ground.setUserData("GROUND");
        BodyFactory factory = BodyFactory.getInstance(world);
        Body water = factory.makeRectangle(0f, -7.9f, 40, 10, BodyFactory.Material.STEEL, BodyDef.BodyType.StaticBody, false);
        water.setUserData("WATER");
        factory.makeAllFixturesSensors(water);
    }

    public void logicStep(float delta) {
        handleInput();

        if (isSwimming) {
            player.body.applyForceToCenter(UNDERWATER_FORCE_VECTOR, true);
        }

        accumulator += Math.min(delta, 0.25f);
        while (accumulator >= 1 / 60f) {
            world.step(1 / 60f, 6, 2);
            accumulator -= 1 / 60f;
        }
    }

    public void handleInput() {
        if (controller.left) {
            player.body.applyForceToCenter(-10f, 0f, true);
        }
        if (controller.right) {
            player.body.applyForceToCenter(10f, 0f, true);
        }
        if (controller.up) {
            player.body.applyForceToCenter(0f, 10f, true);
        }
        if (controller.down) {
            player.body.applyForceToCenter(0f, -10f, true);
        }

        // Mouse
        if (controller.mouse1 && isMouseOnBody(player.body, controller.mousePos)) {
            Gdx.app.log(TAG, "mouse on player");

            playSound(SoundEffect.BOING);
        }
    }

    /**
     * Checks if point is in first fixture
     * Does not check all fixtures.....yet
     *
     * @param body     the Box2D body to check
     * @param mousePos the point on the screen
     * @return true if click is inside body
     */
    public boolean isMouseOnBody(Body body, Vector2 mousePos) {
        // converts vector2 to vector3
        Vector3 mousePos3 = new Vector3(mousePos, 0f);

        // converts screen coordinates to world coordinates
        camera.unproject(mousePos3);

        // check whether mouse position intersects body
        return body.getFixtureList().first().testPoint(mousePos3.x, mousePos3.y);
    }

    public void playSound(SoundEffect sound) {
        switch (sound) {
            case PING:
                pingSound.play(game.getPreferences().getSoundVolume());
                break;
            case BOING:
                boingSound.play(game.getPreferences().getSoundVolume());
                break;
            default:
                // do nothing
        }
    }

    public Body createFloor() {
        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.type = BodyDef.BodyType.StaticBody;
        groundBodyDef.position.set(new Vector2(0f, -10f));

        ground = world.createBody(groundBodyDef);

        PolygonShape groundBox = new PolygonShape();
        groundBox.setAsBox(50f, 0.1f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = groundBox;
        fixtureDef.density = 1f;
        fixtureDef.friction = 1f;

        ground.createFixture(fixtureDef);

        // stay green!
        groundBox.dispose();

        return ground;
    }

    public void createWall(float posX, float posY) {
        BodyDef wallDef = new BodyDef();
        wallDef.type = BodyDef.BodyType.StaticBody;
        wallDef.position.set(new Vector2(posX, posY));

        ground = world.createBody(wallDef);

        PolygonShape wallBox = new PolygonShape();
        wallBox.setAsBox(1f, 10f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = wallBox;
        fixtureDef.density = 1f;
        fixtureDef.friction = .5f;

        ground.createFixture(fixtureDef);

        // stay green!
        wallBox.dispose();
    }

    @Override
    public void dispose() {
        world.dispose();
        debugRenderer.dispose();
        player.dispose();
    }
}

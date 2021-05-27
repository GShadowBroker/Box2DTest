package com.gledyson.game;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.gledyson.game.loaders.Box2DAssetManager;
import com.gledyson.game.physics.BodyFactory;

public class Player implements Disposable {
    private final Box2DGame game;

    private final World world;
    public final Vector2 position;
    public final Body body;

    // Graphics
    private static final int FRAME_COLS = 2, FRAME_ROWS = 2;
    public Animation<TextureRegion> animation;

    // Time
    private float stateTime;
    private static final float FRAME_DURATION = 0.07f;

    public Player(Vector2 position, World world, Box2DGame game) {
        this.position = position;
        this.world = world;
        this.game = game;
        this.body = createBody(position);
        buildAnimation();
    }

    public void update() {
        position.set(body.getPosition());
    }

    public void draw(float delta) {
        stateTime += delta;

        TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
        game.batch.draw(currentFrame, position.x - 2f / 2f, position.y - 2f / 2f, 2f, 2f);
    }

    private void buildAnimation() {
        // Asset manager

        TextureAtlas atlas = game.assetManager.manager.get(Box2DAssetManager.GAME_ATLAS);
        TextureRegion player1 = atlas.findRegion("player-1");
        TextureRegion player2 = atlas.findRegion("player-2");
        TextureRegion player3 = atlas.findRegion("player-3");
        TextureRegion player4 = atlas.findRegion("player-4");

        TextureRegion[] playerAnimation = new TextureRegion[]{
                player1, player1, player1, player1,
                player1, player1, player1, player1,
                player1, player1, player1, player1,
                player1, player1, player1, player1,
                player1, player1, player1, player1,
                player1, player1, player1, player1,
                player2, player3, player4, player2,
                player1, player1, player1, player1,
                player1, player1, player1, player1,
                player1, player1, player1, player1,
                player1, player1, player1, player1,
                player1, player1, player1, player1,
                player1, player1, player1, player1,
                player2, player3, player4, player2,
                player1, player1, player1, player1,
                player1, player1, player1, player1,
                player1, player1, player1, player1,
                player1, player1, player1, player1,
                player1, player1, player1, player1,
                player1, player1, player1, player1,
                player2, player3, player4, player2,
                player2, player3, player4, player2
        };

        animation = new Animation<>(FRAME_DURATION, playerAnimation);
        this.stateTime = 0f;
    }

    private Body createBody(Vector2 position) {
        BodyFactory factory = BodyFactory.getInstance(world);
        return factory.makeRectangle(position.x, position.y, 2f, 2f,
                BodyFactory.Material.RUBBER, BodyDef.BodyType.DynamicBody, false);
    }

    @Override
    public void dispose() {
    }
}

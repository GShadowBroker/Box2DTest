package com.gledyson.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
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
    private static final float FRAME_DURATION = 0.125f;

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
        game.assetManager.queueAddImages();
        game.assetManager.manager.finishLoading();

        Texture playerSpriteSheet = game.assetManager.manager.get(Box2DAssetManager.PLAYER_IMG);

        TextureRegion[][] temp = TextureRegion.split(playerSpriteSheet,
                playerSpriteSheet.getWidth() / FRAME_COLS,
                playerSpriteSheet.getHeight() / FRAME_ROWS);

        TextureRegion[] playerAnimation = new TextureRegion[12 + (FRAME_COLS * FRAME_ROWS)];

        int index = 0;
        for (TextureRegion[] textureRegions : temp) {
            for (TextureRegion textureRegion : textureRegions) {
                playerAnimation[index] = textureRegion;
                index++;
            }
        }

        // Add still frames after wink frames
        for (int i = FRAME_COLS * FRAME_ROWS; i < 12 + (FRAME_COLS * FRAME_ROWS); i++) {
            playerAnimation[i] = temp[0][0];
        }

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

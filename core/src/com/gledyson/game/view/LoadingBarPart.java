package com.gledyson.game.view;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class LoadingBarPart extends Actor {
    private final TextureRegion textureRegion;
    private final Animation<TextureRegion> animation;
    private TextureRegion currentFrame;
    private float animationStateTime = 0f;

    public LoadingBarPart(TextureRegion textureRegion, Animation<TextureRegion> animation) {
        super();
        this.textureRegion = textureRegion;
        this.animation = animation;
        this.setWidth(30);
        this.setHeight(25);
        this.setVisible(false);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        animationStateTime += delta;
        currentFrame = animation.getKeyFrame(animationStateTime, true);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.draw(textureRegion, getX(), getY(), 30, 30);
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        batch.draw(currentFrame, getX() - 5, getY(), 40, 40);
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }
}

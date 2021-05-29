package com.gledyson.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.gledyson.game.components.TextureComponent;
import com.gledyson.game.components.TransformComponent;

import java.util.Comparator;

public class RenderingSystem extends SortedIteratingSystem {

    private final SpriteBatch batch;
    private final Array<Entity> renderQueue;
    private final OrthographicCamera camera;

    public static final float PPM = 32.0f; // PPM = pixels per meter
    public static final float PIXELS_TO_METERS = 1.0f / PPM;

    public static final float FRUSTUM_WIDTH = Gdx.graphics.getWidth() / PPM;
    public static final float FRUSTUM_HEIGHT = Gdx.graphics.getHeight() / PPM;

    // static method to get screen width in meters
    private static final Vector2 meterDimensions = new Vector2();
    private static final Vector2 pixelDimensions = new Vector2();

    // component mappers to get components from entities
    private final ComponentMapper<TextureComponent> textureCMapper;
    private final ComponentMapper<TransformComponent> transformCMapper;

    // compares vectors based on their z-axis value
    private final Comparator<Entity> comparator = new ZComparator();

    public RenderingSystem(SpriteBatch batch) {

        super(Family.all(TransformComponent.class, TextureComponent.class).get(), new ZComparator());

        this.batch = batch;
        this.camera = new OrthographicCamera(FRUSTUM_WIDTH, FRUSTUM_HEIGHT);
        camera.position.set(FRUSTUM_WIDTH / 2f, FRUSTUM_HEIGHT / 2f, 0f); // center

        this.renderQueue = new Array<>();

        textureCMapper = ComponentMapper.getFor(TextureComponent.class);
        transformCMapper = ComponentMapper.getFor(TransformComponent.class);
    }

    @Override
    public void update(float deltaTime) {

        super.update(deltaTime);

        // sort based on z
        renderQueue.sort(comparator);

        // update camera and sprite batch
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.enableBlending();

        batch.begin();
        drawEntities();
        batch.end();

        renderQueue.clear();
    }

    private void drawEntities() {

        for (Entity entity : renderQueue) {

            TextureComponent textureC = textureCMapper.get(entity);
            TransformComponent transformC = transformCMapper.get(entity);

            // don't render if no texture or is hidden
            if (textureC.region == null || transformC.isHidden) continue;

//            float width = textureC.region.getRegionWidth();
//            float height = textureC.region.getRegionHeight();
            float width = 1f * PPM;
            float height = 1f * PPM;

            // batch.draw(textureRegion, x, y, originX, originY, width, height, scaleX, scaleY, rotation);
            batch.draw(
                    textureC.region,
                    transformC.position.x - (width / 2f),
                    transformC.position.y - (height / 2f),
                    (width / 2f), (height / 2f),
                    width, height,
                    pixelsToMeters(transformC.scale.x), pixelsToMeters(transformC.scale.y),
                    transformC.rotation
            );
        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        renderQueue.add(entity);
    }

    public static Vector2 getScreenSizeInMeters() {
        meterDimensions.set(
                Gdx.graphics.getWidth() * PIXELS_TO_METERS,
                Gdx.graphics.getHeight() * PIXELS_TO_METERS
        );
        return meterDimensions;
    }

    public static Vector2 getScreenSizeInPixels() {
        pixelDimensions.set(
                Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight()
        );
        return pixelDimensions;
    }

    public static float pixelsToMeters(float pixels) {
        return pixels * PIXELS_TO_METERS;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public static class ZComparator implements Comparator<Entity> {

        private final ComponentMapper<TransformComponent> transCMapper =
                ComponentMapper.getFor(TransformComponent.class);

        @Override
        public int compare(Entity entityA, Entity entityB) {

            float az = transCMapper.get(entityA).position.z;
            float bz = transCMapper.get(entityB).position.z;

            if (az > bz) return 1;
            else if (az < bz) return -1;
            return 0;
        }
    }
}

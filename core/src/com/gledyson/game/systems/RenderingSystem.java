package com.gledyson.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.gledyson.game.Box2DGame;
import com.gledyson.game.components.TextureComponent;
import com.gledyson.game.components.TransformComponent;

import java.util.Comparator;

public class RenderingSystem extends SortedIteratingSystem {
    private static final String TAG = RenderingSystem.class.getSimpleName();

    private final SpriteBatch batch;
    private final Array<Entity> renderQueue;
    private final OrthographicCamera camera;

    private final OrthogonalTiledMapRenderer mapRenderer;
    public static final float PPM = 32.0f; // PPM = pixels per meter
    public static final float PIXELS_TO_METERS = 1.0f / PPM;

    public static final float FRUSTUM_WIDTH = Gdx.graphics.getWidth() / PPM;
    public static final float FRUSTUM_HEIGHT = Gdx.graphics.getHeight() / PPM;

    // static method to get screen width in meters
    private static final Vector2 meterDimensions = new Vector2();
    private static final Vector2 pixelDimensions = new Vector2();

    // compares vectors based on their z-axis value
    private final Comparator<Entity> comparator = new ZComparator();

    public RenderingSystem(Box2DGame game, OrthogonalTiledMapRenderer mapRenderer) {

        super(Family.all(TransformComponent.class, TextureComponent.class).get(), new ZComparator());

        this.batch = game.batch;
        this.camera = new OrthographicCamera(FRUSTUM_WIDTH, FRUSTUM_HEIGHT);
        camera.position.set(FRUSTUM_WIDTH / 2f, FRUSTUM_HEIGHT / 2f, 0f); // center
        this.mapRenderer = mapRenderer;

        this.renderQueue = new Array<>();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        // sort based on z
        renderQueue.sort(comparator);

        // update camera and sprite batch
        camera.update();
        mapRenderer.setView(camera);
        batch.setProjectionMatrix(camera.combined);

        // blending
        batch.enableBlending();
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // render map
        mapRenderer.render();

//        int[] backgroundLayers = {0};
//        int[] foregroundLayers = {1, 2};

        batch.begin();
        drawEntities();
        batch.end();

        renderQueue.clear();
    }

    private void drawEntities() {

        for (Entity entity : renderQueue) {

            TextureComponent textureC = Mappers.texture.get(entity);
            TransformComponent transformC = Mappers.transform.get(entity);

            // don't render if no texture or is hidden
            if (textureC.region == null || transformC.isHidden) continue;

            float width = textureC.region.getRegionWidth() / 4f;
            float height = textureC.region.getRegionHeight() / 4f;
//            float width = 1f * PPM;
//            float height = 1f * PPM;

            float x = transformC.position.x - (width / 2f);
            float y = transformC.position.y - (height / 2f);

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

        @Override
        public int compare(Entity entityA, Entity entityB) {

            float az = Mappers.transform.get(entityA).position.z;
            float bz = Mappers.transform.get(entityB).position.z;

            if (az > bz) return 1;
            else if (az < bz) return -1;
            return 0;
        }
    }
}

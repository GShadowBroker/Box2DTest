package com.gledyson.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.gledyson.game.LevelFactory;
import com.gledyson.game.components.PlayerComponent;
import com.gledyson.game.components.TransformComponent;

public class LevelGenerationSystem extends IteratingSystem {
    private static final String TAG = LevelGenerationSystem.class.getSimpleName();

    private final LevelFactory lvlFactory;

    public LevelGenerationSystem(LevelFactory levelFactory) {
        super(Family.all(PlayerComponent.class).get());
        this.lvlFactory = levelFactory;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TransformComponent transformC = Mappers.transform.get(entity);

        int currentPosition = (int) transformC.position.y;

//        Gdx.app.log(TAG, "currentPos: " + currentPosition + ", currentLevel: " + lvlFactory.currentLevel);

//        if ((currentPosition + 7) > lvlFactory.currentLevel) {
////            Gdx.app.log(TAG, "currentPos: " + (currentPosition + 7) + ", currentLevel: " + lvlFactory.currentLevel);
//            lvlFactory.generateLevel(currentPosition + 7);
//        }
    }
}

package com.gledyson.game.ai;

import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.ai.steer.behaviors.Flee;
import com.badlogic.gdx.ai.steer.behaviors.Seek;
import com.badlogic.gdx.ai.steer.behaviors.Wander;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.gledyson.game.components.SteeringComponent;

public class SteeringPresets {
    public static Wander<Vector2> getWander(SteeringComponent steeringComponent) {
        return new Wander<>(steeringComponent)
                .setFaceEnabled(false)
                .setWanderOffset(20f)
                .setWanderOrientation(0f)
                .setWanderRadius(10f)
                .setWanderRate(MathUtils.PI2 * 2);
    }

    public static Seek<Vector2> getSeek(SteeringComponent seeker, SteeringComponent target) {
        return new Seek<>(seeker, target);
    }

    public static Flee<Vector2> getFlee(SteeringComponent runner, SteeringComponent fleeingFrom) {
        return new Flee<>(runner, fleeingFrom);
    }

    public static Arrive<Vector2> getArrive(SteeringComponent runner, SteeringComponent target) {
        return getArrive(runner, target, 7f, 10f);
    }

    public static Arrive<Vector2> getArrive(
            SteeringComponent runner, SteeringComponent target,
            float arrivalTolerance, float decelerationRadius
    ) {
        return new Arrive<>(runner, target)
                .setTimeToTarget(0.1f)
                .setArrivalTolerance(arrivalTolerance)
                .setDecelerationRadius(decelerationRadius);
    }
}

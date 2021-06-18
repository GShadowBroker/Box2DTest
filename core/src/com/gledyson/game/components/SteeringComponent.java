package com.gledyson.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Pool;
import com.gledyson.game.utils.DFUtils;

public class SteeringComponent implements Steerable<Vector2>, Component, Pool.Poolable {

    public enum SteeringState {
        WANDER, SEEK, FLEE, ARRIVE, NONE
    }

    public SteeringState currentMode = SteeringState.WANDER;
    public Body body;

    // Steering data
    public float maxLinearSpeed = 2f;
    public float maxLinearAcceleration = 5f;
    public float maxAngularSpeed = 50f;
    public float maxAngularAcceleration = 5f;
    public float zeroThreshold = 0.1f;

    public SteeringBehavior<Vector2> steeringBehavior;
    private static final SteeringAcceleration<Vector2> steeringOutput =
            new SteeringAcceleration<>(new Vector2());
    private final float boundingRadius = 1f;
    private boolean tagged = true;
    private boolean independentFacing = false;

    @Override
    public void reset() {
        currentMode = SteeringState.NONE;
        body = null;
        steeringBehavior = null;
    }

    public boolean isIndependentFacing() {
        return independentFacing;
    }

    public void setIndependentFacing(boolean newIndependentFacing) {
        independentFacing = newIndependentFacing;
    }

    /**
     * Call this to update the steering behaviour (per frame)
     *
     * @param delta delta time between frames
     */
    public void update(float delta) {
        if (steeringBehavior != null) {
            steeringBehavior.calculateSteering(steeringOutput);
            applySteering(steeringOutput, delta);
        }
    }

    /**
     * apply steering to the Box2d body
     *
     * @param steeringOutput the steering vector
     * @param delta          the delta time
     */
    protected void applySteering(SteeringAcceleration<Vector2> steeringOutput, float delta) {
        boolean anyAccelerations = false;

        // update position and linear velocity
        if (!steeringOutput.linear.isZero()) {
            body.applyForceToCenter(steeringOutput.linear, true);
            anyAccelerations = true;
        }

        // update orientation and angular velocity
        if (isIndependentFacing()) {
            if (steeringOutput.angular != 0) {
                body.applyTorque(steeringOutput.angular, true);
                anyAccelerations = true;
            }
        } else {
            // if we haven't got and velocity, then we can do nothing
            if (!getLinearVelocity().isZero(zeroThreshold)) {
                float newOrientation = vectorToAngle(getLinearVelocity());
                body.setAngularVelocity((newOrientation - getAngularVelocity()) * delta); // superfluous is independentFacing is always true
                body.setTransform(getPosition(), newOrientation);
            }
        }

        if (anyAccelerations) {
            // cap linear speed
            Vector2 linearVelocity = getLinearVelocity();

            float currentSpeedSquare = linearVelocity.len2();
            float maxLinearSpeed = getMaxLinearSpeed();

            if (currentSpeedSquare > (maxLinearSpeed * maxLinearSpeed)) {
                body.setLinearVelocity(linearVelocity.scl(maxLinearSpeed / (float) Math.sqrt(currentSpeedSquare)));
            }

            // cap angular speed
            if (body.getAngularVelocity() > getMaxAngularSpeed()) {
                body.setAngularVelocity(getMaxAngularSpeed());
            }
        }
    }

    @Override
    public Vector2 getLinearVelocity() {
        return body.getLinearVelocity();
    }

    @Override
    public float getAngularVelocity() {
        return body.getAngularVelocity();
    }

    @Override
    public float getBoundingRadius() {
        return boundingRadius;
    }

    @Override
    public boolean isTagged() {
        return tagged;
    }

    @Override
    public void setTagged(boolean tagged) {
        this.tagged = tagged;
    }

    @Override
    public float getZeroLinearSpeedThreshold() {
        return zeroThreshold;
    }

    @Override
    public void setZeroLinearSpeedThreshold(float value) {
        zeroThreshold = value;
    }

    @Override
    public float getMaxLinearSpeed() {
        return maxLinearSpeed;
    }

    @Override
    public void setMaxLinearSpeed(float maxLinearSpeed) {
        this.maxLinearSpeed = maxLinearSpeed;
    }

    @Override
    public float getMaxLinearAcceleration() {
        return maxLinearAcceleration;
    }

    @Override
    public void setMaxLinearAcceleration(float maxLinearAcceleration) {
        this.maxLinearAcceleration = maxLinearAcceleration;
    }

    @Override
    public float getMaxAngularSpeed() {
        return maxAngularSpeed;
    }

    @Override
    public void setMaxAngularSpeed(float maxAngularSpeed) {
        this.maxAngularSpeed = maxAngularSpeed;
    }

    @Override
    public float getMaxAngularAcceleration() {
        return maxAngularAcceleration;
    }

    @Override
    public void setMaxAngularAcceleration(float maxAngularAcceleration) {
        this.maxAngularAcceleration = maxAngularAcceleration;
    }

    @Override
    public Vector2 getPosition() {
        return body.getPosition();
    }

    @Override
    public float getOrientation() {
        return body.getAngle();
    }

    @Override
    public void setOrientation(float orientation) {
        body.setTransform(getPosition(), orientation);
    }

    @Override
    public float vectorToAngle(Vector2 vector) {
        return DFUtils.vectorToAngle(vector);
    }

    @Override
    public Vector2 angleToVector(Vector2 outVector, float angle) {
        return DFUtils.angleToVector(outVector, angle);
    }

    @Override
    public Location<Vector2> newLocation() {
        return new Box2DLocation();
    }
}

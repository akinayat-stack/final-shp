package com.finalshp.memory;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class Card {
    public enum State {
        FACE_DOWN,
        FACE_UP,
        MATCHED
    }

    private final int id;
    private final Rectangle bounds;
    private final TextureRegion faceRegion;
    private final TextureRegion backRegion;

    private State state = State.FACE_DOWN;
    private float flipProgress = 0f;
    private boolean flipping = false;
    private float glowTime = 0f;

    public Card(int id, float x, float y, float width, float height, TextureRegion faceRegion, TextureRegion backRegion) {
        this.id = id;
        this.bounds = new Rectangle(x, y, width, height);
        this.faceRegion = faceRegion;
        this.backRegion = backRegion;
    }

    public void update(float delta, float pulseSpeed) {
        if (flipping) {
            flipProgress += delta * pulseSpeed;
            if (flipProgress >= 1f) {
                flipProgress = 1f;
                flipping = false;
            }
        }

        if (state == State.MATCHED) {
            glowTime += delta;
        }
    }

    public void setState(State newState) {
        if (state != newState) {
            state = newState;
            startFlip();
            if (newState == State.MATCHED) {
                glowTime = 0f;
            }
        }
    }

    public void forceState(State newState) {
        state = newState;
        flipProgress = 1f;
        flipping = false;
    }

    private void startFlip() {
        flipProgress = 0f;
        flipping = true;
    }

    public boolean contains(float x, float y) {
        return bounds.contains(x, y);
    }

    public float getVisualScaleX() {
        float t = MathUtils.clamp(flipProgress, 0f, 1f);
        return Math.abs((t * 2f) - 1f);
    }

    public boolean shouldDrawFace() {
        return state == State.MATCHED || state == State.FACE_UP;
    }

    public float getGlowIntensity() {
        if (state != State.MATCHED) {
            return 0f;
        }
        return 0.35f + 0.25f * MathUtils.sin(glowTime * 4f);
    }

    public int getId() {
        return id;
    }

    public State getState() {
        return state;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public TextureRegion getFaceRegion() {
        return faceRegion;
    }

    public TextureRegion getBackRegion() {
        return backRegion;
    }

    public boolean isAnimating() {
        return flipping;
    }
}

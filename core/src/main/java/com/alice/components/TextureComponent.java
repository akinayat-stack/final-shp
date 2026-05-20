package com.alice.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Pool;

public class TextureComponent implements Component, Pool.Poolable {
    public Texture texture;
    public Texture idleTexture;
    public Texture walkTexture;
    public boolean flipX = false;
    public boolean lastFlipX = false;
    public float renderWidth = 64f;
    public float renderHeight = 64f;
    public float stateTime = 0f;
    public boolean invincible = false;
    public float invincibleTimer = 0f;
    public boolean visible = true;

    @Override
    public void reset() {
        texture = null;
        idleTexture = null;
        walkTexture = null;
        flipX = false;
        lastFlipX = false;
        renderWidth = 64f;
        renderHeight = 64f;
        stateTime = 0f;
        invincible = false;
        invincibleTimer = 0f;
        visible = true;
    }
}

package com.alice.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Pool;

public class TextureComponent implements Component, Pool.Poolable {
    public Texture texture;

    // Старые текстуры (для NPC / совместимость)
    public Texture idleTexture;
    public Texture walkTexture;
    public Texture backTexture;

    // Спрайтшиты Алисы (4 направления, 4 кадра каждый)
    public Texture walkFrontSheet;   // идёт прямо (к камере)
    public Texture walkBackSheet;    // идёт назад (от камеры)
    public Texture walkLeftSheet;    // идёт влево
    public Texture walkRightSheet;   // идёт вправо

    // Направление движения игрока
    public enum Direction { DOWN, UP, LEFT, RIGHT }
    public Direction direction = Direction.DOWN;

    // Анимация
    public float stateTime = 0f;
    public static final int FRAME_COUNT = 4;
    public static final float FRAME_DURATION = 0.15f; // сек на кадр

    public boolean flipX = false;
    public boolean lastFlipX = false;
    public float renderWidth = 64f;
    public float renderHeight = 64f;
    public boolean invincible = false;
    public float invincibleTimer = 0f;
    public boolean visible = true;

    @Override
    public void reset() {
        texture = null;
        idleTexture = null;
        walkTexture = null;
        backTexture = null;
        walkFrontSheet = null;
        walkBackSheet = null;
        walkLeftSheet = null;
        walkRightSheet = null;
        direction = Direction.DOWN;
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

package com.alice.utils;

public final class Constants {
    public static final float TILE_SIZE = 64f;

    public static final float PLAYER_SIZE      = 80f;   // было 80 — увеличено
    public static final float PLAYER_COLLISION = 48f;
    public static final float GUARD_SIZE       = 80f;
    public static final float GUARD_COLLISION  = 48f;
    public static final float VALET_SIZE       = 80f;
    public static final float VALET_COLLISION  = 64f;
    public static final float KEY_SIZE         = 40f;
    public static final float HEART_SIZE       = 40f;

    public static final float PLAYER_SPEED       = 140f;
    public static final float GUARD_SPEED        = 70f;
    public static final float GUARD_CHASE_SPEED  = 105f;
    public static final float GUARD_RETURN_SPEED = 80f;
    public static final float VALET_SPEED        = 85f;
    public static final float VALET_CHASE_SPEED  = 125f;

    public static final float DETECTION_RADIUS    = 170f;
    public static final float VALET_DETECT_RADIUS = 240f;
    public static final float BUSH_DETECT_MULT    = 0.5f;

    public static final int   PLAYER_LIVES       = 3;
    public static final float INVINCIBILITY_TIME = 2f;
    public static final float CAMERA_LERP        = 5f;
    public static final float KEY_BOB_SPEED      = 3f;
    public static final float KEY_BOB_AMPLITUDE  = 3f;
    public static final float VIEWPORT_W         = 800f;
    public static final float VIEWPORT_H         = 480f;
    public static final int   TOTAL_KEYS         = 3;

    private Constants() {}
}

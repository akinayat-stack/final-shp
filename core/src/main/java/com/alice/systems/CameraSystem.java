package com.alice.systems;

import com.alice.components.PositionComponent;
import com.alice.screens.GameScreen;
import com.alice.utils.Constants;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;

public class CameraSystem extends EntitySystem {
    private final GameScreen screen;

    public CameraSystem(GameScreen screen) {
        super(4);
        this.screen = screen;
    }

    @Override
    public void update(float deltaTime) {
        PositionComponent p = screen.player.getComponent(PositionComponent.class);
        float targetX = p.x + 32;
        float targetY = p.y + 32;
        float curX = screen.worldCam.position.x;
        float curY = screen.worldCam.position.y;
        float lerp = Constants.CAMERA_LERP * deltaTime;
        if (lerp > 1f) lerp = 1f;
        float nx = curX + (targetX - curX) * lerp;
        float ny = curY + (targetY - curY) * lerp;

        float mapW = screen.map[0].length * Constants.TILE_SIZE;
        float mapH = screen.map.length * Constants.TILE_SIZE;
        float halfW = Constants.VIEWPORT_W / 2f;
        float halfH = Constants.VIEWPORT_H / 2f;
        nx = MathUtils.clamp(nx, halfW, mapW - halfW);
        ny = MathUtils.clamp(ny, halfH, mapH - halfH);
        if (mapW < Constants.VIEWPORT_W) nx = mapW / 2f;
        if (mapH < Constants.VIEWPORT_H) ny = mapH / 2f;

        screen.worldCam.position.set(nx, ny, 0);
        screen.worldCam.update();
    }
}

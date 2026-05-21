package com.alice.systems;

import java.util.ArrayList;
import java.util.List;

import com.alice.components.PositionComponent;
import com.alice.components.StateComponent;
import com.alice.components.TextureComponent;
import com.alice.screens.GameScreen;
import com.alice.utils.Constants;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class RenderSystem extends EntitySystem {
    private final GameScreen screen;
    private ImmutableArray<Entity> entities;

    public RenderSystem(GameScreen screen) {
        super(5);
        this.screen = screen;
    }

    @Override
    public void addedToEngine(com.badlogic.ashley.core.Engine engine) {
        entities = engine.getEntitiesFor(Family.all(PositionComponent.class, TextureComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        screen.worldCam.update();

        screen.game.batch.setProjectionMatrix(screen.worldCam.combined);
        screen.game.batch.begin();
        screen.mapRenderer.render(screen.game.batch, screen.worldCam, screen);

        Texture keyTex = screen.game.assets.get("key.png", Texture.class);
        float bob = (float) Math.sin(screen.keyBobTime * Constants.KEY_BOB_SPEED) * Constants.KEY_BOB_AMPLITUDE;
        float keyOff = (Constants.TILE_SIZE - Constants.KEY_SIZE) / 2f;
        for (int i = 0; i < screen.keyPositions.size(); i++) {
            if (screen.keyCollected.get(i)) continue;
            Vector2 kp = screen.keyPositions.get(i);
            screen.game.batch.draw(keyTex, kp.x + keyOff, kp.y + keyOff + bob, Constants.KEY_SIZE, Constants.KEY_SIZE);
        }

        List<Entity> sorted = new ArrayList<>();
        for (Entity e : entities) sorted.add(e);
        sorted.sort((a, b) -> Float.compare(
            b.getComponent(PositionComponent.class).y,
            a.getComponent(PositionComponent.class).y));
        for (Entity e : sorted) {
            StateComponent s = e.getComponent(StateComponent.class);
            if (s != null && s.isPlayer) continue;
            drawEntity(e);
        }

        drawEntity(screen.player);

        screen.game.batch.end();

        drawChaseIndicators();
        drawHud();
    }

    private void drawEntity(Entity e) {
        PositionComponent p = e.getComponent(PositionComponent.class);
        TextureComponent t = e.getComponent(TextureComponent.class);
        if (t.texture == null) return;
        if (!t.visible) return;
        Texture tex = t.texture;
        screen.game.batch.draw(
            tex,
            p.x, p.y,
            0, 0,
            t.renderWidth, t.renderHeight,
            1f, 1f,
            0f,
            0, 0,
            tex.getWidth(), tex.getHeight(),
            t.flipX, false
        );
    }




    private void drawChaseIndicators() {
        screen.game.batch.setProjectionMatrix(screen.hudCam.combined);
        screen.game.batch.begin();
        for (Entity g : screen.guards) {
            StateComponent s = g.getComponent(StateComponent.class);
            if (s.current != StateComponent.State.CHASE) continue;
            drawIndicator(g);
        }
        if (screen.valet != null) {
            StateComponent s = screen.valet.getComponent(StateComponent.class);
            if (s.current == StateComponent.State.CHASE) drawIndicator(screen.valet);
        }
        screen.game.batch.end();
    }

    private void drawIndicator(Entity e) {
        PositionComponent p = e.getComponent(PositionComponent.class);
        Vector3 v = new Vector3(p.x + 32, p.y + 80, 0);
        screen.worldCam.project(v);
        screen.game.font.setColor(Color.RED);
        screen.game.font.getData().setScale(2f);
        screen.game.font.draw(screen.game.batch, "!", v.x, v.y);
        screen.game.font.getData().setScale(1.2f);
    }

    private void drawHud() {
        screen.game.batch.setProjectionMatrix(screen.hudCam.combined);
        screen.game.batch.begin();
        Texture heart = screen.game.assets.get("heart.png", Texture.class);
        for (int i = 0; i < Constants.PLAYER_LIVES; i++) {
            if (i < screen.lives) screen.game.batch.setColor(Color.WHITE);
            else screen.game.batch.setColor(0.3f, 0.3f, 0.3f, 1f);
            screen.game.batch.draw(heart, 10 + i * 48, 432, Constants.HEART_SIZE, Constants.HEART_SIZE);
        }
        screen.game.batch.setColor(Color.WHITE);

        screen.game.font.setColor(Color.GOLD);
        screen.game.font.draw(screen.game.batch,
            "KEYS: " + screen.keysCollected + " / " + Constants.TOTAL_KEYS, 340, 470);
        screen.game.font.setColor(Color.WHITE);
        screen.game.font.draw(screen.game.batch, "LEVEL: " + screen.level, 700, 470);
        screen.game.batch.end();
    }
}

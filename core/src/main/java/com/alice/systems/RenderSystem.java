package com.alice.systems;

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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RenderSystem extends EntitySystem {
    private final GameScreen screen;
    private ImmutableArray<Entity> entities;

    public RenderSystem(GameScreen screen) {
        super(5);
        this.screen = screen;
    }

    private void drawRabbitHole() {
        com.badlogic.gdx.graphics.glutils.ShapeRenderer sr = screen.mapRenderer.getShapes();
        sr.setProjectionMatrix(screen.worldCam.combined);
        com.badlogic.gdx.Gdx.gl.glEnable(com.badlogic.gdx.graphics.GL20.GL_BLEND);
        com.badlogic.gdx.Gdx.gl.glBlendFunc(com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA,
            com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA);

        float cx = screen.exitPos.x + 32;
        float cy = screen.exitPos.y + 32;
        float t = screen.keyBobTime;

        sr.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled);

        sr.setColor(0.18f, 0.10f, 0.04f, 1f);
        sr.ellipse(cx - 38, cy - 24, 76, 48);

        sr.setColor(0.28f, 0.16f, 0.07f, 1f);
        sr.ellipse(cx - 36, cy - 22, 72, 44);

        sr.setColor(0.42f, 0.26f, 0.12f, 1f);
        sr.ellipse(cx - 34, cy - 20, 68, 40);

        sr.setColor(0.55f, 0.36f, 0.18f, 1f);
        sr.ellipse(cx - 32, cy - 18, 64, 36);

        sr.setColor(0.65f, 0.44f, 0.22f, 1f);
        sr.ellipse(cx - 30, cy - 16, 60, 32);

        int seed = (int)(screen.exitPos.x * 7 + screen.exitPos.y * 13);

        sr.setColor(0.40f, 0.24f, 0.10f, 1f);
        for (int i = 0; i < 8; i++) {
            float angle = ((seed + i * 41) % 360) * com.badlogic.gdx.math.MathUtils.degreesToRadians;
            float dist = 22 + ((seed + i * 17) % 12);
            float bx = cx + (float) Math.cos(angle) * dist;
            float by = cy + (float) Math.sin(angle) * dist * 0.55f;
            sr.circle(bx, by, 1.8f);
        }

        sr.setColor(0.72f, 0.50f, 0.26f, 1f);
        for (int i = 0; i < 6; i++) {
            float angle = ((seed * 3 + i * 53) % 360) * com.badlogic.gdx.math.MathUtils.degreesToRadians;
            float dist = 18 + ((seed + i * 11) % 10);
            float bx = cx + (float) Math.cos(angle) * dist;
            float by = cy + (float) Math.sin(angle) * dist * 0.55f;
            sr.circle(bx, by, 1.4f);
        }

        sr.setColor(0.24f, 0.50f, 0.18f, 1f);
        for (int i = 0; i < 10; i++) {
            float angle = ((seed + i * 37) % 360) * com.badlogic.gdx.math.MathUtils.degreesToRadians;
            float dist = 28 + ((seed + i * 19) % 8);
            float bx = cx + (float) Math.cos(angle) * dist;
            float by = cy + (float) Math.sin(angle) * dist * 0.6f;
            float wave = (float) Math.sin(t * 1.5f + i * 0.7f) * 1.2f;
            sr.rectLine(bx, by, bx + wave, by + 4 + (i % 2), 1.3f);
        }

        sr.setColor(0.36f, 0.66f, 0.22f, 1f);
        for (int i = 0; i < 7; i++) {
            float angle = ((seed * 5 + i * 47) % 360) * com.badlogic.gdx.math.MathUtils.degreesToRadians;
            float dist = 30 + ((seed + i * 13) % 6);
            float bx = cx + (float) Math.cos(angle) * dist;
            float by = cy + (float) Math.sin(angle) * dist * 0.6f;
            float wave = (float) Math.sin(t * 2f + i * 0.5f) * 1f;
            sr.rectLine(bx, by, bx + wave, by + 3 + (i % 2), 1f);
        }

        sr.setColor(0.10f, 0.05f, 0.02f, 1f);
        sr.ellipse(cx - 26, cy - 14, 52, 28);

        sr.setColor(0.06f, 0.03f, 0.01f, 1f);
        sr.ellipse(cx - 23, cy - 12, 46, 24);

        sr.setColor(0.02f, 0.01f, 0f, 1f);
        sr.ellipse(cx - 20, cy - 10, 40, 20);

        sr.setColor(0f, 0f, 0f, 1f);
        sr.ellipse(cx - 17, cy - 8, 34, 16);

        sr.setColor(0.30f, 0.18f, 0.08f, 0.7f);
        sr.ellipse(cx - 17, cy - 9, 34, 4);

        sr.setColor(0.55f, 0.36f, 0.18f, 0.5f);
        sr.ellipse(cx - 14, cy + 4, 28, 3);

        sr.setColor(0.45f, 0.28f, 0.14f, 1f);
        for (int i = 0; i < 5; i++) {
            float angle = (seed * 5 + i * 72) * com.badlogic.gdx.math.MathUtils.degreesToRadians;
            float dist = 14;
            float rx = cx + (float) Math.cos(angle) * dist;
            float ry = cy + (float) Math.sin(angle) * dist * 0.5f - 3;
            sr.circle(rx, ry, 1.6f);
        }

        sr.end();

        sr.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Line);
        sr.setColor(0.30f, 0.55f, 0.20f, 0.6f);
        sr.ellipse(cx - 33, cy - 19, 66, 38);
        sr.end();
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
        screen.mapRenderer.renderFloors(screen.game.batch, screen.worldCam);
        screen.game.batch.end();

        drawRabbitHole();

        screen.game.batch.setProjectionMatrix(screen.worldCam.combined);
        screen.game.batch.begin();

        screen.mapRenderer.renderBushes(screen.game.batch, screen.worldCam);

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
        sorted.sort(new Comparator<Entity>() {
            @Override
            public int compare(Entity a, Entity b) {
                return Float.compare(
                    b.getComponent(PositionComponent.class).y,
                    a.getComponent(PositionComponent.class).y);
            }
        });
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

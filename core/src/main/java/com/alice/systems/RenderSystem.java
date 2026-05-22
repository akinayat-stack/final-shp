package com.alice.systems;

import java.util.ArrayList;
import java.util.List;

import com.alice.components.PositionComponent;
import com.alice.components.StateComponent;
import com.alice.components.TextureComponent;
import com.alice.components.VelocityComponent;
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

    // У Алисы 4 кадра, у валета 2 кадра
    private static final int ALICE_FRAMES = 4;
    private static final int VALET_FRAMES = 2;
    private static final float FRAME_DURATION = 0.18f;

    public RenderSystem(GameScreen screen) {
        super(5);
        this.screen = screen;
    }

    @Override
    public void addedToEngine(com.badlogic.ashley.core.Engine engine) {
        entities = engine.getEntitiesFor(
            Family.all(PositionComponent.class, TextureComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        screen.worldCam.update();
        screen.game.batch.setProjectionMatrix(screen.worldCam.combined);
        screen.game.batch.begin();
        screen.mapRenderer.render(screen.game.batch, screen.worldCam, screen);

        // Предметы уровня
        Texture itemTex = screen.itemTexture;
        float bob = (float) Math.sin(screen.itemBobTime * Constants.KEY_BOB_SPEED) * Constants.KEY_BOB_AMPLITUDE;
        float off = (Constants.TILE_SIZE - Constants.KEY_SIZE) / 2f;
        for (int i = 0; i < screen.itemPositions.size(); i++) {
            if (screen.itemCollected.get(i)) continue;
            Vector2 pos = screen.itemPositions.get(i);
            screen.game.batch.draw(itemTex, pos.x + off, pos.y + off + bob, Constants.KEY_SIZE, Constants.KEY_SIZE);
        }

        // Сортировка по Y
        List<Entity> sorted = new ArrayList<>();
        for (Entity e : entities) sorted.add(e);
        sorted.sort((a, b) -> Float.compare(
            b.getComponent(PositionComponent.class).y,
            a.getComponent(PositionComponent.class).y));

        // NPC
        for (Entity e : sorted) {
            StateComponent s = e.getComponent(StateComponent.class);
            if (s != null && s.isPlayer) continue;
            drawAnimated(e, deltaTime, VALET_FRAMES);
        }

        // Игрок поверх всех
        drawAnimated(screen.player, deltaTime, ALICE_FRAMES);

        screen.game.batch.end();
        drawChaseIndicators();
        drawHud();
    }

    /**
     * Универсальная анимация по спрайтшиту для любой сущности.
     * frameCount — кол-во кадров в ряд (4 у Алисы, 2 у валета).
     */
    private void drawAnimated(Entity entity, float deltaTime, int frameCount) {
        if (entity == null) return;
        PositionComponent pos = entity.getComponent(PositionComponent.class);
        TextureComponent  tc  = entity.getComponent(TextureComponent.class);
        VelocityComponent vc  = entity.getComponent(VelocityComponent.class);
        if (tc == null || !tc.visible) return;

        // Если нет спрайтшитов — рисуем старой текстурой (fallback)
        if (tc.walkFrontSheet == null) {
            if (tc.texture == null) return;
            screen.game.batch.draw(tc.texture, pos.x, pos.y, tc.renderWidth, tc.renderHeight);
            return;
        }

        boolean moving = (vc != null) && (Math.abs(vc.vx) > 1f || Math.abs(vc.vy) > 1f);

        // Обновляем направление при движении
        if (moving && vc != null) {
            if (Math.abs(vc.vx) >= Math.abs(vc.vy)) {
                tc.direction = (vc.vx > 0)
                    ? TextureComponent.Direction.RIGHT
                    : TextureComponent.Direction.LEFT;
            } else {
                tc.direction = (vc.vy > 0)
                    ? TextureComponent.Direction.UP
                    : TextureComponent.Direction.DOWN;
            }
        }

        if (moving) {
            tc.stateTime += deltaTime;
        } else {
            tc.stateTime = 0f;
        }

        Texture sheet = getSheet(tc);
        if (sheet == null) return;

        int frameIndex = moving
            ? ((int)(tc.stateTime / FRAME_DURATION)) % frameCount
            : 0;

        int frameW = sheet.getWidth() / frameCount;
        int frameH = sheet.getHeight();
        int srcX   = frameIndex * frameW;

        // Мерцание при неуязвимости (только для игрока)
        StateComponent sc = entity.getComponent(StateComponent.class);
        if (sc != null && sc.isPlayer && tc.invincible) {
            tc.invincibleTimer -= deltaTime;
            boolean blink = ((int)(tc.invincibleTimer * 8)) % 2 == 0;
            if (!blink) screen.game.batch.setColor(1f, 1f, 1f, 0.3f);
            if (tc.invincibleTimer <= 0f) {
                tc.invincible = false;
                tc.invincibleTimer = 0f;
            }
        }

        screen.game.batch.draw(
            sheet,
            pos.x, pos.y,
            tc.renderWidth, tc.renderHeight,
            srcX, 0,
            frameW, frameH,
            false, false
        );

        screen.game.batch.setColor(Color.WHITE);
    }

    private Texture getSheet(TextureComponent tc) {
        switch (tc.direction) {
            case UP:    return tc.walkBackSheet;
            case DOWN:  return tc.walkFrontSheet;
            case LEFT:  return tc.walkLeftSheet;
            case RIGHT: return tc.walkRightSheet;
            default:    return tc.walkFrontSheet;
        }
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
            screen.currentItemType.label + ": " + screen.itemsCollected + " / " + Constants.TOTAL_ITEMS,
            340, 470);
        screen.game.font.setColor(Color.WHITE);
        screen.game.font.draw(screen.game.batch, "LEVEL: " + screen.level, 700, 470);
        screen.game.batch.end();
    }
}

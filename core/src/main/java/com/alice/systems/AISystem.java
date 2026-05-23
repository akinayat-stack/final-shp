package com.alice.systems;

import com.alice.ai.ChaseBehavior;
import com.alice.ai.GuardBehavior;
import com.alice.ai.PatrolBehavior;
import com.alice.ai.ReturnBehavior;
import com.alice.components.PositionComponent;
import com.alice.components.StateComponent;
import com.alice.components.VelocityComponent;
import com.alice.screens.GameScreen;
import com.alice.utils.Constants;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

public class AISystem extends IteratingSystem {
    private final GameScreen screen;
    private final GuardBehavior patrol = new PatrolBehavior();
    private final GuardBehavior chase  = new ChaseBehavior();
    private final GuardBehavior returnB = new ReturnBehavior();

    public AISystem(GameScreen screen) {
        super(Family.all(StateComponent.class, PositionComponent.class, VelocityComponent.class).get(), 3);
        this.screen = screen;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        StateComponent s = entity.getComponent(StateComponent.class);
        if (s.isPlayer) {
            handlePlayerInput(entity, deltaTime);
            return;
        }
        if (!s.isValet) return;

        PositionComponent ep = entity.getComponent(PositionComponent.class);
        PositionComponent pp = screen.player.getComponent(PositionComponent.class);
        StateComponent playerState = screen.player.getComponent(StateComponent.class);
        if (playerState.invisible && s.current == StateComponent.State.CHASE) {
            s.current = StateComponent.State.RETURN;
            s.lostSightTimer = 0f;
        }
        boolean detected = canDetectPlayer(ep, pp, s);

        if (detected) {
            if (s.current != StateComponent.State.CHASE) s.current = StateComponent.State.CHASE;
            s.lostSightTimer = 0;
        } else {
            if (s.current == StateComponent.State.CHASE) {
                s.lostSightTimer += deltaTime;
                if (s.lostSightTimer > 2f) {
                    s.current = StateComponent.State.RETURN;
                }
            }
        }

        switch (s.current) {
            case PATROL: patrol.update(deltaTime, entity, screen); break;
            case CHASE:  chase.update(deltaTime, entity, screen);  break;
            case RETURN: returnB.update(deltaTime, entity, screen); break;
            default: s.current = StateComponent.State.PATROL;
        }
    }

    private boolean canDetectPlayer(PositionComponent ep, PositionComponent pp, StateComponent s) {
        StateComponent playerState = screen.player.getComponent(StateComponent.class);
        if (playerState.invisible) return false;

        float cx = ep.x + 32;
        float cy = ep.y + 32;
        float px = pp.x + 32;
        float py = pp.y + 32;
        float dx = px - cx;
        float dy = py - cy;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);
        float radius = s.detectRadius;
        if (dist > radius) return false;
        return !rayBlocked(cx, cy, px, py);
    }

    private boolean rayBlocked(float x1, float y1, float x2, float y2) {
        int steps = 20;
        int[][] map = screen.map;
        int rows = map.length;
        int cols = map[0].length;
        for (int i = 1; i < steps; i++) {
            float t = i / (float) steps;
            float x = x1 + (x2 - x1) * t;
            float y = y1 + (y2 - y1) * t;
            int col = (int) (x / Constants.TILE_SIZE);
            int row = (int) (y / Constants.TILE_SIZE);
            int mapRow = rows - 1 - row;
            if (col < 0 || col >= cols || mapRow < 0 || mapRow >= rows) continue;
            if (map[mapRow][col] == 1) return true;
        }
        return false;
    }

    private void handlePlayerInput(Entity player, float delta) {
        VelocityComponent v = player.getComponent(VelocityComponent.class);
        com.alice.components.TextureComponent t = player.getComponent(com.alice.components.TextureComponent.class);
        StateComponent s = player.getComponent(StateComponent.class);

        v.vx = 0; v.vy = 0;
        boolean movingUp    = false;
        boolean movingHoriz = false;

        // Спрайт по умолчанию смотрит ВПРАВО:
        //   движение влево  → flipX = true  (зеркалим)
        //   движение вправо → flipX = false (как есть)
        if (com.badlogic.gdx.Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.A) ||
            com.badlogic.gdx.Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.LEFT)) {
            v.vx = -Constants.PLAYER_SPEED;
            movingHoriz = true;
            t.lastFlipX = true;   // влево — зеркалим
        } else if (com.badlogic.gdx.Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.D) ||
                   com.badlogic.gdx.Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.RIGHT)) {
            v.vx = Constants.PLAYER_SPEED;
            movingHoriz = true;
            t.lastFlipX = false;  // вправо — без флипа
        }

        if (com.badlogic.gdx.Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.W) ||
            com.badlogic.gdx.Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.UP)) {
            v.vy = Constants.PLAYER_SPEED;
            movingUp = true;
        } else if (com.badlogic.gdx.Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.S) ||
                   com.badlogic.gdx.Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.DOWN)) {
            v.vy = -Constants.PLAYER_SPEED;
        }

        boolean moving = (v.vx != 0 || v.vy != 0);

        if (!moving) {
            s.current = StateComponent.State.IDLE;
            t.texture = t.idleTexture;
            t.flipX = t.lastFlipX;
        } else if (movingUp && !movingHoriz) {
            // Только вперёд (W/UP) — кадр сзади, без флипа
            s.current = StateComponent.State.WALK;
            t.texture = t.backTexture;
            t.flipX = false;
        } else {
            // Влево / вправо — боковой кадр
            s.current = StateComponent.State.WALK;
            t.texture = t.walkTexture;
            t.flipX = t.lastFlipX;
        }

        // Инвинсибилити-мигание
        if (t.invincible) {
            t.invincibleTimer -= delta;
            t.stateTime += delta;
            t.visible = ((int)(t.stateTime / 0.15f)) % 2 == 0;
            if (t.invincibleTimer <= 0) {
                t.invincible = false;
                t.visible = true;
            }
        } else {
            t.visible = true;
        }
    }
}

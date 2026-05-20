package com.alice.ai;

import com.alice.components.PositionComponent;
import com.alice.components.StateComponent;
import com.alice.components.TextureComponent;
import com.alice.components.VelocityComponent;
import com.alice.screens.GameScreen;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;

public class ChaseBehavior implements GuardBehavior {
    @Override
    public void update(float delta, Entity guard, GameScreen screen) {
        StateComponent s = guard.getComponent(StateComponent.class);
        PositionComponent p = guard.getComponent(PositionComponent.class);
        PositionComponent pp = screen.player.getComponent(PositionComponent.class);
        VelocityComponent v = guard.getComponent(VelocityComponent.class);
        TextureComponent t = guard.getComponent(TextureComponent.class);

        s.repathTimer -= delta;

        boolean directSight = Pathfinder.hasLineOfSight(screen.map,
            p.x + 32, p.y + 32, pp.x + 32, pp.y + 32);

        if (directSight) {
            float dx = pp.x - p.x;
            float dy = pp.y - p.y;
            float dist = (float) Math.sqrt(dx * dx + dy * dy);
            if (dist < 1f) { v.vx = 0; v.vy = 0; return; }
            v.vx = (dx / dist) * s.chaseSpeed;
            v.vy = (dy / dist) * s.chaseSpeed;
            s.currentPath.clear();
            s.pathIndex = 0;
        } else {
            if (s.repathTimer <= 0f || s.currentPath.isEmpty() || s.pathIndex >= s.currentPath.size()) {
                s.currentPath = Pathfinder.findPath(screen.map, p.x, p.y, pp.x, pp.y);
                s.pathIndex = 0;
                s.repathTimer = 0.4f;
            }
            followPath(p, v, s, s.chaseSpeed);
        }

        checkStuck(p, v, s, delta);

        if (v.vx > 0) { t.flipX = false; t.lastFlipX = false; }
        else if (v.vx < 0) { t.flipX = true; t.lastFlipX = true; }
    }

    private void followPath(PositionComponent p, VelocityComponent v, StateComponent s, float speed) {
        if (s.currentPath.isEmpty() || s.pathIndex >= s.currentPath.size()) {
            v.vx = 0; v.vy = 0;
            return;
        }
        Vector2 target = s.currentPath.get(s.pathIndex);
        float dx = target.x - p.x;
        float dy = target.y - p.y;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);
        if (dist < 8f) {
            s.pathIndex++;
            if (s.pathIndex >= s.currentPath.size()) {
                v.vx = 0; v.vy = 0;
                return;
            }
            target = s.currentPath.get(s.pathIndex);
            dx = target.x - p.x;
            dy = target.y - p.y;
            dist = (float) Math.sqrt(dx * dx + dy * dy);
            if (dist < 1f) { v.vx = 0; v.vy = 0; return; }
        }
        v.vx = (dx / dist) * speed;
        v.vy = (dy / dist) * speed;
    }

    private void checkStuck(PositionComponent p, VelocityComponent v, StateComponent s, float delta) {
        float moved = Math.abs(p.x - s.lastX) + Math.abs(p.y - s.lastY);
        if (moved < 0.5f && (v.vx != 0 || v.vy != 0)) {
            s.stuckTimer += delta;
            if (s.stuckTimer > 0.5f) {
                s.repathTimer = 0f;
                s.stuckTimer = 0f;
            }
        } else {
            s.stuckTimer = 0f;
        }
        s.lastX = p.x;
        s.lastY = p.y;
    }
}

package com.alice.ai;

import com.alice.components.PositionComponent;
import com.alice.components.StateComponent;
import com.alice.components.TextureComponent;
import com.alice.components.VelocityComponent;
import com.alice.screens.GameScreen;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;

public class PatrolBehavior implements GuardBehavior {
    @Override
    public void update(float delta, Entity guard, GameScreen screen) {
        StateComponent s = guard.getComponent(StateComponent.class);
        PositionComponent p = guard.getComponent(PositionComponent.class);
        VelocityComponent v = guard.getComponent(VelocityComponent.class);
        TextureComponent t = guard.getComponent(TextureComponent.class);

        if (s.waypoints.isEmpty()) {
            v.vx = 0; v.vy = 0;
            return;
        }

        s.repathTimer -= delta;
        Vector2 currentWp = s.waypoints.get(s.waypointIndex);

        float dx = currentWp.x - p.x;
        float dy = currentWp.y - p.y;
        float distToWp = (float) Math.sqrt(dx * dx + dy * dy);

        if (distToWp < 10f) {
            s.waypointIndex = (s.waypointIndex + 1) % s.waypoints.size();
            s.currentPath.clear();
            s.pathIndex = 0;
            s.repathTimer = 0f;
            v.vx = 0; v.vy = 0;
            return;
        }

        if (s.repathTimer <= 0f || s.currentPath.isEmpty() || s.pathIndex >= s.currentPath.size()) {
            s.currentPath = Pathfinder.findPath(screen.map, p.x, p.y, currentWp.x, currentWp.y);
            s.pathIndex = 0;
            s.repathTimer = 1.0f;
        }

        if (s.currentPath.isEmpty()) {
            s.waypointIndex = (s.waypointIndex + 1) % s.waypoints.size();
            v.vx = 0; v.vy = 0;
            return;
        }

        Vector2 target = s.currentPath.get(s.pathIndex);
        float tdx = target.x - p.x;
        float tdy = target.y - p.y;
        float tdist = (float) Math.sqrt(tdx * tdx + tdy * tdy);

        if (tdist < 8f) {
            s.pathIndex++;
            if (s.pathIndex >= s.currentPath.size()) {
                v.vx = 0; v.vy = 0;
                return;
            }
            target = s.currentPath.get(s.pathIndex);
            tdx = target.x - p.x;
            tdy = target.y - p.y;
            tdist = (float) Math.sqrt(tdx * tdx + tdy * tdy);
            if (tdist < 1f) { v.vx = 0; v.vy = 0; return; }
        }

        v.vx = (tdx / tdist) * s.patrolSpeed;
        v.vy = (tdy / tdist) * s.patrolSpeed;

        checkStuck(p, v, s, delta);

        if (v.vx > 0) { t.flipX = false; t.lastFlipX = false; }
        else if (v.vx < 0) { t.flipX = true; t.lastFlipX = true; }
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

package com.alice.ai;

import com.alice.components.PositionComponent;
import com.alice.components.StateComponent;
import com.alice.components.TextureComponent;
import com.alice.components.VelocityComponent;
import com.alice.screens.GameScreen;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;

public class ReturnBehavior implements GuardBehavior {
    @Override
    public void update(float delta, Entity guard, GameScreen screen) {
        StateComponent s = guard.getComponent(StateComponent.class);
        PositionComponent p = guard.getComponent(PositionComponent.class);
        VelocityComponent v = guard.getComponent(VelocityComponent.class);
        TextureComponent t = guard.getComponent(TextureComponent.class);
        if (s.waypoints.isEmpty()) { v.vx = 0; v.vy = 0; return; }
        Vector2 wp = s.waypoints.get(0);
        float dx = wp.x - p.x;
        float dy = wp.y - p.y;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);
        if (dist < 5f) {
            s.current = StateComponent.State.PATROL;
            s.waypointIndex = 0;
            v.vx = 0; v.vy = 0;
            return;
        }
        v.vx = (dx / dist) * s.returnSpeed;
        v.vy = (dy / dist) * s.returnSpeed;
        if (v.vx > 0) { t.flipX = false; t.lastFlipX = false; }
        else if (v.vx < 0) { t.flipX = true; t.lastFlipX = true; }
    }
}

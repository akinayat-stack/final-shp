package com.alice.entities;

import com.alice.components.*;
import com.alice.utils.Constants;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class Guard {
    public static Entity create(PooledEngine engine, AssetManager assets, float x, float y) {
        Entity e = engine.createEntity();
        PositionComponent p = engine.createComponent(PositionComponent.class);
        p.x = x; p.y = y;
        VelocityComponent v = engine.createComponent(VelocityComponent.class);
        TextureComponent t = engine.createComponent(TextureComponent.class);
        Texture tex = assets.get("guard_walk.png", Texture.class);
        t.texture = tex;
        t.walkTexture = tex;
        t.idleTexture = tex;
        t.renderWidth = Constants.GUARD_SIZE;
        t.renderHeight = Constants.GUARD_SIZE;
        CollisionComponent c = engine.createComponent(CollisionComponent.class);
        float pad = (Constants.GUARD_SIZE - Constants.GUARD_COLLISION) / 2f;
        c.bounds.set(x + pad, y + pad, Constants.GUARD_COLLISION, Constants.GUARD_COLLISION);
        StateComponent s = engine.createComponent(StateComponent.class);
        s.isGuard = true;
        s.current = StateComponent.State.PATROL;
        s.detectRadius = Constants.DETECTION_RADIUS;
        s.patrolSpeed = Constants.GUARD_SPEED;
        s.chaseSpeed = Constants.GUARD_CHASE_SPEED;
        s.returnSpeed = Constants.GUARD_RETURN_SPEED;
        s.spawnPoint.set(x, y);
        s.waypoints.add(new Vector2(x, y));
        s.waypoints.add(new Vector2(x + Constants.TILE_SIZE * 2, y));
        e.add(p); e.add(v); e.add(t); e.add(c); e.add(s);
        engine.addEntity(e);
        return e;
    }
}

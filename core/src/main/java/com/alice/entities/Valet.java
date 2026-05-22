package com.alice.entities;

import com.alice.components.*;
import com.alice.utils.Constants;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class Valet {
    public static Entity create(PooledEngine engine, AssetManager assets, float x, float y) {
        Entity e = engine.createEntity();
        PositionComponent p = engine.createComponent(PositionComponent.class);
        p.x = x; p.y = y;
        VelocityComponent v = engine.createComponent(VelocityComponent.class);
        TextureComponent t = engine.createComponent(TextureComponent.class);
        // Спрайтшиты валета (2 кадра в ряд)
        t.walkFrontSheet = assets.get("valet_walk_front.png", Texture.class);
        t.walkBackSheet  = assets.get("valet_walk_back.png",  Texture.class);
        t.walkLeftSheet  = assets.get("valet_walk_left.png",  Texture.class);
        t.walkRightSheet = assets.get("valet_walk_right.png", Texture.class);
        t.texture = t.walkFrontSheet;
        t.direction = TextureComponent.Direction.DOWN;
        t.renderWidth  = Constants.VALET_SIZE;
        t.renderHeight = Constants.VALET_SIZE;
        CollisionComponent c = engine.createComponent(CollisionComponent.class);
        float pad = (Constants.VALET_SIZE - Constants.VALET_COLLISION) / 2f;
        c.bounds.set(x + pad, y + pad, Constants.VALET_COLLISION, Constants.VALET_COLLISION);
        StateComponent s = engine.createComponent(StateComponent.class);
        s.isValet = true;
        s.current = StateComponent.State.PATROL;
        s.detectRadius = Constants.VALET_DETECT_RADIUS;
        s.patrolSpeed = Constants.VALET_SPEED;
        s.chaseSpeed = Constants.VALET_CHASE_SPEED;
        s.returnSpeed = Constants.VALET_SPEED;
        s.spawnPoint.set(x, y);
        s.waypoints.add(new Vector2(x, y));
        s.waypoints.add(new Vector2(x + Constants.TILE_SIZE * 3, y));
        e.add(p); e.add(v); e.add(t); e.add(c); e.add(s);
        engine.addEntity(e);
        return e;
    }
}

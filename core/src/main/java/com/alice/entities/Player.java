package com.alice.entities;

import com.alice.components.*;
import com.alice.utils.Constants;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

public class Player {
    public static Entity create(PooledEngine engine, AssetManager assets, float x, float y) {
        Entity e = engine.createEntity();

        PositionComponent p = engine.createComponent(PositionComponent.class);
        p.x = x;
        p.y = y;

        VelocityComponent v = engine.createComponent(VelocityComponent.class);

        TextureComponent t = engine.createComponent(TextureComponent.class);
        // Загружаем 4 спрайтшита направлений (4 кадра в ряд на каждом)
        t.walkFrontSheet = assets.get("alice_walk_front.png", Texture.class); // прямо (к камере)
        t.walkBackSheet  = assets.get("alice_walk_back.png",  Texture.class); // назад (от камеры)
        t.walkLeftSheet  = assets.get("alice_walk_left.png",  Texture.class); // влево
        t.walkRightSheet = assets.get("alice_walk_right.png", Texture.class); // вправо
        // Начальная текстура — первый кадр анимации «прямо»
        t.texture = t.walkFrontSheet;
        t.direction = TextureComponent.Direction.DOWN;
        t.renderWidth  = Constants.PLAYER_SIZE;
        t.renderHeight = Constants.PLAYER_SIZE;

        CollisionComponent c = engine.createComponent(CollisionComponent.class);
        float pad = (Constants.PLAYER_SIZE - Constants.PLAYER_COLLISION) / 2f;
        c.bounds.set(x + pad, y + pad, Constants.PLAYER_COLLISION, Constants.PLAYER_COLLISION);

        StateComponent s = engine.createComponent(StateComponent.class);
        s.isPlayer = true;
        s.current = StateComponent.State.IDLE;

        e.add(p);
        e.add(v);
        e.add(t);
        e.add(c);
        e.add(s);
        engine.addEntity(e);
        return e;
    }
}

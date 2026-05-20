package com.alice.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool;

public class CollisionComponent implements Component, Pool.Poolable {
    public Rectangle bounds = new Rectangle(0, 0, 40, 40);

    @Override
    public void reset() {
        bounds.set(0, 0, 40, 40);
    }
}

package com.alice.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class VelocityComponent implements Component, Pool.Poolable {
    public float vx, vy;

    @Override
    public void reset() {
        vx = 0;
        vy = 0;
    }
}

package com.alice.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class PositionComponent implements Component, Pool.Poolable {
    public float x, y;

    @Override
    public void reset() {
        x = 0;
        y = 0;
    }
}

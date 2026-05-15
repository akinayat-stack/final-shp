package com.alice.ai;

import com.alice.screens.GameScreen;
import com.badlogic.ashley.core.Entity;

public interface GuardBehavior {
    void update(float delta, Entity guard, GameScreen screen);
}

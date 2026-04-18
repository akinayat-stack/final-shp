package com.finalshp.memory;

import com.badlogic.gdx.Game;

public class MemoryGame extends Game {
    @Override
    public void create() {
        setScreen(new GameScreen(this));
    }
}

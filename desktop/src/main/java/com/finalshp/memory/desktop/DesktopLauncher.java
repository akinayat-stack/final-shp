package com.finalshp.memory.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.finalshp.memory.MemoryGame;

public class DesktopLauncher {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Murmur Deck");
        config.setWindowedMode(1280, 720);
        config.setForegroundFPS(60);
        config.setResizable(false);
        new Lwjgl3Application(new MemoryGame(), config);
    }
}

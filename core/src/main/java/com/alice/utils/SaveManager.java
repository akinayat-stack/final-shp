package com.alice.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class SaveManager {
    private static final String PREF_NAME = "alice_save";
    private final Preferences prefs;

    public SaveManager() {
        prefs = Gdx.app.getPreferences(PREF_NAME);
    }

    public void save(int level, int keys, int lives) {
        prefs.putInteger("level", level);
        prefs.putInteger("keys", keys);
        prefs.putInteger("lives", lives);
        prefs.flush();
    }

    public int getSavedLevel() { return prefs.getInteger("level", 1); }
    public int getSavedKeys() { return prefs.getInteger("keys", 0); }
    public int getSavedLives() { return prefs.getInteger("lives", 3); }
    public boolean hasSave() { return prefs.contains("level"); }

    public void reset() {
        prefs.clear();
        prefs.flush();
    }
}

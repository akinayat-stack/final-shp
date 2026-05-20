package com.alice;

import com.alice.screens.MenuScreen;
import com.alice.utils.SaveManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class AliceGame extends Game {
    public SpriteBatch batch;
    public BitmapFont font;
    public AssetManager assets;
    public SaveManager saveManager;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(1.2f);
        assets = new AssetManager();
        saveManager = new SaveManager();
        loadAssets();
        setScreen(new MenuScreen(this));
    }

    private void loadAssets() {
        assets.load("alice_idle.png", Texture.class);
        assets.load("alice_walk.png", Texture.class);
        assets.load("guard_walk.png", Texture.class);
        assets.load("valet_walk.png", Texture.class);
        assets.load("bg_menu.png", Texture.class);
        assets.load("heart.png", Texture.class);
        assets.load("key.png", Texture.class);
        assets.load("tile_bush.png", Texture.class);
        assets.load("game_over.png", Texture.class);
        assets.load("victory.png", Texture.class);
        assets.finishLoading();
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        assets.dispose();
    }
}

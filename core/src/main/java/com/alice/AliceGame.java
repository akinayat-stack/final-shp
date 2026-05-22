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
        // Алиса — 4 направления, 4 кадра в ряд
        assets.load("alice_walk_front.png", Texture.class);
        assets.load("alice_walk_back.png",  Texture.class);
        assets.load("alice_walk_left.png",  Texture.class);
        assets.load("alice_walk_right.png", Texture.class);

        // Валет — 4 направления, 2 кадра в ряд
        assets.load("valet_walk_front.png", Texture.class);
        assets.load("valet_walk_back.png",  Texture.class);
        assets.load("valet_walk_left.png",  Texture.class);
        assets.load("valet_walk_right.png", Texture.class);

        // UI / прочее
        assets.load("bg_menu.png",   Texture.class);
        assets.load("heart.png",     Texture.class);
        assets.load("key.png",       Texture.class);
        assets.load("bush_tile.png", Texture.class);
        assets.load("floor.png",     Texture.class);
        assets.load("wall.png",      Texture.class);
        assets.load("portal.png",    Texture.class);
        assets.load("game_over.png", Texture.class);
        assets.load("try_again.png", Texture.class);
        assets.load("main_menu.png", Texture.class);
        assets.load("new_game.png",  Texture.class);
        assets.load("exit.png",      Texture.class);
        assets.load("level1.png",    Texture.class);
        assets.load("play.png",      Texture.class);
        assets.load("victory.png",   Texture.class);
        assets.finishLoading();
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        assets.dispose();
    }
}

package com.alice.screens;

import com.alice.AliceGame;
import com.alice.utils.Constants;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class WinScreen implements Screen {
    private final AliceGame game;
    private final OrthographicCamera camera;
    private final Texture background;
    private final Texture playAgainTex;
    private final Texture mainMenuTex;
    private final Rectangle playAgainBounds;
    private final Rectangle mainMenuBounds;
    private final Vector3 mouseWorld;

    private static final float BUTTON_WIDTH = 200f;
    private static final float BUTTON_GAP = 40f;
    private static final float BUTTON_Y = 80f;

    public WinScreen(AliceGame game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Constants.VIEWPORT_W, Constants.VIEWPORT_H);
        background = game.assets.get("victory.png", Texture.class);
        playAgainTex = game.assets.get("play_again.png", Texture.class);
        mainMenuTex = game.assets.get("main_menu.png", Texture.class);

        float playAgainH = BUTTON_WIDTH * (playAgainTex.getHeight() / (float) playAgainTex.getWidth());
        float mainMenuH = BUTTON_WIDTH * (mainMenuTex.getHeight() / (float) mainMenuTex.getWidth());
        float totalWidth = BUTTON_WIDTH + BUTTON_GAP + BUTTON_WIDTH;
        float startX = (Constants.VIEWPORT_W - totalWidth) / 2f;

        playAgainBounds = new Rectangle(startX, BUTTON_Y, BUTTON_WIDTH, playAgainH);
        mainMenuBounds = new Rectangle(startX + BUTTON_WIDTH + BUTTON_GAP, BUTTON_Y, BUTTON_WIDTH, mainMenuH);
        mouseWorld = new Vector3();
    }

    @Override public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        camera.update();

        game.batch.setProjectionMatrix(camera.combined);
        mouseWorld.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mouseWorld);

        game.batch.begin();
        game.batch.draw(background, 0, 0, Constants.VIEWPORT_W, Constants.VIEWPORT_H);
        boolean hoverPlayAgain = playAgainBounds.contains(mouseWorld.x, mouseWorld.y);
        boolean hoverMainMenu = mainMenuBounds.contains(mouseWorld.x, mouseWorld.y);
        drawButton(playAgainTex, playAgainBounds, hoverPlayAgain);
        drawButton(mainMenuTex, mainMenuBounds, hoverMainMenu);
        game.batch.end();

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (hoverPlayAgain) {
                game.saveManager.reset();
                game.setScreen(new GameScreen(game, 1, 0, Constants.PLAYER_LIVES));
                dispose();
                return;
            } else if (hoverMainMenu) {
                game.saveManager.reset();
                game.setScreen(new MenuScreen(game));
                dispose();
                return;
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.saveManager.reset();
            game.setScreen(new MenuScreen(game));
            dispose();
        }
    }

    private void drawButton(Texture texture, Rectangle bounds, boolean hover) {
        if (hover) {
            game.batch.draw(texture,
                bounds.x - 5f, bounds.y - 5f,
                bounds.width + 10f, bounds.height + 10f);
        } else {
            game.batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}

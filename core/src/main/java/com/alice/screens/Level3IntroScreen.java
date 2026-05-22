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

public class Level3IntroScreen implements Screen {
    private final AliceGame game;
    private final OrthographicCamera camera;
    private final Texture background;
    private final Texture playButtonTex;
    private final Rectangle playButtonBounds;
    private final Vector3 mouseWorld;

    private static final float BUTTON_WIDTH = 200f;
    private static final float BUTTON_PADDING_BOTTOM = 40f;

    public Level3IntroScreen(AliceGame game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Constants.VIEWPORT_W, Constants.VIEWPORT_H);
        this.background = game.assets.get("level3.png", Texture.class);
        this.playButtonTex = game.assets.get("play3.png", Texture.class);

        float buttonHeight = BUTTON_WIDTH * (playButtonTex.getHeight() / (float) playButtonTex.getWidth());
        float buttonX = (Constants.VIEWPORT_W - BUTTON_WIDTH) / 2f;
        float buttonY = BUTTON_PADDING_BOTTOM;
        this.playButtonBounds = new Rectangle(buttonX, buttonY, BUTTON_WIDTH, buttonHeight);
        this.mouseWorld = new Vector3();
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.batch.draw(background, 0f, 0f, Constants.VIEWPORT_W, Constants.VIEWPORT_H);
        game.batch.end();

        mouseWorld.set(Gdx.input.getX(), Gdx.input.getY(), 0f);
        camera.unproject(mouseWorld);
        boolean hover = playButtonBounds.contains(mouseWorld.x, mouseWorld.y);

        game.batch.begin();
        if (hover) {
            game.batch.draw(
                playButtonTex,
                playButtonBounds.x - 5f,
                playButtonBounds.y - 5f,
                playButtonBounds.width + 10f,
                playButtonBounds.height + 10f
            );
        } else {
            game.batch.draw(
                playButtonTex,
                playButtonBounds.x,
                playButtonBounds.y,
                playButtonBounds.width,
                playButtonBounds.height
            );
        }
        game.batch.end();

        if ((Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && hover)
            || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
            || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            startGame();
        }
    }

    private void startGame() {
        game.setScreen(new GameScreen(game, 3, 0, Constants.PLAYER_LIVES));
        dispose();
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {}
}
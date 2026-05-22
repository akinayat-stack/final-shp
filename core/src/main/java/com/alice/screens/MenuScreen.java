package com.alice.screens;

import com.alice.AliceGame;
import com.alice.utils.Constants;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class MenuScreen implements Screen {
    private final AliceGame game;
    private final OrthographicCamera camera;
    private final Texture bg;
    private final Texture btnNewTex;
    private final Texture btnExitTex;
    private final Rectangle btnContinue;
    private final Rectangle btnNew;
    private final Rectangle btnExit;
    private final Vector3 mouseWorld;
    private final GlyphLayout continueLayout;

    private static final float BUTTON_WIDTH = 180f;
    private static final float BUTTON_GAP = 40f;
    private static final float BUTTON_Y = 80f;

    public MenuScreen(AliceGame game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Constants.VIEWPORT_W, Constants.VIEWPORT_H);
        this.bg = game.assets.get("bg_menu.png", Texture.class);
        this.btnNewTex = game.assets.get("new_game.png", Texture.class);
        this.btnExitTex = game.assets.get("exit.png", Texture.class);

        this.continueLayout = new GlyphLayout();
        this.continueLayout.setText(game.font, "CONTINUE");

        float newH = BUTTON_WIDTH * (btnNewTex.getHeight() / (float) btnNewTex.getWidth());
        float exitH = BUTTON_WIDTH * (btnExitTex.getHeight() / (float) btnExitTex.getWidth());
        float continueWidth = continueLayout.width + 36f;
        float continueHeight = continueLayout.height + 20f;

        float totalWidth = BUTTON_WIDTH + BUTTON_GAP + BUTTON_WIDTH;
        float startX = (Constants.VIEWPORT_W - totalWidth) / 2f;

        this.btnContinue = new Rectangle((Constants.VIEWPORT_W - continueWidth) / 2f, 195f, continueWidth, continueHeight);
        this.btnNew = new Rectangle(startX, BUTTON_Y, BUTTON_WIDTH, newH);
        this.btnExit = new Rectangle(startX + BUTTON_WIDTH + BUTTON_GAP, BUTTON_Y, BUTTON_WIDTH, exitH);
        this.mouseWorld = new Vector3();
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.batch.draw(bg, 0f, 0f, Constants.VIEWPORT_W, Constants.VIEWPORT_H);
        game.batch.end();

        mouseWorld.set(Gdx.input.getX(), Gdx.input.getY(), 0f);
        camera.unproject(mouseWorld);

        boolean hasSave = game.saveManager.hasSave();
        boolean hoverContinue = hasSave && btnContinue.contains(mouseWorld.x, mouseWorld.y);
        boolean hoverNew = btnNew.contains(mouseWorld.x, mouseWorld.y);
        boolean hoverExit = btnExit.contains(mouseWorld.x, mouseWorld.y);

        game.batch.begin();
        game.font.getData().setScale(1.2f);
        game.font.setColor(0.15f, 0.1f, 0.05f, 1f);
        game.font.draw(game.batch, "CONTINUE", btnContinue.x + 16f, btnContinue.y + btnContinue.height - 8f);
        game.font.setColor(hoverContinue ? 1f : 0.65f, hoverContinue ? 0.9f : 0.65f, 0.2f, hasSave ? 1f : 0.55f);
        game.font.draw(game.batch, "CONTINUE", btnContinue.x + 14f, btnContinue.y + btnContinue.height - 10f);
        game.font.setColor(1f, 1f, 1f, 1f);

        if (hoverNew) {
            game.batch.draw(btnNewTex, btnNew.x - 5f, btnNew.y - 5f, btnNew.width + 10f, btnNew.height + 10f);
        } else {
            game.batch.draw(btnNewTex, btnNew.x, btnNew.y, btnNew.width, btnNew.height);
        }
        if (hoverExit) {
            game.batch.draw(btnExitTex, btnExit.x - 5f, btnExit.y - 5f, btnExit.width + 10f, btnExit.height + 10f);
        } else {
            game.batch.draw(btnExitTex, btnExit.x, btnExit.y, btnExit.width, btnExit.height);
        }
        game.batch.end();

        game.font.getData().setScale(1.2f);
        game.font.setColor(1f, 1f, 1f, 1f);

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (hoverContinue) {
                continueGame();
                dispose();
            } else if (btnNew.contains(mouseWorld.x, mouseWorld.y)) {
                game.saveManager.reset();
                game.setScreen(new Level1IntroScreen(game));
                dispose();
            } else if (btnExit.contains(mouseWorld.x, mouseWorld.y)) {
                Gdx.app.exit();
            }
        }
    }

    private void continueGame() {
        int savedLevel = game.saveManager.getSavedLevel();
        int savedKeys = game.saveManager.getSavedKeys();
        int savedLives = game.saveManager.getSavedLives();

        if (savedLevel == 1) {
            game.setScreen(new Level1IntroScreen(game));
        } else if (savedLevel == 2) {
            game.setScreen(new Level2IntroScreen(game));
        } else if (savedLevel == 3) {
            game.setScreen(new Level3IntroScreen(game));
        } else {
            game.setScreen(new GameScreen(game, savedLevel, savedKeys, savedLives));
        }
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {}
}
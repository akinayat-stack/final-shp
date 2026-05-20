package com.alice.screens;

import com.alice.AliceGame;
import com.alice.utils.Constants;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class MenuScreen implements Screen {
    private final AliceGame game;
    private final OrthographicCamera camera;
    private final ShapeRenderer shapes;
    private final Texture bg;
    private final Rectangle btnNew, btnContinue, btnExit;
    private final Vector3 mouseWorld;

    public MenuScreen(AliceGame game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Constants.VIEWPORT_W, Constants.VIEWPORT_H);
        shapes = new ShapeRenderer();
        bg = game.assets.get("bg_menu.png", Texture.class);
        btnNew = new Rectangle(300, 240, 200, 50);
        btnContinue = new Rectangle(300, 170, 200, 50);
        btnExit = new Rectangle(300, 100, 200, 50);
        mouseWorld = new Vector3();
    }

    @Override public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(bg, 0, 0, Constants.VIEWPORT_W, Constants.VIEWPORT_H);
        game.font.getData().setScale(2.0f);
        game.font.setColor(Color.GOLD);
        game.font.draw(game.batch, "ALICE: ESCAPE FROM WONDERLAND", 70, 420);
        game.font.getData().setScale(1.2f);
        game.batch.end();

        mouseWorld.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mouseWorld);

        shapes.setProjectionMatrix(camera.combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        drawButton(btnNew, btnNew.contains(mouseWorld.x, mouseWorld.y));
        drawButton(btnContinue, btnContinue.contains(mouseWorld.x, mouseWorld.y) && game.saveManager.hasSave());
        drawButton(btnExit, btnExit.contains(mouseWorld.x, mouseWorld.y));
        shapes.end();

        shapes.begin(ShapeRenderer.ShapeType.Line);
        shapes.setColor(Color.WHITE);
        shapes.rect(btnNew.x, btnNew.y, btnNew.width, btnNew.height);
        shapes.rect(btnContinue.x, btnContinue.y, btnContinue.width, btnContinue.height);
        shapes.rect(btnExit.x, btnExit.y, btnExit.width, btnExit.height);
        shapes.end();

        game.batch.begin();
        game.font.setColor(Color.WHITE);
        game.font.draw(game.batch, "NEW GAME", btnNew.x + 50, btnNew.y + 32);
        if (game.saveManager.hasSave()) game.font.setColor(Color.WHITE);
        else game.font.setColor(Color.GRAY);
        game.font.draw(game.batch, "CONTINUE", btnContinue.x + 55, btnContinue.y + 32);
        game.font.setColor(Color.WHITE);
        game.font.draw(game.batch, "EXIT", btnExit.x + 80, btnExit.y + 32);
        game.batch.end();

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (btnNew.contains(mouseWorld.x, mouseWorld.y)) {
                game.saveManager.reset();
                game.setScreen(new GameScreen(game, 1, 0, Constants.PLAYER_LIVES));
                dispose();
            } else if (btnContinue.contains(mouseWorld.x, mouseWorld.y) && game.saveManager.hasSave()) {
                game.setScreen(new GameScreen(game,
                    game.saveManager.getSavedLevel(),
                    game.saveManager.getSavedKeys(),
                    game.saveManager.getSavedLives()));
                dispose();
            } else if (btnExit.contains(mouseWorld.x, mouseWorld.y)) {
                Gdx.app.exit();
            }
        }
    }

    private void drawButton(Rectangle r, boolean hover) {
        if (hover) shapes.setColor(0.4f, 0.2f, 0.6f, 1f);
        else shapes.setColor(0.15f, 0.1f, 0.25f, 1f);
        shapes.rect(r.x, r.y, r.width, r.height);
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { shapes.dispose(); }
}


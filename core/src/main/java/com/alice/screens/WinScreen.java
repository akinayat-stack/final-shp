package com.alice.screens;

import com.alice.AliceGame;
import com.alice.utils.Constants;
import com.alice.utils.UIButton;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

public class WinScreen implements Screen {
    private final AliceGame game;
    private final OrthographicCamera camera;
    private final ShapeRenderer shapes;
    private final UIButton btnPlay, btnMenu;
    private final Vector3 mouseWorld;
    private final Texture victoryImg;

    public WinScreen(AliceGame game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Constants.VIEWPORT_W, Constants.VIEWPORT_H);
        shapes = new ShapeRenderer();
        victoryImg = game.assets.get("victory.png", Texture.class);
        btnPlay = new UIButton(300, 110, 200, 50, "PLAY AGAIN");
        btnMenu = new UIButton(300, 45, 200, 50, "MAIN MENU");
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
        mouseWorld.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mouseWorld);

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(victoryImg, 0, 0, Constants.VIEWPORT_W, Constants.VIEWPORT_H);
        game.batch.end();

        shapes.setProjectionMatrix(camera.combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        btnPlay.drawFill(shapes, btnPlay.contains(mouseWorld.x, mouseWorld.y));
        btnMenu.drawFill(shapes, btnMenu.contains(mouseWorld.x, mouseWorld.y));
        shapes.end();

        shapes.begin(ShapeRenderer.ShapeType.Line);
        btnPlay.drawOutline(shapes);
        btnMenu.drawOutline(shapes);
        shapes.end();

        game.batch.begin();
        game.font.getData().setScale(1.0f);
        game.font.setColor(1f, 0.9f, 0.5f, 1f);
        game.font.draw(game.batch, "Alice escaped the Queen!", 290, 195);

        btnPlay.drawLabel(game.batch, game.font);
        btnMenu.drawLabel(game.batch, game.font);

        game.font.getData().setScale(0.8f);
        game.font.setColor(0.95f, 0.95f, 0.95f, 0.9f);
        game.font.draw(game.batch, "[ENTER] Main Menu", 335, 25);
        game.font.getData().setScale(1.2f);
        game.batch.end();

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (btnPlay.contains(mouseWorld.x, mouseWorld.y)) {
                game.saveManager.reset();
                game.setScreen(new GameScreen(game, 1, 0, Constants.PLAYER_LIVES));
                dispose(); return;
            } else if (btnMenu.contains(mouseWorld.x, mouseWorld.y)) {
                game.saveManager.reset();
                game.setScreen(new MenuScreen(game));
                dispose(); return;
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.saveManager.reset();
            game.setScreen(new MenuScreen(game));
            dispose();
        }
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { shapes.dispose(); }
}

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

public class MenuScreen implements Screen {
    private final AliceGame game;
    private final OrthographicCamera camera;
    private final Texture bg;
    private final Texture btnNewTex, btnExitTex;
    private final Rectangle btnNew, btnExit;
    private final Vector3 mouseWorld;

    // Размеры и расположение кнопок (горизонтально)
    private static final float BUTTON_WIDTH = 180f;   // ширина кнопки
    private static final float BUTTON_HEIGHT = 70f;   // высота (подбирается под пропорции текстуры)
    private static final float BUTTON_GAP = 40f;      // расстояние между кнопками
    private static final float BUTTON_Y = 80f;        // высота от низа экрана

    public MenuScreen(AliceGame game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Constants.VIEWPORT_W, Constants.VIEWPORT_H);
        bg = game.assets.get("bg_menu.png", Texture.class);
        btnNewTex = game.assets.get("new_game.png", Texture.class);
        btnExitTex = game.assets.get("exit.png", Texture.class);

        // Вычисляем реальную высоту кнопок, сохраняя пропорции текстур
        float newH = BUTTON_WIDTH * (btnNewTex.getHeight() / (float) btnNewTex.getWidth());
        float exitH = BUTTON_WIDTH * (btnExitTex.getHeight() / (float) btnExitTex.getWidth());

        // Горизонтальное расположение: кнопки рядом, одинаковый Y
        float totalWidth = BUTTON_WIDTH + BUTTON_GAP + BUTTON_WIDTH;
        float startX = (Constants.VIEWPORT_W - totalWidth) / 2f;

        btnNew = new Rectangle(startX, BUTTON_Y, BUTTON_WIDTH, newH);
        btnExit = new Rectangle(startX + BUTTON_WIDTH + BUTTON_GAP, BUTTON_Y, BUTTON_WIDTH, exitH);

        mouseWorld = new Vector3();
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(bg, 0, 0, Constants.VIEWPORT_W, Constants.VIEWPORT_H);
        game.batch.end();

        mouseWorld.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mouseWorld);

        boolean hoverNew = btnNew.contains(mouseWorld.x, mouseWorld.y);
        boolean hoverExit = btnExit.contains(mouseWorld.x, mouseWorld.y);

        // Рисуем кнопки (можно добавить эффект наведения, например, масштаб)
        game.batch.begin();
        if (hoverNew) {
            game.batch.draw(btnNewTex, btnNew.x - 5, btnNew.y - 5, btnNew.width + 10, btnNew.height + 10);
        } else {
            game.batch.draw(btnNewTex, btnNew.x, btnNew.y, btnNew.width, btnNew.height);
        }
        if (hoverExit) {
            game.batch.draw(btnExitTex, btnExit.x - 5, btnExit.y - 5, btnExit.width + 10, btnExit.height + 10);
        } else {
            game.batch.draw(btnExitTex, btnExit.x, btnExit.y, btnExit.width, btnExit.height);
        }
        game.batch.end();

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (btnNew.contains(mouseWorld.x, mouseWorld.y)) {
                game.saveManager.reset();
                game.setScreen(new Level1IntroScreen(game));
                dispose();
            } else if (btnExit.contains(mouseWorld.x, mouseWorld.y)) {
                Gdx.app.exit();
            }
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
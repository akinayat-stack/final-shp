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

public class GameOverScreen implements Screen {
    private final AliceGame game;
    private final int level;
    private final OrthographicCamera camera;
    private final Texture bg;
    private final Texture btnRetryTex, btnMenuTex;
    private final Rectangle btnRetry, btnMenu;
    private final Vector3 mouseWorld;

    // Размеры и расположение кнопок (горизонтально)
    private static final float BUTTON_WIDTH = 200f;
    private static final float BUTTON_GAP = 40f;
    private static final float BUTTON_Y = 120f;   // высота от низа экрана (чтобы не перекрывать лежащую Алису)

    public GameOverScreen(AliceGame game, int level) {
        this.game = game;
        this.level = level;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Constants.VIEWPORT_W, Constants.VIEWPORT_H);
        bg = game.assets.get("game_over.png", Texture.class);
        btnRetryTex = game.assets.get("try_again.png", Texture.class);
        btnMenuTex = game.assets.get("main_menu.png", Texture.class);

        // Высота кнопок с сохранением пропорций
        float retryH = BUTTON_WIDTH * (btnRetryTex.getHeight() / (float) btnRetryTex.getWidth());
        float menuH = BUTTON_WIDTH * (btnMenuTex.getHeight() / (float) btnMenuTex.getWidth());

        // Горизонтальное расположение: кнопки рядом
        float totalWidth = BUTTON_WIDTH + BUTTON_GAP + BUTTON_WIDTH;
        float startX = (Constants.VIEWPORT_W - totalWidth) / 2f;

        btnRetry = new Rectangle(startX, BUTTON_Y, BUTTON_WIDTH, retryH);
        btnMenu = new Rectangle(startX + BUTTON_WIDTH + BUTTON_GAP, BUTTON_Y, BUTTON_WIDTH, menuH);

        mouseWorld = new Vector3();
    }

    @Override
    public void show() {}

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
        game.batch.draw(bg, 0, 0, Constants.VIEWPORT_W, Constants.VIEWPORT_H);
        game.batch.end();

        boolean hoverRetry = btnRetry.contains(mouseWorld.x, mouseWorld.y);
        boolean hoverMenu = btnMenu.contains(mouseWorld.x, mouseWorld.y);

        // Отрисовка кнопок с лёгким увеличением при наведении
        game.batch.begin();
        if (hoverRetry) {
            game.batch.draw(btnRetryTex, btnRetry.x - 5, btnRetry.y - 5, btnRetry.width + 10, btnRetry.height + 10);
        } else {
            game.batch.draw(btnRetryTex, btnRetry.x, btnRetry.y, btnRetry.width, btnRetry.height);
        }
        if (hoverMenu) {
            game.batch.draw(btnMenuTex, btnMenu.x - 5, btnMenu.y - 5, btnMenu.width + 10, btnMenu.height + 10);
        } else {
            game.batch.draw(btnMenuTex, btnMenu.x, btnMenu.y, btnMenu.width, btnMenu.height);
        }
        game.batch.end();

        // Подсказка по клавишам (опционально)
        game.batch.begin();
        game.font.getData().setScale(0.7f);
        game.font.setColor(1, 1, 1, 0.7f);
        game.font.draw(game.batch, "[R] Retry    [M] Menu", 330, 25);
        game.font.getData().setScale(1.2f);
        game.batch.end();

        // Обработка кликов
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (btnRetry.contains(mouseWorld.x, mouseWorld.y)) {
                game.setScreen(new GameScreen(game, level, 0, Constants.PLAYER_LIVES));
                dispose();
                return;
            } else if (btnMenu.contains(mouseWorld.x, mouseWorld.y)) {
                game.setScreen(new MenuScreen(game));
                dispose();
                return;
            }
        }

        // Клавиши
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            game.setScreen(new GameScreen(game, level, 0, Constants.PLAYER_LIVES));
            dispose();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            game.setScreen(new MenuScreen(game));
            dispose();
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
package com.alice.screens;

import java.util.ArrayList;
import java.util.List;

import com.alice.AliceGame;
import com.alice.components.PositionComponent;
import com.alice.components.StateComponent;
import com.alice.entities.Player;
import com.alice.entities.Valet;
import com.alice.map.MapData;
import com.alice.map.MapRenderer;
import com.alice.systems.AISystem;
import com.alice.systems.CameraSystem;
import com.alice.systems.CollisionSystem;
import com.alice.systems.RenderSystem;
import com.alice.utils.Constants;
import com.alice.utils.ItemType;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class GameScreen implements Screen {
    public final AliceGame game;
    public final PooledEngine engine;
    public final OrthographicCamera worldCam;
    public final OrthographicCamera hudCam;
    public final MapRenderer mapRenderer;

    public int level;
    public ItemType currentItemType;
    public Texture itemTexture;
    public Texture potionTex;
    public int itemsCollected;
    public int lives;
    public int[][] map;
    public Vector2 exitPos = new Vector2();
    public List<Vector2> itemPositions = new ArrayList<>();
    public List<Boolean> itemCollected = new ArrayList<>();
    public List<Vector2> potionPositions = new ArrayList<>();
    public List<Boolean> potionCollected = new ArrayList<>();
    // guards убраны — на всех уровнях только valet
    public List<Entity> guards = new ArrayList<>();  // оставляем пустым для совместимости с RenderSystem
    public Entity valet;
    public Entity player;
    public Player playerLogic;
    public float itemBobTime = 0f;
    public boolean paused = false;

    public GameScreen(AliceGame game, int level, int keys, int lives) {
        this.game = game;
        this.level = level;
        this.itemsCollected = keys;
        this.lives = lives;
        engine = new PooledEngine();
        worldCam = new OrthographicCamera();
        worldCam.setToOrtho(false, Constants.VIEWPORT_W, Constants.VIEWPORT_H);
        hudCam = new OrthographicCamera();
        hudCam.setToOrtho(false, Constants.VIEWPORT_W, Constants.VIEWPORT_H);
        map = MapData.getLevel(level);
        switch (level) {
            case 1: currentItemType = ItemType.KEY; break;
            case 2: currentItemType = ItemType.ROSE; break;
            case 3: currentItemType = ItemType.CARD; break;
            default: currentItemType = ItemType.KEY;
        }
        itemTexture = game.assets.get(currentItemType.texturePath, Texture.class);
        potionTex = game.assets.get("potion.png", Texture.class);
        mapRenderer = new MapRenderer(game.assets, map, level);
        loadEntities();
        engine.addSystem(new CollisionSystem(map, this));
        engine.addSystem(new AISystem(this));
        engine.addSystem(new CameraSystem(this));
        engine.addSystem(new RenderSystem(this));
    }

    private void loadEntities() {
        int rows = map.length;
        int cols = map[0].length;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                float wx = c * Constants.TILE_SIZE;
                float wy = (rows - 1 - r) * Constants.TILE_SIZE;
                int t = map[r][c];
                switch (t) {
                    case 2:
                        itemPositions.add(new Vector2(wx, wy));
                        itemCollected.add(false);
                        break;
                    case Constants.ELIXIR_TILE:
                        potionPositions.add(new Vector2(wx, wy));
                        potionCollected.add(false);
                        break;
                    case 3:
                        exitPos.set(wx, wy);
                        break;
                    case 6:
                        // Создаём нескольких валетов; если valet уже создан — создаём ещё один и добавляем в guards
                        // для совместимости с RenderSystem (индикаторы погони)
                        Entity v = Valet.create(engine, game.assets, wx, wy);
                        if (valet == null) {
                            valet = v;
                        } else {
                            guards.add(v);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        Vector2 spawn = findPlayerSpawn();
        player = Player.create(engine, game.assets, spawn.x, spawn.y);
        playerLogic = new Player();
    }

    private Vector2 findPlayerSpawn() {
        int rows = map.length;
        int cols = map[0].length;
        for (int r = rows - 1; r >= 0; r--) {
            for (int c = 0; c < cols; c++) {
                if (map[r][c] == 0) {
                    return new Vector2(c * Constants.TILE_SIZE,
                        (rows - 1 - r) * Constants.TILE_SIZE);
                }
            }
        }
        return new Vector2(64, 64);
    }

    @Override public void show() {}

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) paused = !paused;

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!paused) {
            itemBobTime += delta;
            checkItemPickup();
            checkPotionPickup();
            updatePotionTimer(delta);
            engine.update(delta);
            checkExit();
            checkGameOver();
        } else {
            engine.getSystem(RenderSystem.class).update(0);
            drawPauseOverlay();
        }
    }

    private void checkItemPickup() {
        PositionComponent pp = player.getComponent(PositionComponent.class);
        for (int i = 0; i < itemPositions.size(); i++) {
            if (itemCollected.get(i)) continue;
            Vector2 ip = itemPositions.get(i);
            float dx = (pp.x + 32) - (ip.x + 32);
            float dy = (pp.y + 32) - (ip.y + 32);
            if (dx * dx + dy * dy < 40 * 40) {
                itemCollected.set(i, true);
                itemsCollected++;
            }
        }
    }

    private void checkPotionPickup() {
        PositionComponent pp = player.getComponent(PositionComponent.class);
        for (int i = 0; i < potionPositions.size(); i++) {
            if (potionCollected.get(i)) continue;
            Vector2 pos = potionPositions.get(i);
            float dx = (pp.x + 32) - (pos.x + 32);
            float dy = (pp.y + 32) - (pos.y + 32);
            if (dx * dx + dy * dy < 40 * 40) {
                potionCollected.set(i, true);
                StateComponent state = player.getComponent(StateComponent.class);
                state.invisible = true;
                state.invisibleTimer = Constants.INVISIBILITY_DURATION;
            }
        }
    }

    private void updatePotionTimer(float delta) {
        StateComponent state = player.getComponent(StateComponent.class);
        if (!state.invisible) return;
        state.invisibleTimer -= delta;
        if (state.invisibleTimer <= 0f) {
            state.invisible = false;
            state.invisibleTimer = 0f;
        }
    }

    private void checkExit() {
        if (itemsCollected < Constants.TOTAL_ITEMS) return;
        PositionComponent pp = player.getComponent(PositionComponent.class);
        Rectangle pr = new Rectangle(pp.x + 12, pp.y + 12, 40, 40);
        Rectangle er = new Rectangle(exitPos.x, exitPos.y, 64, 64);
        if (pr.overlaps(er)) {
            if (level < 3) {
                game.saveManager.save(level + 1, 0, lives);
                switch (level) {
                    case 1:
                        game.setScreen(new Level2IntroScreen(game));
                        break;
                    case 2:
                        game.setScreen(new Level3IntroScreen(game));
                        break;
                    default:
                        game.setScreen(new GameScreen(game, level + 1, 0, lives));
                        break;
                }
                dispose();
            } else {
                game.saveManager.reset();
                game.setScreen(new WinScreen(game));
                dispose();
            }
        }
    }

    private void checkGameOver() {
        if (lives <= 0) {
            game.setScreen(new GameOverScreen(game, level));
            dispose();
        }
    }

    private void drawPauseOverlay() {
        game.batch.setProjectionMatrix(hudCam.combined);
        game.batch.begin();
        game.font.getData().setScale(2.5f);
        game.font.setColor(Color.WHITE);
        game.font.draw(game.batch, "PAUSED", 320, 320);
        game.font.getData().setScale(1.5f);
        game.font.draw(game.batch, "RESUME (ESC)", 290, 240);
        game.font.draw(game.batch, "MAIN MENU (M)", 285, 190);
        game.font.getData().setScale(1.2f);
        game.batch.end();
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            game.setScreen(new MenuScreen(game));
            dispose();
        }
    }

    public void damagePlayer() {
        lives--;
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        engine.removeAllEntities();
        engine.removeAllSystems();
        mapRenderer.dispose();
    }
}

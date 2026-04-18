package com.finalshp.memory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameScreen extends ScreenAdapter {
    private static final float WORLD_WIDTH = 1280f;
    private static final float WORLD_HEIGHT = 720f;
    private static final int GRID_SIZE = 4;

    private final MemoryGame game;
    private final OrthographicCamera camera;
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final GameAssets assets;
    private final Array<Card> cards;
    private final Rectangle restartBounds;
    private final Array<FogParticle> particles;

    private Card firstSelected;
    private Card secondSelected;
    private float mismatchTimer;
    private int moves;
    private boolean won;
    private float ambientTime;

    public GameScreen(MemoryGame game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();
        this.assets = new GameAssets();
        this.assets.load();
        this.cards = new Array<>();
        this.restartBounds = new Rectangle(38f, WORLD_HEIGHT - 102f, 96f, 64f);
        this.particles = new Array<>();
        initParticles();
        resetGame();
    }

    private void initParticles() {
        particles.clear();
        for (int i = 0; i < 32; i++) {
            particles.add(new FogParticle(
                MathUtils.random(WORLD_WIDTH),
                MathUtils.random(WORLD_HEIGHT),
                MathUtils.random(18f, 42f),
                MathUtils.random(0.01f, 0.06f),
                MathUtils.random(8f, 24f)
            ));
        }
    }

    private void resetGame() {
        cards.clear();
        firstSelected = null;
        secondSelected = null;
        mismatchTimer = 0f;
        moves = 0;
        won = false;

        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            ids.add(i);
            ids.add(i);
        }
        Collections.shuffle(ids);

        float cardW = 170f;
        float cardH = 230f;
        float gap = 18f;
        float totalWidth = GRID_SIZE * cardW + (GRID_SIZE - 1) * gap;
        float totalHeight = GRID_SIZE * cardH + (GRID_SIZE - 1) * gap;
        float startX = (WORLD_WIDTH - totalWidth) * 0.5f;
        float startY = (WORLD_HEIGHT - totalHeight) * 0.5f - 20f;

        int idx = 0;
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                float x = startX + col * (cardW + gap);
                float y = startY + (GRID_SIZE - 1 - row) * (cardH + gap);
                int id = ids.get(idx++);
                cards.add(new Card(id, x, y, cardW, cardH, assets.cardFaces[id], assets.cardBack));
            }
        }
    }

    @Override
    public void render(float delta) {
        ambientTime += delta;
        updateLogic(delta);

        Gdx.gl.glClearColor(0.05f, 0.045f, 0.055f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        drawBackground();
        drawCards();
        drawParticles();
        drawUi();
        if (won) {
            drawWinOverlay();
        }
        batch.end();
    }

    private void updateLogic(float delta) {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            Vector3 world = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0f));
            handleClick(world.x, world.y);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            resetGame();
        }

        for (Card card : cards) {
            card.update(delta, 4.4f);
        }

        for (FogParticle p : particles) {
            p.update(delta);
        }

        if (secondSelected != null && mismatchTimer > 0f) {
            mismatchTimer -= delta;
            if (mismatchTimer <= 0f) {
                if (firstSelected.getId() != secondSelected.getId()) {
                    firstSelected.setState(Card.State.FACE_DOWN);
                    secondSelected.setState(Card.State.FACE_DOWN);
                }
                firstSelected = null;
                secondSelected = null;
                checkWin();
            }
        }
    }

    private void handleClick(float x, float y) {
        if (restartBounds.contains(x, y)) {
            resetGame();
            return;
        }

        if (won || secondSelected != null) {
            return;
        }

        for (Card card : cards) {
            if (card.contains(x, y) && card.getState() == Card.State.FACE_DOWN) {
                card.setState(Card.State.FACE_UP);

                if (firstSelected == null) {
                    firstSelected = card;
                } else {
                    secondSelected = card;
                    moves++;
                    if (firstSelected.getId() == secondSelected.getId()) {
                        firstSelected.setState(Card.State.MATCHED);
                        secondSelected.setState(Card.State.MATCHED);
                        firstSelected = null;
                        secondSelected = null;
                        checkWin();
                    } else {
                        mismatchTimer = 0.9f;
                    }
                }
                return;
            }
        }
    }

    private void checkWin() {
        for (Card card : cards) {
            if (card.getState() != Card.State.MATCHED) {
                return;
            }
        }
        won = true;
    }

    private void drawBackground() {
        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(assets.tableTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);

        float ambientPulse = 0.08f + 0.05f * MathUtils.sin(ambientTime * 0.8f);
        batch.setColor(0.56f, 0.53f, 0.69f, ambientPulse);
        batch.draw(assets.glow, 160f, 70f, 960f, 580f);
        batch.setColor(1f, 1f, 1f, 1f);
    }

    private void drawCards() {
        Vector3 mouseWorld = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

        for (Card card : cards) {
            float x = card.getBounds().x;
            float y = card.getBounds().y;
            float w = card.getBounds().width;
            float h = card.getBounds().height;

            float hoverBoost = card.contains(mouseWorld.x, mouseWorld.y) && card.getState() == Card.State.FACE_DOWN ? 8f : 0f;
            float scaleX = Math.max(0.08f, card.getVisualScaleX());
            float drawW = w * scaleX;
            float drawX = x + (w - drawW) * 0.5f;

            if (card.getState() == Card.State.MATCHED) {
                float glowAlpha = card.getGlowIntensity();
                batch.setColor(0.75f, 0.62f, 0.9f, glowAlpha);
                batch.draw(assets.glow, x - 42f, y - 36f + hoverBoost * 0.25f, w + 84f, h + 72f);
                batch.setColor(1f, 1f, 1f, 1f);
            }

            batch.draw(card.shouldDrawFace() ? card.getFaceRegion() : card.getBackRegion(), drawX, y + hoverBoost, drawW, h);
        }
    }

    private void drawParticles() {
        for (FogParticle p : particles) {
            float alpha = 0.04f + 0.05f * MathUtils.sin(ambientTime * p.pulse + p.phase);
            batch.setColor(0.87f, 0.85f, 0.8f, alpha);
            batch.draw(assets.fogParticle, p.x, p.y, p.size, p.size);
        }
        batch.setColor(Color.WHITE);
    }

    private void drawUi() {
        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(assets.restartButton, restartBounds.x, restartBounds.y, restartBounds.width, restartBounds.height);

        assets.font.draw(batch, "moves: " + moves, WORLD_WIDTH - 180f, WORLD_HEIGHT - 40f);
        assets.font.draw(batch, "r", restartBounds.x + 44f, restartBounds.y + 42f);
    }

    private void drawWinOverlay() {
        batch.end();
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.04f, 0.04f, 0.06f, 0.58f);
        shapeRenderer.rect(0f, 0f, WORLD_WIDTH, WORLD_HEIGHT);
        shapeRenderer.end();
        batch.begin();

        assets.font.getData().setScale(1.8f);
        assets.font.setColor(0.95f, 0.89f, 0.78f, 1f);
        assets.font.draw(batch, "all memories uncovered", WORLD_WIDTH * 0.5f - 210f, WORLD_HEIGHT * 0.5f + 16f);
        assets.font.getData().setScale(1.1f);
        assets.font.draw(batch, "press R or click the carved token", WORLD_WIDTH * 0.5f - 190f, WORLD_HEIGHT * 0.5f - 26f);
        assets.font.getData().setScale(1.2f);
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        assets.dispose();
    }

    private static class FogParticle {
        float x;
        float y;
        final float size;
        final float speed;
        final float pulse;
        final float phase;

        private FogParticle(float x, float y, float size, float speed, float pulse) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.speed = speed;
            this.pulse = pulse;
            this.phase = MathUtils.random(0f, MathUtils.PI2);
        }

        void update(float delta) {
            x += speed * delta * 60f;
            y += MathUtils.sin((x + phase) * 0.01f) * delta * 6f;
            if (x > WORLD_WIDTH + size) {
                x = -size;
            }
        }
    }
}

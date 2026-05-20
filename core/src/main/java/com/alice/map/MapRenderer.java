package com.alice.map;

import com.alice.utils.Constants;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class MapRenderer {
    private final Texture bush;
    private final int[][] map;
    private final int rows;
    private final int cols;
    private final ShapeRenderer shapes;

    public MapRenderer(AssetManager assets, int[][] map) {
        this.map = map;
        this.rows = map.length;
        this.cols = map[0].length;
        this.bush = assets.get("tile_bush.png", Texture.class);
        this.shapes = new ShapeRenderer();
    }

    public ShapeRenderer getShapes() {
        return shapes;
    }

    public void renderFloors(SpriteBatch batch, OrthographicCamera cam) {
        batch.end();
        shapes.setProjectionMatrix(cam.combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);

        int startCol = Math.max(0, (int) ((cam.position.x - Constants.VIEWPORT_W / 2) / Constants.TILE_SIZE) - 2);
        int endCol = Math.min(cols - 1, (int) ((cam.position.x + Constants.VIEWPORT_W / 2) / Constants.TILE_SIZE) + 2);
        int startRow = Math.max(0, (int) ((cam.position.y - Constants.VIEWPORT_H / 2) / Constants.TILE_SIZE) - 2);
        int endRow = Math.min(rows - 1, (int) ((cam.position.y + Constants.VIEWPORT_H / 2) / Constants.TILE_SIZE) + 2);

        for (int row = startRow; row <= endRow; row++) {
            for (int col = startCol; col <= endCol; col++) {
                drawFloorTile(col * Constants.TILE_SIZE, row * Constants.TILE_SIZE, col, row);
            }
        }

        for (int row = startRow; row <= endRow; row++) {
            for (int col = startCol; col <= endCol; col++) {
                int mapRow = rows - 1 - row;
                if (mapRow < 0 || mapRow >= rows) continue;
                if (map[mapRow][col] == 1) {
                    drawWallShadow(col * Constants.TILE_SIZE, row * Constants.TILE_SIZE);
                }
            }
        }

        for (int row = startRow; row <= endRow; row++) {
            for (int col = startCol; col <= endCol; col++) {
                int mapRow = rows - 1 - row;
                if (mapRow < 0 || mapRow >= rows) continue;
                if (map[mapRow][col] == 1) {
                    drawStoneWall(col * Constants.TILE_SIZE, row * Constants.TILE_SIZE, col, mapRow);
                }
            }
        }

        shapes.end();
        batch.begin();
    }

    private void drawFloorTile(float x, float y, int col, int row) {
        float s = Constants.TILE_SIZE;
        boolean dark = ((col + row) % 2 == 0);
        if (dark) shapes.setColor(0.24f, 0.20f, 0.30f, 1f);
        else      shapes.setColor(0.20f, 0.16f, 0.26f, 1f);
        shapes.rect(x - 0.5f, y - 0.5f, s + 1f, s + 1f);

        int seed = col * 31 + row * 17;
        if ((seed % 6) == 0) {
            shapes.setColor(0.30f, 0.24f, 0.36f, 1f);
            shapes.circle(x + 12 + (seed % 40), y + 12 + ((seed * 3) % 40), 1.3f);
        }
        if ((seed % 5) == 0) {
            shapes.setColor(0.15f, 0.12f, 0.20f, 1f);
            shapes.circle(x + 20 + ((seed * 7) % 24), y + 20 + ((seed * 11) % 24), 1f);
        }
    }

    private boolean isWall(int mapRow, int col) {
        if (mapRow < 0 || mapRow >= rows || col < 0 || col >= cols) return false;
        return map[mapRow][col] == 1;
    }

    private void drawWallShadow(float x, float y) {
        float s = Constants.TILE_SIZE;
        shapes.setColor(0f, 0f, 0f, 0.45f);
        shapes.rect(x + 4, y - 5, s, s);
    }

    private void drawStoneWall(float x, float y, int col, int mapRow) {
        float s = Constants.TILE_SIZE;
        int seed = (col * 53 + mapRow * 19) & 0xFFFF;

        shapes.setColor(0.10f, 0.06f, 0.16f, 1f);
        shapes.rect(x, y, s, s);

        int blockRows = 4;
        float blockH = s / blockRows;

        for (int i = 0; i < blockRows; i++) {
            float by = y + i * blockH;
            boolean offsetRow = (i % 2 == 0);

            if (offsetRow) {
                float bw1 = s * 0.4f;
                float bw2 = s * 0.6f;
                drawBlock(x, by, bw1, blockH, seed + i * 7);
                drawBlock(x + bw1, by, bw2, blockH, seed + i * 13);
            } else {
                float bw1 = s * 0.55f;
                float bw2 = s * 0.45f;
                drawBlock(x, by, bw1, blockH, seed + i * 11);
                drawBlock(x + bw1, by, bw2, blockH, seed + i * 17);
            }
        }

        shapes.setColor(0.06f, 0.03f, 0.10f, 1f);
        for (int i = 1; i < blockRows; i++) {
            float gy = y + i * blockH;
            shapes.rect(x, gy - 0.5f, s, 1f);
        }

        boolean up    = isWall(mapRow - 1, col);
        boolean down  = isWall(mapRow + 1, col);
        boolean left  = isWall(mapRow, col - 1);
        boolean right = isWall(mapRow, col + 1);

        if (!up) {
            shapes.setColor(0.62f, 0.50f, 0.78f, 1f);
            shapes.rect(x, y + s - 3, s, 3);
            shapes.setColor(0.78f, 0.66f, 0.92f, 0.7f);
            shapes.rect(x, y + s - 1.2f, s, 1.2f);
        }
        if (!down) {
            shapes.setColor(0.08f, 0.04f, 0.12f, 1f);
            shapes.rect(x, y, s, 2f);
        }
        if (!left) {
            shapes.setColor(0.14f, 0.08f, 0.20f, 1f);
            shapes.rect(x, y, 2, s);
        }
        if (!right) {
            shapes.setColor(0.55f, 0.44f, 0.70f, 1f);
            shapes.rect(x + s - 2, y, 2, s);
            shapes.setColor(0.74f, 0.62f, 0.88f, 0.5f);
            shapes.rect(x + s - 1, y, 1, s);
        }

        if (!up && !left) {
            shapes.setColor(0.78f, 0.66f, 0.92f, 1f);
            shapes.rect(x, y + s - 3, 4, 3);
        }
        if (!up && !right) {
            shapes.setColor(0.78f, 0.66f, 0.92f, 1f);
            shapes.rect(x + s - 4, y + s - 3, 4, 3);
        }
    }

    private void drawBlock(float bx, float by, float bw, float bh, int seed) {
        float r, g, b;
        int variant = Math.abs(seed) % 4;

        if (variant == 0) {
            r = 0.38f + ((seed & 7) * 0.010f);
            g = 0.30f + ((seed & 3) * 0.008f);
            b = 0.52f + ((seed & 5) * 0.010f);
        } else if (variant == 1) {
            r = 0.32f + ((seed & 5) * 0.010f);
            g = 0.24f;
            b = 0.46f;
        } else if (variant == 2) {
            r = 0.42f;
            g = 0.34f;
            b = 0.56f;
        } else {
            r = 0.36f + ((seed & 7) * 0.008f);
            g = 0.28f;
            b = 0.50f;
        }

        shapes.setColor(r, g, b, 1f);
        shapes.rect(bx + 1, by + 1, bw - 2, bh - 2);

        shapes.setColor(r * 1.25f, g * 1.25f, b * 1.18f, 1f);
        shapes.rect(bx + 1, by + bh - 3, bw - 2, 1.5f);

        shapes.setColor(r * 0.55f, g * 0.55f, b * 0.60f, 1f);
        shapes.rect(bx + 1, by + 1, bw - 2, 1.2f);

        if ((seed % 6) == 0) {
            shapes.setColor(r * 0.7f, g * 0.7f, b * 0.75f, 0.6f);
            shapes.rect(bx + 4 + (seed & 7), by + 5, 3, 1f);
        }
        if ((seed % 8) == 0) {
            shapes.setColor(r * 1.35f, g * 1.30f, b * 1.20f, 0.5f);
            shapes.circle(bx + 6 + ((seed * 3) & 7), by + bh - 6, 0.8f);
        }
        if ((seed % 11) == 0) {
            shapes.setColor(0.10f, 0.05f, 0.16f, 0.8f);
            shapes.rect(bx + 3 + ((seed * 5) & 7), by + 4 + ((seed * 7) & 3), 2, 0.8f);
        }
    }

    public void renderBushes(SpriteBatch batch, OrthographicCamera cam) {
        int startCol = Math.max(0, (int) ((cam.position.x - Constants.VIEWPORT_W / 2) / Constants.TILE_SIZE) - 1);
        int endCol = Math.min(cols - 1, (int) ((cam.position.x + Constants.VIEWPORT_W / 2) / Constants.TILE_SIZE) + 1);
        int startRow = Math.max(0, (int) ((cam.position.y - Constants.VIEWPORT_H / 2) / Constants.TILE_SIZE) - 1);
        int endRow = Math.min(rows - 1, (int) ((cam.position.y + Constants.VIEWPORT_H / 2) / Constants.TILE_SIZE) + 1);

        batch.end();
        shapes.setProjectionMatrix(cam.combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        for (int row = startRow; row <= endRow; row++) {
            for (int col = startCol; col <= endCol; col++) {
                int mapRow = rows - 1 - row;
                if (map[mapRow][col] == 4) {
                    drawBushTile(col * Constants.TILE_SIZE, row * Constants.TILE_SIZE, col, row);
                }
            }
        }
        shapes.end();
        batch.begin();
    }

    private void drawBushTile(float x, float y, int col, int row) {
        float s = Constants.TILE_SIZE;
        int seed = col * 47 + row * 29;
        shapes.setColor(0.06f, 0.12f, 0.06f, 1f);
        shapes.rect(x, y, s, s);
        shapes.setColor(0.10f, 0.22f, 0.10f, 1f);
        for (int i = 0; i < 6; i++) {
            shapes.circle(x + 8 + ((seed + i * 13) % 48), y + 6 + ((seed + i * 19) % 50), 14);
        }
        shapes.setColor(0.16f, 0.34f, 0.16f, 1f);
        for (int i = 0; i < 8; i++) {
            shapes.circle(x + 10 + ((seed + i * 11) % 44), y + 10 + ((seed + i * 17) % 44), 9);
        }
        shapes.setColor(0.22f, 0.48f, 0.22f, 1f);
        for (int i = 0; i < 6; i++) {
            shapes.circle(x + 14 + ((seed + i * 23) % 36), y + 14 + ((seed + i * 29) % 36), 5);
        }
        if ((seed % 3) == 0) {
            shapes.setColor(0.9f, 0.3f, 0.4f, 1f);
            shapes.circle(x + 18 + (seed % 28), y + 20 + ((seed * 3) % 24), 2.5f);
        }
    }

    public void dispose() {
        shapes.dispose();
    }
}

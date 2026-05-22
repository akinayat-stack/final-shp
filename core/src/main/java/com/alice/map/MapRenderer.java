package com.alice.map;

import com.alice.screens.GameScreen;
import com.alice.utils.Constants;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MapRenderer {
    private final int[][] map;
    private final int rows;
    private final int cols;
    private final Texture floorTex;
    private final Texture wallTex;
    private final Texture portalTex;

    public MapRenderer(AssetManager assets, int[][] map, int level) {
        this.map = map;
        this.rows = map.length;
        this.cols = map[0].length;

        String suffix = (level == 1) ? "" : String.valueOf(level);
        this.floorTex = assets.get("floor" + suffix + ".png", Texture.class);
        this.wallTex = assets.get("wall" + suffix + ".png", Texture.class);
        this.portalTex = assets.get("portal" + suffix + ".png", Texture.class);
    }

    public void render(SpriteBatch batch, OrthographicCamera cam, GameScreen screen) {
        int startCol = Math.max(0, (int) ((cam.position.x - Constants.VIEWPORT_W / 2f) / Constants.TILE_SIZE) - 1);
        int endCol = Math.min(cols - 1, (int) ((cam.position.x + Constants.VIEWPORT_W / 2f) / Constants.TILE_SIZE) + 1);
        int startRow = Math.max(0, (int) ((cam.position.y - Constants.VIEWPORT_H / 2f) / Constants.TILE_SIZE) - 1);
        int endRow = Math.min(rows - 1, (int) ((cam.position.y + Constants.VIEWPORT_H / 2f) / Constants.TILE_SIZE) + 1);

        for (int row = startRow; row <= endRow; row++) {
            for (int col = startCol; col <= endCol; col++) {
                int mapRow = rows - 1 - row;
                if (mapRow < 0 || mapRow >= rows || col < 0 || col >= cols) continue;

                int tile = map[mapRow][col];
                float x = col * Constants.TILE_SIZE;
                float y = row * Constants.TILE_SIZE;

                batch.draw(floorTex, x, y, Constants.TILE_SIZE, Constants.TILE_SIZE);

                if (tile == 1) {
                    batch.draw(wallTex, x, y, Constants.TILE_SIZE, Constants.TILE_SIZE);
                }
            }
        }

        if (screen.keysCollected >= Constants.TOTAL_KEYS && screen.exitPos != null) {
            float px = screen.exitPos.x;
            float py = screen.exitPos.y;
            float halfW = Constants.VIEWPORT_W / 2f;
            float halfH = Constants.VIEWPORT_H / 2f;
            if (px + Constants.TILE_SIZE > cam.position.x - halfW &&
                px < cam.position.x + halfW &&
                py + Constants.TILE_SIZE > cam.position.y - halfH &&
                py < cam.position.y + halfH) {
                batch.draw(portalTex, px, py, Constants.TILE_SIZE, Constants.TILE_SIZE);
            }
        }
    }

    public void dispose() {
    }
}
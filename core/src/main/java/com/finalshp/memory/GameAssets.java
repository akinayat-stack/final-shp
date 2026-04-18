package com.finalshp.memory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class GameAssets implements Disposable {
    private final Array<Texture> textures = new Array<>();

    public TextureRegion tableTexture;
    public TextureRegion cardBack;
    public TextureRegion restartButton;
    public TextureRegion[] cardFaces;
    public TextureRegion glow;
    public TextureRegion fogParticle;
    public BitmapFont font;

    public void load() {
        tableTexture = new TextureRegion(newTexture(createTablePixmap(1280, 720)));
        cardBack = new TextureRegion(newTexture(createCardBackPixmap(220, 300)));
        restartButton = new TextureRegion(newTexture(createRestartButtonPixmap(110, 70)));
        glow = new TextureRegion(newTexture(createGlowPixmap(256, 256)));
        fogParticle = new TextureRegion(newTexture(createFogPixmap(80, 80)));

        cardFaces = new TextureRegion[8];
        for (int i = 0; i < cardFaces.length; i++) {
            cardFaces[i] = new TextureRegion(newTexture(createFacePixmap(220, 300, i)));
        }

        font = new BitmapFont();
        font.getData().setScale(1.2f);
        font.setColor(new Color(0.93f, 0.88f, 0.77f, 1f));
    }

    private Texture newTexture(Pixmap pixmap) {
        Texture texture = new Texture(pixmap);
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        textures.add(texture);
        pixmap.dispose();
        return texture;
    }

    private Pixmap createTablePixmap(int width, int height) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        for (int y = 0; y < height; y++) {
            float t = y / (float) height;
            for (int x = 0; x < width; x++) {
                float n = ((x * 13 + y * 7) % 100) / 100f;
                float grain = 0.08f * (float) Math.sin((x + y) * 0.03f) + 0.06f * n;
                float r = 0.15f + 0.17f * t + grain;
                float g = 0.11f + 0.1f * t + grain * 0.6f;
                float b = 0.09f + 0.08f * t + grain * 0.4f;
                pixmap.setColor(clamp(r), clamp(g), clamp(b), 1f);
                pixmap.drawPixel(x, y);
            }
        }

        pixmap.setColor(new Color(0.09f, 0.06f, 0.05f, 0.5f));
        for (int i = 0; i < 22; i++) {
            int y = 18 + i * 32;
            pixmap.fillRectangle(0, y, width, 3);
        }
        return pixmap;
    }

    private Pixmap createCardBackPixmap(int width, int height) {
        Pixmap pixmap = createCardBase(width, height, new Color(0.88f, 0.79f, 0.71f, 1f));
        Color stitch = new Color(0.77f, 0.42f, 0.37f, 1f);
        pixmap.setColor(stitch);
        for (int x = 22; x < width - 20; x += 18) {
            pixmap.drawLine(x, 18, x - 18, height - 20);
            pixmap.drawLine(x, height - 20, x - 18, 18);
        }
        return pixmap;
    }

    private Pixmap createFacePixmap(int width, int height, int symbolIndex) {
        Color[] palette = new Color[] {
            new Color(0.72f, 0.26f, 0.27f, 1f),
            new Color(0.33f, 0.32f, 0.38f, 1f),
            new Color(0.40f, 0.49f, 0.37f, 1f),
            new Color(0.58f, 0.49f, 0.45f, 1f)
        };

        Pixmap pixmap = createCardBase(width, height, new Color(0.93f, 0.89f, 0.79f, 1f));
        Color symbolColor = palette[symbolIndex % palette.length];
        drawSymbol(pixmap, symbolIndex % 4, width / 2, height / 2 + 14, 60, symbolColor);
        drawSymbol(pixmap, symbolIndex % 4, 48, 52, 16, symbolColor);
        drawSymbol(pixmap, symbolIndex % 4, width - 48, height - 52, 16, symbolColor);
        return pixmap;
    }

    private Pixmap createCardBase(int width, int height, Color baseColor) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0f, 0f, 0f, 0f));
        pixmap.fill();

        for (int y = 0; y < height; y++) {
            float tint = ((y * 37) % 50) / 400f;
            pixmap.setColor(baseColor.r - tint, baseColor.g - tint, baseColor.b - tint, 1f);
            pixmap.drawLine(0, y, width - 1, y);
        }

        pixmap.setColor(new Color(0.07f, 0.06f, 0.06f, 1f));
        drawRoughRect(pixmap, 2, 2, width - 4, height - 4, 4);
        pixmap.setColor(new Color(0.18f, 0.14f, 0.11f, 1f));
        drawRoughRect(pixmap, 8, 8, width - 16, height - 16, 3);
        return pixmap;
    }

    private void drawSymbol(Pixmap pixmap, int suit, int cx, int cy, int size, Color color) {
        pixmap.setColor(color);
        switch (suit) {
            case 0 -> drawHeart(pixmap, cx, cy, size);
            case 1 -> drawDiamond(pixmap, cx, cy, size);
            case 2 -> drawSpade(pixmap, cx, cy, size);
            default -> drawClub(pixmap, cx, cy, size);
        }
        pixmap.setColor(0.06f, 0.05f, 0.05f, 1f);
        switch (suit) {
            case 0 -> drawHeartOutline(pixmap, cx, cy, size);
            case 1 -> drawDiamondOutline(pixmap, cx, cy, size);
            case 2 -> drawSpadeOutline(pixmap, cx, cy, size);
            default -> drawClubOutline(pixmap, cx, cy, size);
        }
    }

    private void drawHeart(Pixmap p, int cx, int cy, int s) {
        p.fillCircle(cx - s / 4, cy + s / 8, s / 4);
        p.fillCircle(cx + s / 4, cy + s / 8, s / 4);
        p.fillTriangle(cx - s / 2, cy + s / 8, cx + s / 2, cy + s / 8, cx, cy - s / 2);
    }

    private void drawDiamond(Pixmap p, int cx, int cy, int s) {
        p.fillTriangle(cx, cy + s / 2, cx + s / 2, cy, cx, cy - s / 2);
        p.fillTriangle(cx, cy + s / 2, cx - s / 2, cy, cx, cy - s / 2);
    }

    private void drawSpade(Pixmap p, int cx, int cy, int s) {
        p.fillCircle(cx - s / 4, cy, s / 4);
        p.fillCircle(cx + s / 4, cy, s / 4);
        p.fillTriangle(cx - s / 2, cy, cx + s / 2, cy, cx, cy + s / 2);
        p.fillRectangle(cx - s / 10, cy - s / 2, s / 5, s / 2);
    }

    private void drawClub(Pixmap p, int cx, int cy, int s) {
        p.fillCircle(cx - s / 4, cy, s / 4);
        p.fillCircle(cx + s / 4, cy, s / 4);
        p.fillCircle(cx, cy + s / 4, s / 4);
        p.fillRectangle(cx - s / 10, cy - s / 2, s / 5, s / 2);
    }

    private void drawHeartOutline(Pixmap p, int cx, int cy, int s) {
        p.drawCircle(cx - s / 4, cy + s / 8, s / 4);
        p.drawCircle(cx + s / 4, cy + s / 8, s / 4);
        p.drawLine(cx - s / 2, cy + s / 8, cx, cy - s / 2);
        p.drawLine(cx + s / 2, cy + s / 8, cx, cy - s / 2);
    }

    private void drawDiamondOutline(Pixmap p, int cx, int cy, int s) {
        p.drawLine(cx, cy + s / 2, cx + s / 2, cy);
        p.drawLine(cx + s / 2, cy, cx, cy - s / 2);
        p.drawLine(cx, cy - s / 2, cx - s / 2, cy);
        p.drawLine(cx - s / 2, cy, cx, cy + s / 2);
    }

    private void drawSpadeOutline(Pixmap p, int cx, int cy, int s) {
        p.drawCircle(cx - s / 4, cy, s / 4);
        p.drawCircle(cx + s / 4, cy, s / 4);
        p.drawLine(cx - s / 2, cy, cx, cy + s / 2);
        p.drawLine(cx + s / 2, cy, cx, cy + s / 2);
        p.drawRectangle(cx - s / 10, cy - s / 2, s / 5, s / 2);
    }

    private void drawClubOutline(Pixmap p, int cx, int cy, int s) {
        p.drawCircle(cx - s / 4, cy, s / 4);
        p.drawCircle(cx + s / 4, cy, s / 4);
        p.drawCircle(cx, cy + s / 4, s / 4);
        p.drawRectangle(cx - s / 10, cy - s / 2, s / 5, s / 2);
    }

    private void drawRoughRect(Pixmap pixmap, int x, int y, int w, int h, int jitter) {
        for (int i = 0; i < w; i += 2) {
            pixmap.drawPixel(x + i, y + ((i * 13) % jitter));
            pixmap.drawPixel(x + i, y + h - 1 - ((i * 7) % jitter));
        }
        for (int i = 0; i < h; i += 2) {
            pixmap.drawPixel(x + ((i * 5) % jitter), y + i);
            pixmap.drawPixel(x + w - 1 - ((i * 11) % jitter), y + i);
        }
    }

    private Pixmap createGlowPixmap(int width, int height) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        int cx = width / 2;
        int cy = height / 2;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float dx = x - cx;
                float dy = y - cy;
                float dist = (float) Math.sqrt(dx * dx + dy * dy) / (width * 0.5f);
                float a = Math.max(0f, 0.45f - dist * 0.45f);
                pixmap.setColor(0.72f, 0.61f, 0.8f, a);
                pixmap.drawPixel(x, y);
            }
        }
        return pixmap;
    }

    private Pixmap createFogPixmap(int width, int height) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        int cx = width / 2;
        int cy = height / 2;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float dx = x - cx;
                float dy = y - cy;
                float dist = (float) Math.sqrt(dx * dx + dy * dy) / (width * 0.5f);
                float alpha = Math.max(0f, 0.12f - dist * 0.12f);
                pixmap.setColor(0.82f, 0.81f, 0.77f, alpha);
                pixmap.drawPixel(x, y);
            }
        }
        return pixmap;
    }

    private Pixmap createRestartButtonPixmap(int width, int height) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.7f, 0.65f, 0.53f, 1f));
        pixmap.fillRectangle(0, 0, width, height);
        pixmap.setColor(new Color(0.1f, 0.08f, 0.08f, 1f));
        drawRoughRect(pixmap, 0, 0, width, height, 3);
        pixmap.drawCircle(width / 2, height / 2, 20);
        pixmap.drawLine(width / 2 + 14, height / 2 + 10, width / 2 + 20, height / 2 + 2);
        pixmap.drawLine(width / 2 + 14, height / 2 + 10, width / 2 + 6, height / 2 + 7);
        return pixmap;
    }

    private float clamp(float value) {
        return Math.max(0f, Math.min(1f, value));
    }

    @Override
    public void dispose() {
        for (Texture texture : textures) {
            texture.dispose();
        }
        if (font != null) {
            font.dispose();
        }
    }
}

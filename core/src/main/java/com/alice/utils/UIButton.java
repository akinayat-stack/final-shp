package com.alice.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class UIButton {
    public final Rectangle bounds;
    public final String label;
    public boolean enabled = true;

    public UIButton(float x, float y, float w, float h, String label) {
        this.bounds = new Rectangle(x, y, w, h);
        this.label = label;
    }

    public boolean contains(float x, float y) {
        return bounds.contains(x, y);
    }

    public void drawFill(ShapeRenderer sr, boolean hover) {
        if (!enabled) sr.setColor(0.10f, 0.08f, 0.15f, 1f);
        else if (hover) sr.setColor(0.40f, 0.20f, 0.60f, 1f);
        else sr.setColor(0.15f, 0.10f, 0.25f, 1f);
        sr.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public void drawOutline(ShapeRenderer sr) {
        sr.setColor(Color.WHITE);
        sr.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public void drawLabel(SpriteBatch batch, BitmapFont font) {
        if (enabled) font.setColor(Color.WHITE);
        else font.setColor(Color.GRAY);
        com.badlogic.gdx.graphics.g2d.GlyphLayout layout =
            new com.badlogic.gdx.graphics.g2d.GlyphLayout(font, label);
        float tx = bounds.x + (bounds.width - layout.width) / 2f;
        float ty = bounds.y + (bounds.height + layout.height) / 2f;
        font.draw(batch, label, tx, ty);
    }
}

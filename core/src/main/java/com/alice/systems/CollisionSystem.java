package com.alice.systems;

import com.alice.components.CollisionComponent;
import com.alice.components.PositionComponent;
import com.alice.components.StateComponent;
import com.alice.components.TextureComponent;
import com.alice.components.VelocityComponent;
import com.alice.screens.GameScreen;
import com.alice.utils.Constants;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Rectangle;

public class CollisionSystem extends IteratingSystem {
    private final int[][] map;
    private final GameScreen screen;
    private final Rectangle tileRect = new Rectangle();
    private final Rectangle entityRect = new Rectangle();

    public CollisionSystem(int[][] map, GameScreen screen) {
        super(Family.all(PositionComponent.class, CollisionComponent.class, VelocityComponent.class).get(), 2);
        this.map = map;
        this.screen = screen;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent p = entity.getComponent(PositionComponent.class);
        CollisionComponent c = entity.getComponent(CollisionComponent.class);
        VelocityComponent v = entity.getComponent(VelocityComponent.class);
        TextureComponent tc = entity.getComponent(TextureComponent.class);

        float bw = c.bounds.width;
        float bh = c.bounds.height;
        float padX = (tc != null) ? (tc.renderWidth - bw) / 2f : 12f;
        float padY = (tc != null) ? (tc.renderHeight - bh) / 2f : 12f;

        float newX = p.x + v.vx * deltaTime;
        entityRect.set(newX + padX, p.y + padY, bw, bh);
        if (collidesWithWalls(entityRect)) {
            if (v.vx > 0) {
                int rightCol = (int) ((entityRect.x + entityRect.width) / Constants.TILE_SIZE);
                p.x = rightCol * Constants.TILE_SIZE - entityRect.width - padX - 0.5f;
            } else if (v.vx < 0) {
                int leftCol = (int) (entityRect.x / Constants.TILE_SIZE);
                p.x = (leftCol + 1) * Constants.TILE_SIZE - padX + 0.5f;
            }
            v.vx = 0;
        } else {
            p.x = newX;
        }

        float newY = p.y + v.vy * deltaTime;
        entityRect.set(p.x + padX, newY + padY, bw, bh);
        if (collidesWithWalls(entityRect)) {
            if (v.vy > 0) {
                int topRow = (int) ((entityRect.y + entityRect.height) / Constants.TILE_SIZE);
                p.y = topRow * Constants.TILE_SIZE - entityRect.height - padY - 0.5f;
            } else if (v.vy < 0) {
                int botRow = (int) (entityRect.y / Constants.TILE_SIZE);
                p.y = (botRow + 1) * Constants.TILE_SIZE - padY + 0.5f;
            }
            v.vy = 0;
        } else {
            p.y = newY;
        }

        c.bounds.setPosition(p.x + padX, p.y + padY);

        StateComponent s = entity.getComponent(StateComponent.class);
        if (s != null && s.isPlayer) {
            checkEnemyCollision(entity, p, c, padX, padY);
        }
    }

    private boolean collidesWithWalls(Rectangle r) {
        int rows = map.length;
        int cols = map[0].length;
        int leftCol = (int) (r.x / Constants.TILE_SIZE);
        int rightCol = (int) ((r.x + r.width - 0.01f) / Constants.TILE_SIZE);
        int bottomRow = (int) (r.y / Constants.TILE_SIZE);
        int topRow = (int) ((r.y + r.height - 0.01f) / Constants.TILE_SIZE);

        for (int row = bottomRow; row <= topRow; row++) {
            for (int col = leftCol; col <= rightCol; col++) {
                if (col < 0 || col >= cols || row < 0 || row >= rows) return true;
                int mapRow = rows - 1 - row;
                if (mapRow < 0 || mapRow >= rows) return true;
                if (map[mapRow][col] == 1) {
                    tileRect.set(col * Constants.TILE_SIZE, row * Constants.TILE_SIZE,
                        Constants.TILE_SIZE, Constants.TILE_SIZE);
                    if (r.overlaps(tileRect)) return true;
                }
            }
        }
        return false;
    }

    private void checkEnemyCollision(Entity player, PositionComponent pp, CollisionComponent pc, float padX, float padY) {
        TextureComponent pt = player.getComponent(TextureComponent.class);
        if (pt.invincible) return;
        Rectangle pr = new Rectangle(pp.x + padX, pp.y + padY, pc.bounds.width, pc.bounds.height);
        for (Entity g : screen.guards) {
            PositionComponent gp = g.getComponent(PositionComponent.class);
            CollisionComponent gc = g.getComponent(CollisionComponent.class);
            TextureComponent gt = g.getComponent(TextureComponent.class);
            float gPad = (gt.renderWidth - gc.bounds.width) / 2f;
            Rectangle gr = new Rectangle(gp.x + gPad, gp.y + gPad, gc.bounds.width, gc.bounds.height);
            if (pr.overlaps(gr)) {
                screen.damagePlayer();
                pt.invincible = true;
                pt.invincibleTimer = Constants.INVINCIBILITY_TIME;
                return;
            }
        }
        if (screen.valet != null) {
            PositionComponent vp = screen.valet.getComponent(PositionComponent.class);
            CollisionComponent vc = screen.valet.getComponent(CollisionComponent.class);
            TextureComponent vt = screen.valet.getComponent(TextureComponent.class);
            float vPad = (vt.renderWidth - vc.bounds.width) / 2f;
            Rectangle vr = new Rectangle(vp.x + vPad, vp.y + vPad, vc.bounds.width, vc.bounds.height);
            if (pr.overlaps(vr)) {
                screen.damagePlayer();
                pt.invincible = true;
                pt.invincibleTimer = Constants.INVINCIBILITY_TIME;
            }
        }
    }
}

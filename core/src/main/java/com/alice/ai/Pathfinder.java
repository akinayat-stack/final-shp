package com.alice.ai;

import com.alice.utils.Constants;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

public class Pathfinder {

    private static class Node {
        int col, row;
        float g, h, f;
        Node parent;

        Node(int col, int row) {
            this.col = col;
            this.row = row;
        }

        long key() {
            return ((long) col << 16) | (row & 0xFFFF);
        }
    }

    public static List<Vector2> findPath(int[][] map, float startX, float startY, float endX, float endY) {
        int rows = map.length;
        int cols = map[0].length;

        int startCol = (int) ((startX + 32) / Constants.TILE_SIZE);
        int startRow = (int) ((startY + 32) / Constants.TILE_SIZE);
        int endCol = (int) ((endX + 32) / Constants.TILE_SIZE);
        int endRow = (int) ((endY + 32) / Constants.TILE_SIZE);

        if (!isWalkable(map, startCol, startRow, rows, cols)) {
            return new ArrayList<>();
        }
        if (!isWalkable(map, endCol, endRow, rows, cols)) {
            int[] nearest = findNearestWalkable(map, endCol, endRow, rows, cols);
            if (nearest == null) return new ArrayList<>();
            endCol = nearest[0];
            endRow = nearest[1];
        }

        PriorityQueue<Node> open = new PriorityQueue<>((a, b) -> Float.compare(a.f, b.f));
        HashMap<Long, Node> openMap = new HashMap<>();
        HashSet<Long> closed = new HashSet<>();

        Node start = new Node(startCol, startRow);
        start.g = 0;
        start.h = heuristic(startCol, startRow, endCol, endRow);
        start.f = start.h;
        open.add(start);
        openMap.put(start.key(), start);

        int[][] directions = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1},
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };

        int maxIterations = 2000;
        int iter = 0;

        while (!open.isEmpty() && iter < maxIterations) {
            iter++;
            Node current = open.poll();
            if (current == null) break;
            openMap.remove(current.key());

            if (current.col == endCol && current.row == endRow) {
                return reconstructPath(current, rows);
            }

            closed.add(current.key());

            for (int[] dir : directions) {
                int nc = current.col + dir[0];
                int nr = current.row + dir[1];

                if (!isWalkable(map, nc, nr, rows, cols)) continue;

                if (dir[0] != 0 && dir[1] != 0) {
                    if (!isWalkable(map, current.col + dir[0], current.row, rows, cols)) continue;
                    if (!isWalkable(map, current.col, current.row + dir[1], rows, cols)) continue;
                }

                Node neighbor = new Node(nc, nr);
                if (closed.contains(neighbor.key())) continue;

                float moveCost = (dir[0] != 0 && dir[1] != 0) ? 1.414f : 1f;
                float tentativeG = current.g + moveCost;

                Node existing = openMap.get(neighbor.key());
                if (existing != null && tentativeG >= existing.g) continue;

                neighbor.g = tentativeG;
                neighbor.h = heuristic(nc, nr, endCol, endRow);
                neighbor.f = neighbor.g + neighbor.h;
                neighbor.parent = current;

                if (existing != null) open.remove(existing);
                open.add(neighbor);
                openMap.put(neighbor.key(), neighbor);
            }
        }

        return new ArrayList<>();
    }

    private static boolean isWalkable(int[][] map, int col, int row, int rows, int cols) {
        if (col < 0 || col >= cols || row < 0 || row >= rows) return false;
        int mapRow = rows - 1 - row;
        if (mapRow < 0 || mapRow >= rows) return false;
        return map[mapRow][col] != 1;
    }

    private static int[] findNearestWalkable(int[][] map, int col, int row, int rows, int cols) {
        for (int radius = 1; radius < 8; radius++) {
            for (int dc = -radius; dc <= radius; dc++) {
                for (int dr = -radius; dr <= radius; dr++) {
                    if (Math.abs(dc) != radius && Math.abs(dr) != radius) continue;
                    int nc = col + dc;
                    int nr = row + dr;
                    if (isWalkable(map, nc, nr, rows, cols)) {
                        return new int[]{nc, nr};
                    }
                }
            }
        }
        return null;
    }

    private static float heuristic(int x1, int y1, int x2, int y2) {
        int dx = Math.abs(x1 - x2);
        int dy = Math.abs(y1 - y2);
        return (dx + dy) + (1.414f - 2f) * Math.min(dx, dy);
    }

    private static List<Vector2> reconstructPath(Node end, int rows) {
        List<Vector2> path = new ArrayList<>();
        Node n = end;
        while (n != null) {
            float wx = n.col * Constants.TILE_SIZE;
            float wy = n.row * Constants.TILE_SIZE;
            path.add(0, new Vector2(wx, wy));
            n = n.parent;
        }
        if (path.size() > 1) path.remove(0);
        return path;
    }

    public static boolean hasLineOfSight(int[][] map, float x1, float y1, float x2, float y2) {
        int rows = map.length;
        int cols = map[0].length;
        int steps = 30;
        for (int i = 1; i <= steps; i++) {
            float t = i / (float) steps;
            float x = x1 + (x2 - x1) * t;
            float y = y1 + (y2 - y1) * t;
            int col = (int) (x / Constants.TILE_SIZE);
            int row = (int) (y / Constants.TILE_SIZE);
            int mapRow = rows - 1 - row;
            if (col < 0 || col >= cols || mapRow < 0 || mapRow >= rows) continue;
            if (map[mapRow][col] == 1) return false;
        }
        return true;
    }
}

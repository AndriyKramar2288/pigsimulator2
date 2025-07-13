package com.banew.utilites.path_search;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.Set;

public class TileGraph implements IndexedGraph<TileNode> {
    private final int width, height;
    private final TileNode[][] nodes;
    private final Array<Connection<TileNode>>[] connections;

    private final float tileSize;

    @SuppressWarnings("unchecked")
    public TileGraph(int width, int height, float tileSize, Set<Rectangle> collisions) {
        this.width = width;
        this.height = height;
        this.tileSize = tileSize;

        nodes = new TileNode[width][height];
        connections = new Array[width * height];

        int index = 0;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                TileNode node = new TileNode(x, y, index++);
                node.walkable = !isBlocked(x, y, collisions);
                nodes[x][y] = node;
            }
        }

        buildConnections();
        buildPain();
    }

    private boolean isBlocked(int x, int y, Set<Rectangle> collisions) {
        float wx = x * tileSize;
        float wy = y * tileSize;
        Rectangle tileRect = new Rectangle(wx, wy, tileSize, tileSize);
        return collisions.stream().anyMatch(c -> c.overlaps(tileRect));
    }

    private void buildConnections() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                TileNode from = nodes[x][y];
                Array<Connection<TileNode>> conns = new Array<>();

                if (!from.walkable) {
                    connections[from.index] = conns;
                    continue;
                }

                // 4 напрями
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        if (Math.abs(dx) + Math.abs(dy) != 1) continue;

                        int nx = x + dx;
                        int ny = y + dy;

                        if (inBounds(nx, ny)) {
                            TileNode to = nodes[nx][ny];
                            if (to.walkable) {
                                conns.add(new TileConnection(from, to));
                            }
                        }
                    }
                }

                connections[from.index] = conns;
            }
        }
    }

    private void buildPain() {
        int nodesPerMert = (int) (.75f / tileSize);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (!nodes[x][y].walkable) {
                    for (int dx = -nodesPerMert; dx <= nodesPerMert; dx++) {
                        for (int dy = -nodesPerMert; dy <= nodesPerMert; dy++) {
                            int nx = x + dx;
                            int ny = y + dy;
                            if (inBounds(nx, ny)) {
                                TileNode near = nodes[nx][ny];
                                if (near.walkable) {
                                    float new_cost = 1000 / (1 + new Vector2(dx, dy).scl(10).len2());
                                    near.cost += new_cost;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean inBounds(int x, int y) {
        return x >= 0 && y >= 0 && x < width && y < height;
    }

    public TileNode getNodeAt(int x, int y) {
        return inBounds(x, y) ? nodes[x][y] : null;
    }

    public TileNode getNodeAtWorld(float worldX, float worldY) {
        return getNodeAt((int)(worldX / tileSize), (int)(worldY / tileSize));
    }

    @Override
    public int getIndex(TileNode node) {
        return node.index;
    }

    @Override
    public int getNodeCount() {
        return width * height;
    }

    @Override
    public Array<Connection<TileNode>> getConnections(TileNode fromNode) {
        return connections[fromNode.index];
    }
}


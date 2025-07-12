package com.banew.utilites.path_search;

import com.badlogic.gdx.math.Vector2;
import lombok.Getter;

public class TileNode {
    public final int x, y;
    @Getter
    public final int index;
    public boolean walkable = true;
    public float cost = 1f;

    public TileNode(int x, int y, int index) {
        this.x = x;
        this.y = y;
        this.index = index;
    }

    public Vector2 toWorldPos(float tileSize) {
        return new Vector2(x * tileSize + tileSize / 2, y * tileSize + tileSize / 2);
    }
}

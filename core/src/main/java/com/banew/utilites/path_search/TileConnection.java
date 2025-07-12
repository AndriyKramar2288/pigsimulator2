package com.banew.utilites.path_search;

import com.badlogic.gdx.ai.pfa.Connection;

public class TileConnection implements Connection<TileNode> {
    private final TileNode from;
    private final TileNode to;

    public TileConnection(TileNode from, TileNode to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public TileNode getFromNode() {
        return from;
    }

    @Override
    public TileNode getToNode() {
        return to;
    }

    @Override
    public float getCost() {
        return to.cost;
    }
}


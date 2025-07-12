package com.banew.utilites.path_search;

import com.badlogic.gdx.ai.pfa.Heuristic;

public class ManhattanHeuristic implements Heuristic<TileNode> {
    @Override
    public float estimate(TileNode node, TileNode goal) {
        return Math.abs(goal.x - node.x) + Math.abs(goal.y - node.y);
    }
}

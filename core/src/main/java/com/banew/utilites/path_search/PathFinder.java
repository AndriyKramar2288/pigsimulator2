package com.banew.utilites.path_search;

import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PathFinder {
    private final TileGraph graph;

    public PathFinder(Set<Rectangle> collisions) {
        graph = new TileGraph(1000, 1000, .1f, collisions);
    }

    public List<Vector2> findPath(Vector2 start, Vector2 end) {
        List<Vector2> result = new ArrayList<>();
        TileNode startNode = graph.getNodeAtWorld(start.x, start.y);
        TileNode goalNode = graph.getNodeAtWorld(end.x, end.y);

        DefaultGraphPath<TileNode> path = new DefaultGraphPath<>();
        IndexedAStarPathFinder<TileNode> finder = new IndexedAStarPathFinder<>(graph, true);
        finder.searchNodePath(startNode, goalNode, new ManhattanHeuristic(), path);

        for (TileNode step : path) {
            Vector2 worldStep = step.toWorldPos(.1f);
            result.add(worldStep);
        }

        return result;
    }
}

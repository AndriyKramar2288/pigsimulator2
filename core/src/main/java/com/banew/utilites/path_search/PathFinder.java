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
    private final float graphTileSize;
    private final Vector2 mapCenter;

    public PathFinder(Set<Rectangle> collisions, int worldWidth, int worldHeight) {
        int nodePerMetr = 5;
        graphTileSize = 1f / nodePerMetr;
        mapCenter = new Vector2(worldWidth / 2f, worldHeight / 2f);

        graph = new TileGraph(worldWidth * nodePerMetr, worldHeight * nodePerMetr, graphTileSize, collisions);
    }

    public List<Vector2> findPath(Vector2 start, Vector2 end) {
        List<Vector2> result = new ArrayList<>();
        TileNode startNode = graph.getNodeAtWorld(start.x, start.y);
        TileNode goalNode = graph.getNodeAtWorld(end.x, end.y);

        if (startNode == null) return result;
        if (goalNode == null) goalNode = graph.getNodeAtWorld(mapCenter.x, mapCenter.y);

        DefaultGraphPath<TileNode> path = new DefaultGraphPath<>();
        IndexedAStarPathFinder<TileNode> finder = new IndexedAStarPathFinder<>(graph, true);
        finder.searchNodePath(startNode, goalNode, new ManhattanHeuristic(), path);

        for (TileNode step : path) {
            Vector2 worldStep = step.toWorldPos(graphTileSize);
            result.add(worldStep);
        }

        return result;
    }
}

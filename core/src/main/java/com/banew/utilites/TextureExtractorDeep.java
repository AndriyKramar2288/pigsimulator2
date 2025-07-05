package com.banew.utilites;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextureExtractorDeep implements TextureExtractor {
    private String region;
    private final int sizeX;
    private final int sizeY;
    private final int cordX;
    private final int cordY;
    private static final Map<String, TextureRegion[][]> cashedRegions = new HashMap<>();

    public TextureExtractorDeep(String region, int width, int height, Point cord) {
        this.region = region;
        this.sizeX = width;
        this.sizeY = height;
        this.cordX = cord.x;
        this.cordY = cord.y;
    }

    public static Point getTilePosition(int index, int width, int height) {
        if (index < 1 || index > width * height) {
            throw new IllegalArgumentException("Індекс виходить за межі сітки");
        }

        int adjustedIndex = index - 1;
        int row = adjustedIndex / width + 1;
        int column = adjustedIndex % width + 1;

        return new Point(column, row);
    }

    public static List<TextureRegion> fromOneSubtexture(
        String region, int width, int height, TextureAtlas atlas, Integer ... indexes
    ) {
        return Arrays.stream(indexes)
            .map(index -> new TextureExtractorDeep(
                region, width, height, getTilePosition(index, width, height
            )))
            .map(each -> each.extractRegions(atlas)).toList();
    }

    @Override
    public TextureRegion extractRegions(TextureAtlas atlas) {
        String key = region + "|" + sizeX + "|" + sizeY;
        TextureRegion[][] grid = cashedRegions.computeIfAbsent(key, s -> {

            TextureRegion fullRegion = atlas.findRegion(region);

            if (fullRegion == null) {
                throw new RuntimeException("Не знайшли регіон: " + region);
            }

            int tileWidth = fullRegion.getRegionWidth() / sizeX;
            int tileHeight = fullRegion.getRegionHeight() / sizeY;
            return fullRegion.split(tileWidth, tileHeight);
        });

        return grid[cordY - 1][cordX - 1];
    }
}

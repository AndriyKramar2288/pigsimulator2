package com.banew.utilites;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.banew.other.records.MatrixVector;

import java.util.*;
import java.util.stream.Collectors;

public class TextureExtractorDeep implements TextureExtractor {
    private final String region;
    private final int sizeX;
    private final int sizeY;
    private final int cordX;
    private final int cordY;
    private static final Map<String, TextureRegion[][]> cashedRegions = new HashMap<>();

    private final float heightScale;
    private final float widthScale;

    public TextureExtractorDeep(String region, MatrixVector size, MatrixVector cords, float widthScale, float heightScale) {
        this.region = region;
        this.sizeX = size.x();
        this.sizeY = size.y();
        this.cordX = cords.x();
        this.cordY = cords.y();
        this.widthScale = widthScale;
        this.heightScale = heightScale;
    }

    public static List<? extends TextureExtractor> fromOneSubtexture(
        String region, MatrixVector size, float widthScale, float heightScale, MatrixVector ... cords
    ) {
        return Arrays.stream(cords)
            .map(cord -> new TextureExtractorDeep(region, size, cord, widthScale, heightScale))
            .toList();
    }

    @Override
    public TextureRegion extractRegions(TextureAtlas atlas) {
        String key = region + "|" + sizeX + "|" + sizeY;
        TextureRegion[][] grid = cashedRegions.computeIfAbsent(key, s -> {

            TextureRegion fullRegion = atlas.findRegion(region);
            int tileWidth = fullRegion.getRegionWidth() / sizeX;
            int tileHeight = fullRegion.getRegionHeight() / sizeY;
            return fullRegion.split(tileWidth, tileHeight);
        });

        return grid[cordY - 1][cordX - 1];
    }

    @Override
    public float getWidthScale() {
        return widthScale;
    }

    @Override
    public float getHeightScale() {
        return heightScale;
    }
}

package com.banew.utilites;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.banew.other.records.MatrixVector;

import java.util.HashMap;
import java.util.Map;

public class TextureExtractorDeep implements TextureExtractor {
    private final String region;
    private final int sizeX;
    private final int sizeY;
    private final int cordX;
    private final int cordY;
    private static final Map<String, TextureRegion[][]> cashedRegions = new HashMap<>();

    public TextureExtractorDeep(String region, MatrixVector size, MatrixVector cords) {
        this.region = region;
        this.sizeX = size.x();
        this.sizeY = size.y();
        this.cordX = cords.x();
        this.cordY = cords.y();
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
}

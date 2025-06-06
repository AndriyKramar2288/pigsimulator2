package com.banew.utilites;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.Map;

public class TextureExtractorDeep implements TextureExtractor {
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

    private final String region;
    private final int sizeX;
    private final int sizeY;
    private final int cordX;
    private final int cordY;
    private final Map<String, TextureRegion[][]> cashedRegions;

    public TextureExtractorDeep(String region, int sizeX, int sizeY, int cordX, int cordY, Map<String, TextureRegion[][]> cashedRegions) {
        this.region = region;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.cordX = cordX;
        this.cordY = cordY;
        this.cashedRegions = cashedRegions;
    }
}

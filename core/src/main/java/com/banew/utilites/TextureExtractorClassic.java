package com.banew.utilites;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TextureExtractorClassic implements TextureExtractor {
    private final String region;

    @Override
    public TextureRegion extractRegions(TextureAtlas atlas) {
        return atlas.findRegion(region);
    }

    public TextureExtractorClassic(String region) {
        this.region = region;
    }
}

package com.banew.utilites;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TextureExtractorClassic implements TextureExtractor {
    private final String region;

    @Override
    public TextureRegion extractRegions(TextureAtlas atlas) {
        TextureRegion texture = atlas.findRegion(region);
        if (texture == null) throw new RuntimeException("Texture \"" + region + "\" was not found!");
        return texture;
    }

    public TextureExtractorClassic(String region) {
        this.region = region;
    }
}

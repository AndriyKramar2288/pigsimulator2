package com.banew.utilites;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lombok.Getter;

public class TextureExtractorClassic implements TextureExtractor {
    private final String region;
    private final float heightScale;
    private final float widthScale;

    @Override
    public TextureRegion extractRegions(TextureAtlas atlas) {
        TextureRegion texture = atlas.findRegion(region);
        if (texture == null) throw new RuntimeException("Texture \"" + region + "\" was not found!");
        return texture;
    }

    @Override
    public float getWidthScale() {
        return widthScale;
    }

    @Override
    public float getHeightScale() {
        return heightScale;
    }

    public TextureExtractorClassic(String region, float widthScale, float heightScale) {
        this.region = region;
        this.heightScale = heightScale;
        this.widthScale = widthScale;
    }
}

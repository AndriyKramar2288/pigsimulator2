package com.banew.utilites;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public interface TextureExtractor {
    public TextureRegion extractRegions(TextureAtlas atlas);
    public float getWidthScale();
    public float getHeightScale();
}

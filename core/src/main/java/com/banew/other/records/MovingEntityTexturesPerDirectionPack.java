package com.banew.other.records;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.banew.utilites.TextureExtractorDeep;

import java.util.List;

public record MovingEntityTexturesPerDirectionPack(
    TextureRegion waitingTexture,
    List<? extends TextureRegion> animation,
    Vector2 scaleTexture
) {
    public static MovingEntityTexturesPerDirectionPack fromOneSubtexture(
        String region, int width, int height, TextureAtlas atlas,
        Integer waitingIndex, Vector2 scaleTexture, Integer ... indexes
    ) {
        return new MovingEntityTexturesPerDirectionPack(
            new TextureExtractorDeep(
                region, width, height,
                TextureExtractorDeep.getTilePosition(waitingIndex, width, height)
            ).extractRegions(atlas),
            TextureExtractorDeep.fromOneSubtexture(region, width, height, atlas, indexes),
            scaleTexture
        );
    }
}

package com.banew.external;

import com.banew.entities.SpriteEntity;
import com.banew.factories.EntityFactory;

public class InitialSpriteEntity extends AbstractInitialEntity {
    private String region;
    @Override
    public SpriteEntity extractEntity(EntityFactory factory) {
        return factory.createSimpleSprite(getTexture().extractTextureExtractor(), getX(), getY());
    }
}

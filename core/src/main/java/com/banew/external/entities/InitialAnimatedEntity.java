package com.banew.external.entities;

import com.banew.entities.AnimatedEntity;
import com.banew.entities.SpriteEntity;
import com.banew.external.textures.AbstractInitialTexture;
import com.banew.factories.EntityFactory;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class InitialAnimatedEntity extends AbstractInitialEntity {
    private float animationDelay;
    private List<List<AbstractInitialTexture>> animations;

    @Override
    public SpriteEntity extractEntity(EntityFactory factory) {
        AnimatedEntity animatedEntity = factory.createAnimatedEntity(
            getX(), getY(),
            getSize_x(), getSize_y(),
            getTexture().extractTextureExtractor(),
            animationDelay,
            animations.stream()
                .map(animation -> animation.stream()
                    .map(AbstractInitialTexture::extractTextureExtractor).toList()
                ).toList()
        );
        animatedEntity.setSize(getSize_x(), getSize_y());
        return animatedEntity;
    }
}

package com.banew.external;

import com.banew.entities.SpriteEntity;
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
        return factory.createAnimatedEntity(
            getX(), getY(),
            getTexture().extractTextureExtractor(),
            animationDelay,
            animations.stream()
                .map(animation -> animation.stream()
                    .map(AbstractInitialTexture::extractTextureExtractor).toList()
                ).toList()
        );
    }
}

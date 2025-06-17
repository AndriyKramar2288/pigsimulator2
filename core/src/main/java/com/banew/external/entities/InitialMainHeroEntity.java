package com.banew.external.entities;

import com.badlogic.gdx.math.Vector2;
import com.banew.entities.SpriteEntity;
import com.banew.external.textures.AbstractInitialTexture;
import com.banew.factories.EntityFactory;
import com.banew.other.records.InitialMovingEntityTexturesPerDirectionPack;
import com.banew.other.records.MovingEntityTexturesPerDirectionPack;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class InitialMainHeroEntity extends AbstractInitialEntity {
    private Map<String, InitialMovingEntityTexturesPerDirectionPack> animations;

    @JsonIgnore
    private final List<String> directions = List.of("up", "left", "down", "right");

    @Override
    public SpriteEntity extractEntity(EntityFactory factory) {
        return factory.createMainHeroEntity(
            getX(), getY(),
            directions.stream()
                .map(direction -> new MovingEntityTexturesPerDirectionPack(
                    animations.get(direction).getWaitingTexture().extractTextureExtractor(),
                    animations.get(direction).getAnimation().stream()
                        .map(AbstractInitialTexture::extractTextureExtractor)
                        .toList(),
                    new Vector2(
                        animations.get(direction).getWaitingTexture().getWidthScale(),
                        animations.get(direction).getWaitingTexture().getHeightScale()
                    )
                )).toList()
        );
    }
}

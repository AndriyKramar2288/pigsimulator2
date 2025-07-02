package com.banew.external.entities;

import com.banew.entities.SpriteEntity;
import com.banew.factories.EntityFactory;
import com.banew.other.records.InitialMovingEntityTexturesPerDirectionPack;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class InitialMainHeroEntity extends AbstractInitialEntity {
    private Map<String, InitialMovingEntityTexturesPerDirectionPack> animations;

    @Override
    public SpriteEntity extractEntity(EntityFactory factory) {
        return factory.createMainHeroEntity(this);
    }
}

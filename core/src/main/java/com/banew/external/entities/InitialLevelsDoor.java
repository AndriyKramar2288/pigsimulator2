package com.banew.external.entities;

import com.banew.entities.SpriteEntity;
import com.banew.factories.EntityFactory;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class InitialLevelsDoor extends AbstractInitialEntity {
    private String levelFrom;
    private String levelTo;
    private String singleName;

    @Override
    public SpriteEntity extractEntity(EntityFactory factory) {
        return factory.createLevelsDoor(
            levelFrom, levelTo, singleName
        );
    }
}

package com.banew.external;

import com.banew.entities.SpriteEntity;
import com.banew.factories.EntityFactory;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class InitialGameLevel {
    private String levelName;
    private List<AbstractInitialEntity> initialEntities;
    private String mapName;

    public Set<SpriteEntity> getEntities(EntityFactory factory) {
        return initialEntities.stream()
            .map(e -> e.extractEntity(factory))
            .collect(Collectors.toSet());
    }
}

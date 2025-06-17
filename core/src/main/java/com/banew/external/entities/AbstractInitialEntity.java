package com.banew.external.entities;

import com.banew.entities.SpriteEntity;
import com.banew.external.textures.AbstractInitialTexture;
import com.banew.factories.EntityFactory;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = InitialSpriteEntity.class, name = "static"),
    @JsonSubTypes.Type(value = InitialAnimatedEntity.class, name = "animated"),
    @JsonSubTypes.Type(value = InitialMainHeroEntity.class, name = "main_hero")
})
@Data
public abstract class AbstractInitialEntity {
    private AbstractInitialTexture texture;
    private float size_x;
    private float size_y;
    private float x;
    private float y;
    private int priority = 5;
    public abstract SpriteEntity extractEntity(EntityFactory factory);
}

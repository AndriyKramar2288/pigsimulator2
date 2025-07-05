package com.banew.external.entities;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.banew.containers.GameLevel;
import com.banew.entities.SpriteEntity;
import com.banew.external.InitialGameLevel;
import com.banew.external.textures.AbstractInitialTexture;
import com.banew.factories.EntityFactory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = InitialSpriteEntity.class, name = "static"),
    @JsonSubTypes.Type(value = InitialAnimatedEntity.class, name = "animated"),
    @JsonSubTypes.Type(value = InitialMainHeroEntity.class, name = "main_hero"),
    @JsonSubTypes.Type(value = InitialZombie.class, name = "zombie"),
    @JsonSubTypes.Type(value = InitialLevelsDoor.class, name = "door")
})
@Data
public abstract class AbstractInitialEntity {
    private AbstractInitialTexture texture;
    private float size_x;
    private float size_y;
    private float x;
    private float y;
    private String nameInLevel = "";
    private int priority = 5;

    public void setGameLevel(InitialGameLevel gameLevel) {
        if (nameInLevel.isEmpty()) {
            return;
        }

        TiledMap map = new TmxMapLoader().load(gameLevel.getMapName());

        Rectangle rectangle = GameLevel.fromMapObject(
            map.getLayers().get("Objects").getObjects().get(nameInLevel)
        );

        size_x = rectangle.getWidth();
        size_y = rectangle.getHeight();
        x = rectangle.getX();
        y = rectangle.getY();
    }

    public abstract SpriteEntity extractEntity(EntityFactory factory);
}

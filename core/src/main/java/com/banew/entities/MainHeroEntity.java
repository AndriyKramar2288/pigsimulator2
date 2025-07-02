package com.banew.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.Body;
import com.banew.other.records.InitialMovingEntityTexturesPerDirectionPack;

import java.util.Map;

public class MainHeroEntity extends MovingEntity {
    public MainHeroEntity(
        Sprite sprite,
        Body body,
        Map<String, InitialMovingEntityTexturesPerDirectionPack> animations,
        TextureAtlas textureAtlas
    ) {
        super(sprite, body, animations, textureAtlas);
    }
}

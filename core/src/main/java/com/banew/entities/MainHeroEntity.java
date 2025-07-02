package com.banew.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.Body;
import com.banew.other.records.InitialMovingEntityTexturesPerDirectionPack;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

public class MainHeroEntity extends MovingEntity {
    @Setter
    @Getter
    private boolean isRunning;

    public MainHeroEntity(
        Sprite sprite,
        Body body,
        Map<String, InitialMovingEntityTexturesPerDirectionPack> animations,
        TextureAtlas textureAtlas
    ) {
        super(sprite, body, animations, textureAtlas);
    }

    @Override
    public void move(float stepX, float stepY) {
        animationList.get(movingSide).setFrameDuration(isRunning ? .15f : .25f);
        super.move(stepX, stepY);
    }
}

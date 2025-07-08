package com.banew.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.Body;
import com.banew.items.AbstractItem;
import com.banew.other.records.MovingEntityTexturesPerDirectionPack;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class MainHeroEntity extends MovingEntity {
    @Setter
    @Getter
    private boolean isRunning;
    @Getter
    private final Map<Integer, AbstractItem> inventory = new HashMap<>();

    public MainHeroEntity(
        Sprite sprite,
        Body body,
        Map<String, MovingEntityTexturesPerDirectionPack> animations,
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

package com.banew.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.List;

public class MainHeroEntity extends MovingEntity {
    public MainHeroEntity(Sprite sprite, List<TextureRegion> waitingRegions, List<Animation<TextureRegion>> animationList) {
        super(sprite, waitingRegions, animationList);
    }
}

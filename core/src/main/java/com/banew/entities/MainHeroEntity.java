package com.banew.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import java.util.List;

public class MainHeroEntity extends MovingEntity {
    public MainHeroEntity(
        Sprite sprite,
        Body body, List<TextureRegion> waitingRegions,
        List<Animation<TextureRegion>> animationList,
        List<Vector2> animationsScales
    ) {
        super(sprite, body, waitingRegions, animationList, animationsScales);
    }
}

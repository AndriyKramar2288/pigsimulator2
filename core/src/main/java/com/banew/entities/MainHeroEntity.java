package com.banew.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.List;

public class MainHeroEntity extends MovingEntity {
    public MainHeroEntity(Sprite sprite, List<TextureRegion> waitingRegions, List<Animation<TextureRegion>> animationList) {
        super(sprite, waitingRegions, animationList);
    }

//    @Override
//    public void replace(float stepX, float stepY) {
//        if (stepX < 0) {
//            getSprite().setRegion(animationList.get(3).getKeyFrame(timer, true));
//        } else if (stepX > 0) {
//            getSprite().setRegion(animationList.get(1).getKeyFrame(timer, true));
//        } else if (stepY > 0) {
//            getSprite().setRegion(animationList.get(2).getKeyFrame(timer, true));
//        } else if (stepY < 0) {
//            getSprite().setRegion(animationList.get(0).getKeyFrame(timer, true));
//        }
//    }
}

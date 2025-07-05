package com.banew.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import java.util.List;

public class Torch extends AnimatedEntity {
    public Torch(Sprite sprite,
                 Body body,
                 TextureRegion waitingRegion,
                 Float delayBetween,
                 List<List<TextureRegion>> regionsList,
                 Vector2 collisionScales) {
        super(sprite, body, waitingRegion, delayBetween, regionsList, collisionScales);
    }
}

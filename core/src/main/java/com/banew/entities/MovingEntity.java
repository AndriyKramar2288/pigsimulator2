package com.banew.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.List;

public class MovingEntity extends SpriteEntity {

    protected float timer = 0f;

    private List<TextureRegion> waitingRegions;
    protected List<Animation<TextureRegion>> animationList;

    public MovingEntity(
        Sprite sprite,
        List<TextureRegion> waitingRegions,
        List<Animation<TextureRegion>> animationList
    ) {
        super(sprite);
        this.waitingRegions = waitingRegions;
        this.animationList = animationList;
    }

    @Override
    public void update(float delta) {
        timer += delta;
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        update(Gdx.graphics.getDeltaTime());
        getSprite().draw(spriteBatch);
    }

    public void move(float stepX, float stepY) {
        replace(stepX, stepY);

        if (stepX > 0) {
            getSprite().setRegion(animationList.get(3).getKeyFrame(timer, true));
        } else if (stepX < 0) {
            getSprite().setRegion(animationList.get(1).getKeyFrame(timer, true));
        } else if (stepY < 0) {
            getSprite().setRegion(animationList.get(2).getKeyFrame(timer, true));
        } else if (stepY > 0) {
            getSprite().setRegion(animationList.get(0).getKeyFrame(timer, true));
        }
    }

}

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

    private int moovingSide = 0;
    private boolean isMoving = false;

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
        if (!isMoving) {
            getSprite().setRegion(waitingRegions.get(moovingSide));
        }
        getSprite().draw(spriteBatch);
        isMoving = false;
    }

    public void move(float stepX, float stepY) {
        replace(stepX, stepY);

        isMoving = true;
        moovingSide = computeMoovingSide(moovingSide, stepX, stepY);

        getSprite().setRegion(animationList.get(moovingSide).getKeyFrame(timer, true));
    }

    private int computeMoovingSide(int moovingSide, float stepX, float stepY) {
        if (stepX > 0) {
            return 3;
        } else if (stepX < 0) {
            return 1;
        } else if (stepY < 0) {
            return 2;
        } else if (stepY > 0) {
            return 0;
        } else {
            return 0;
        }
    }

}

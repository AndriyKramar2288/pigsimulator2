package com.banew.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import java.util.List;
import java.util.Set;

public class MovingEntity extends SpriteEntity {

    protected float timer = 0f;

    private final List<TextureRegion> waitingRegions;
    protected List<Animation<TextureRegion>> animationList;

    private int movingSide = 0;
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
            getSprite().setRegion(waitingRegions.get(movingSide));
        }
        getSprite().draw(spriteBatch);
        isMoving = false;
    }

    public void move(float stepX, float stepY, Set<Rectangle> collisionObjects) {
        for (Rectangle rectCollision : collisionObjects) {
            Rectangle rect = new Rectangle(
                getSprite().getX() + stepX,
                getSprite().getY() + stepY,
                getSprite().getWidth(),
                getSprite().getHeight()
            );

            if (rect.overlaps(rectCollision)) {
                return;
            }
        }

        replace(stepX, stepY);

        isMoving = true;
        movingSide = computeMovingSide(movingSide, stepX, stepY);

        getSprite().setRegion(animationList.get(movingSide).getKeyFrame(timer, true));
    }

    private int computeMovingSide(int movingSide, float stepX, float stepY) {
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

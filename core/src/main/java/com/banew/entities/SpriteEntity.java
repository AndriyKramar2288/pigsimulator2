package com.banew.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SpriteEntity {
    private final Sprite sprite;

    protected Sprite getSprite() {
        return sprite;
    }

    public SpriteEntity(Sprite sprite) {
        this.sprite = sprite;
    }

    public void move(float stepX, float stepY) {
        sprite.translate(stepX, stepY);
    }

    public void draw(SpriteBatch spriteBatch) {
        sprite.draw(spriteBatch);
    }
}

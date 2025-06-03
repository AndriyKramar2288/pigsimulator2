package com.banew.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SpriteEntity {
    private Sprite sprite;

    protected Sprite getSprite() {
        return sprite;
    }

    protected void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public SpriteEntity(Sprite sprite) {
        this.sprite = sprite;
    }

    public void replace(float stepX, float stepY) {
        sprite.translate(stepX, stepY);
    }

    public void update(float delta) {

    }

    public void draw(SpriteBatch spriteBatch) {
        sprite.draw(spriteBatch);
    }
}

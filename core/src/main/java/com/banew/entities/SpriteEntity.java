package com.banew.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

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

    public float getX() {
        return sprite.getX();
    }

    public float getY() {
        return sprite.getY();
    }

    public Vector2 getCenterCoordinates() {
        return new Vector2(
            sprite.getX() + (sprite.getWidth() / 2),
            sprite.getY() + (sprite.getHeight() / 2)
        );
    }

    public void draw(SpriteBatch spriteBatch) {
        sprite.draw(spriteBatch);
    }
}

package com.banew.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import lombok.Getter;
import lombok.Setter;

public class SpriteEntity {
    private final Sprite sprite;
    @Getter
    private final Body body;
    @Setter
    @Getter
    private int priority = 0;

    protected Sprite getSprite() {
        return sprite;
    }

    public SpriteEntity(Sprite sprite, Body body) {
        this.sprite = sprite;
        this.body = body;
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

    public void setTextureScale(float scale) {
        sprite.setOriginCenter();
        getSprite().setScale(scale);
    }

    public void setSize(float width, float height) {
        getSprite().setSize(width, height);
        sprite.setOriginCenter(); // уявний центр для scale і обертання (тупоголовий, після зміни розміру оновлюєм)
        sprite.setPosition(sprite.getX() - sprite.getWidth() / 2f, sprite.getY() - sprite.getHeight() / 2f);
    }

    public void draw(SpriteBatch spriteBatch) {
        sprite.setPosition(
            body.getPosition().x - sprite.getWidth() / 2f,
            body.getPosition().y - sprite.getHeight() / 2f
        );
        sprite.draw(spriteBatch);
    }
}

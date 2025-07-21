package com.banew.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.banew.containers.game.GameLevel;
import com.banew.other.records.GameContext;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

public abstract class SpriteEntity {
    @Getter
    @Setter
    @JsonIgnore
    private Sprite sprite;
    @Getter
    @Setter
    private Body body;
    @Setter
    @Getter
    private int priority = 0;
    @Setter
    private Vector2 currentScales = new Vector2(1, 1);

    public void setSpritePosition(Vector2 position) {
        sprite.setPosition(position.x, position.y);
    }

    public SpriteEntity(Sprite sprite, Body body, Vector2 collisionScales) {
        this.sprite = sprite;
        this.body = body;
        this.currentScales = collisionScales;
    }

    public abstract void render(GameContext context);

    public void step(GameContext context, GameLevel entityLevel) {
        if (body != null) {
            sprite.setPosition(
                body.getPosition().x - sprite.getWidth() / 2f,
                body.getPosition().y - sprite.getHeight() / 2f
            );
        }
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

    public Sprite getCollisionSprite(Texture texture) {
        Sprite sprite = new Sprite(texture);
        sprite.setSize(
            getSprite().getWidth() * currentScales.x,
            getSprite().getHeight() * currentScales.y
        );
        sprite.setPosition(
            getCenterCoordinates().x - sprite.getWidth() / 2,
            getCenterCoordinates().y - sprite.getHeight() / 2
        );
        return sprite;
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
        sprite.draw(spriteBatch);
    }

    public boolean cursorTouchDown(GameContext context) {
        int screenX = Gdx.input.getX();
        int screenY = Gdx.input.getY();
        Camera camera = context.camera();
        // Конвертація координат з екрану у світ
        Vector3 touchPos = new Vector3(screenX, screenY, 0);
        camera.unproject(touchPos); // важливо: без цього буде криво

        // Координати спрайта
        float x = getSprite().getX();
        float y = getSprite().getY();
        float width = getSprite().getWidth();
        float height = getSprite().getHeight();

        if (touchPos.x >= x && touchPos.x <= x + width &&
            touchPos.y >= y && touchPos.y <= y + height) {
            return true;
        }

        return false;
    }
}

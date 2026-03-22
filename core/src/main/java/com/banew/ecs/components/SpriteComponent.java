package com.banew.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.banew.other.records.GameContext;

public class SpriteComponent implements Component {
    public Sprite sprite;
    public Body body;
    public int priority = 0;
    public Vector2 currentScales = new Vector2(1, 1);

    public Vector2 getCenterCoordinates() {
        return new Vector2(
            sprite.getX() + (sprite.getWidth() / 2),
            sprite.getY() + (sprite.getHeight() / 2)
        );
    }

    public boolean cursorTouchDown(GameContext context) { // TODO gemini сказав, що тут пізда продуктивності, треба систему
        int screenX = Gdx.input.getX();
        int screenY = Gdx.input.getY();
        Camera camera = context.camera();
        // Конвертація координат з екрану у світ
        Vector3 touchPos = new Vector3(screenX, screenY, 0);
        camera.unproject(touchPos); // важливо: без цього буде криво

        // Координати спрайта
        float x = sprite.getX();
        float y = sprite.getY();
        float width = sprite.getWidth();
        float height = sprite.getHeight();

        if (touchPos.x >= x && touchPos.x <= x + width &&
            touchPos.y >= y && touchPos.y <= y + height) {
            return true;
        }

        return false;
    }

    public Sprite getCollisionSprite(Texture texture) {
        Sprite sprite = new Sprite(texture);
        sprite.setSize(
            sprite.getWidth() * currentScales.x,
            sprite.getHeight() * currentScales.y
        );
        sprite.setPosition(
            getCenterCoordinates().x - sprite.getWidth() / 2,
            getCenterCoordinates().y - sprite.getHeight() / 2
        );
        return sprite;
    }
}

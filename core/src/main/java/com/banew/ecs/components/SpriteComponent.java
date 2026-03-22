package com.banew.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

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

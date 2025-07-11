package com.banew.entities;

import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Chest extends ContainerEntity {

    public Chest(Sprite sprite, Body body, Vector2 collisionScales, Cursor fineCursor, Cursor badCursor) {
        super(sprite, body, collisionScales, fineCursor, badCursor);
    }

    @Override
    public String getName() {
        return "Скриня";
    }
}

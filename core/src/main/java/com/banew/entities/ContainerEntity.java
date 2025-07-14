package com.banew.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.banew.containers.ItemContainer;
import com.banew.other.records.GameContext;
import lombok.Getter;

public abstract class ContainerEntity extends SpriteEntity {
    private final Cursor fineCursor;
    private final Cursor badCursor;
    @Getter
    private final ItemContainer container;

    public static final float CRITICAL_DISTANCE = .5f;

    public ContainerEntity(Sprite sprite, Body body, Vector2 collisionScales, Cursor fineCursor, Cursor badCursor) {
        super(sprite, body, collisionScales);
        this.fineCursor = fineCursor;
        this.badCursor = badCursor;
        container = new ItemContainer(12);
    }

    public abstract String getName();

    @Override
    public void render(GameContext context) {
        if (cursorTouchDown(context)) {
            if (context.mainHeroEntity().getCenterCoordinates().sub(getCenterCoordinates()).len2() < CRITICAL_DISTANCE) {
                Gdx.graphics.setCursor(fineCursor);
                if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                    context.soundContainer().play("chest");
                    context.mainHeroEntity().setOpenedContainer(this);
                }
            }
            else {
                Gdx.graphics.setCursor(badCursor);
            }
        }
    }
}

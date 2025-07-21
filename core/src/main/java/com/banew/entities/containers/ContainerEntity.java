package com.banew.entities.containers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.banew.containers.game.ItemContainer;
import com.banew.entities.SpriteEntity;
import com.banew.other.records.CursorPair;
import com.banew.other.records.GameContext;
import lombok.Getter;

public abstract class ContainerEntity extends SpriteEntity {
    private final CursorPair cursors;
    @Getter
    private final ItemContainer container;

    public static final float CRITICAL_DISTANCE = .2f;

    public ContainerEntity(Sprite sprite, Body body, Vector2 collisionScales, CursorPair cursors) {
        super(sprite, body, collisionScales);
        this.cursors = cursors;

        container = new ItemContainer(12);
    }

    public abstract String getName();

    protected boolean isOpen(GameContext context) {
        return context.mainHeroEntity().getOpenedContainer() == this;
    }

    @Override
    public void render(GameContext context) {
        if (cursorTouchDown(context)) {
            boolean isOver = context.mainHeroEntity().getCenterCoordinates().sub(getCenterCoordinates()).len2()
                < CRITICAL_DISTANCE;

            cursors.use(isOver);

            if (isOver && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                context.soundContainer().play("chest");
                context.mainHeroEntity().setOpenedContainer(this);
            }
        }
    }
}

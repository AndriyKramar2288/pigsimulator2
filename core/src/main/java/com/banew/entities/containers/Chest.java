package com.banew.entities.containers;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.banew.other.records.CursorPair;
import com.banew.other.records.GameContext;

import java.util.List;

public class Chest extends ContainerEntity {

    private final List<TextureRegion> chests;

    public Chest(Sprite sprite,
                 Body body,
                 Vector2 collisionScales,
                 CursorPair cursorPair,
                 List<TextureRegion> chests) {
        super(sprite, body, collisionScales, cursorPair);
        this.chests = chests;
    }

    @Override
    public void render(GameContext context) {
        super.render(context);

        getSprite().setRegion(chests.get(isOpen(context) ? 1 : 0));
        setTextureScale(2);
    }

    @Override
    public String getName() {
        return "Скриня";
    }
}

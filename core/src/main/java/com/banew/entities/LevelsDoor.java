package com.banew.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import lombok.Getter;
import lombok.Setter;

public class LevelsDoor extends SpriteEntity {
    @Getter
    private final String lavelFrom;
    @Getter
    private final String lavelTo;

    @Getter
    @Setter
    private boolean isOpen = false;

    public LevelsDoor(Sprite sprite, Body body, String lavelFrom, String lavelTo) {
        super(sprite, body);

        this.lavelFrom = lavelFrom;
        this.lavelTo = lavelTo;
    }

    @Override
    public void render() {
        super.render();
    }
}

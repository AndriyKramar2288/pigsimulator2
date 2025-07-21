package com.banew.items;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.banew.entities.alive.AliveEntity;
import com.banew.other.records.GameContext;

public class StupidItem extends AbstractItem {
    public StupidItem(TextureRegion textureRegion, String name) {
        super(textureRegion, name);
    }

    @Override
    public void use(GameContext gameContext, AliveEntity user) {
        gameContext.soundContainer().play("hru");
    }
}

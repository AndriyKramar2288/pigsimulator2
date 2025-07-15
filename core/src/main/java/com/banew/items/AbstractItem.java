package com.banew.items;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.banew.entities.AliveEntity;
import com.banew.other.records.GameContext;
import lombok.Getter;

@Getter
public abstract class AbstractItem {
    private final TextureRegion textureRegion;
    private final String name;

    public AbstractItem(TextureRegion textureRegion, String name) {

        this.textureRegion = textureRegion;
        this.name = name;
    }

    public abstract void use(GameContext gameContext, AliveEntity user);
}

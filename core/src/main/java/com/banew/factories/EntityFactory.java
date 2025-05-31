package com.banew.factories;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.banew.entities.MainHeroEntity;
import com.banew.entities.SpriteEntity;

public class EntityFactory {
    private TextureAtlas textureAtlas;

    public EntityFactory(String atlas_path) {
        textureAtlas = new TextureAtlas(Gdx.files.internal(atlas_path));
    }

    public SpriteEntity createSimpleSprite(String region, Long x, Long y) {
        Sprite sprite = new Sprite(textureAtlas.createSprite(region));
        sprite.setPosition(x, y);
        sprite.setSize(1L, 1L);
        return new SpriteEntity(sprite);
    }

    public MainHeroEntity createMainHeroEntity() {
        Sprite sprite = new Sprite(textureAtlas.createSprite("hryak1/tile000"));
        sprite.setSize(1L, 1L);
        sprite.setPosition(
            0 - sprite.getWidth() / 2f,
            0 - sprite.getHeight() / 2f
        );
        return new MainHeroEntity(sprite);
    }
}

package com.banew.factories;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.banew.entities.AnimatedEntity;
import com.banew.entities.SpriteEntity;
import com.banew.other.records.TexturesRange;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntityFactory {
    private final TextureAtlas textureAtlas;

    public EntityFactory(String atlas_path) {
        textureAtlas = new TextureAtlas(Gdx.files.internal(atlas_path));
    }

    public SpriteEntity createSimpleSprite(String region, Long x, Long y) {
        Sprite sprite = new Sprite(textureAtlas.createSprite(region));
        sprite.setPosition(x, y);
        sprite.setSize(1L, 1L);
        return new SpriteEntity(sprite);
    }

    public AnimatedEntity createAnimatedEntity(
        Long x, Long y,
        String waitingRegionPath,
        Float delayBetween,
        TexturesRange ... range
    ) {
        List<List<TextureRegion>> regionsList = Arrays.stream(range)
            .map(this::initWaitingAnimations)
            .toList();

        TextureRegion waitingRegion = textureAtlas.createSprite(waitingRegionPath);
        Sprite sprite = new Sprite(waitingRegion);
        sprite.setPosition(x, y);
        sprite.setSize(1L, 1L);
        return new AnimatedEntity(sprite, waitingRegion, delayBetween, regionsList);
    }

//    public MainHeroEntity createMainHeroEntity(TexturesRange range) {
//        List<TextureRegion> regions = initWaitingAnimations(range);
//        Sprite sprite = new Sprite(textureAtlas.createSprite("hryak1/tile000"));
//        sprite.setSize(1L, 1L);
//        sprite.setPosition(
//            0 - sprite.getWidth() / 2f,
//            0 - sprite.getHeight() / 2f
//        );
//        return new MainHeroEntity(sprite, regions);
//    }

    private List<TextureRegion> initWaitingAnimations(TexturesRange range) {
        List<TextureRegion> waitingAnimations = new ArrayList<>();

        for (int i = range.start(); i <= range.end(); i++) {
            String name = range.prefix() + String.format("%03d", i);
            waitingAnimations.add(textureAtlas.findRegion(name));
        }

        return waitingAnimations;
    }
}

package com.banew.factories;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.banew.entities.AnimatedEntity;
import com.banew.entities.MovingEntity;
import com.banew.entities.SpriteEntity;
import com.banew.other.records.MovingEntityTexturesPerDirectionPack;
import com.banew.other.records.TexturesRange;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntityFactory {
    private final TextureAtlas textureAtlas;

    public EntityFactory(String atlas_path) {
        textureAtlas = new TextureAtlas(Gdx.files.internal(atlas_path));
    }

    public SpriteEntity createSimpleSprite(String region, Float x, Float y) {
        Sprite sprite = generateBasicSprite(textureAtlas.createSprite(region), x, y);
        return new SpriteEntity(sprite);
    }

    public AnimatedEntity createAnimatedEntity(
        Float x, Float y,
        String waitingRegionPath,
        Float delayBetween,
        TexturesRange ... range
    ) {
        List<List<TextureRegion>> regionsList = Arrays.stream(range)
            .map(this::initWaitingAnimations)
            .toList();

        TextureRegion waitingRegion = textureAtlas.createSprite(waitingRegionPath);
        Sprite sprite = generateBasicSprite(waitingRegion, x, y);

        return new AnimatedEntity(sprite, waitingRegion, delayBetween, regionsList);
    }

    public MovingEntity createMovingEntity(
        Float x, Float y,
        List<MovingEntityTexturesPerDirectionPack> texturePacks
    ) {
        Sprite sprite = generateBasicSprite(textureAtlas.findRegion(texturePacks.get(0).waitingTexture()), x, y);

        return new MovingEntity(
            sprite,
            texturePacks.stream()
                .map(MovingEntityTexturesPerDirectionPack::waitingTexture)
                .map(t -> (TextureRegion) textureAtlas.findRegion(t))
                .toList(),
            texturePacks.stream()
                .map(range -> new Animation<TextureRegion>(
                    0.25f,
                    initWaitingAnimations(range.animation()).toArray(new TextureRegion[0])
                ))
                .toList()
        );
    }

    private Sprite generateBasicSprite(TextureRegion region, Float x, Float y) {
        Sprite sprite = new Sprite(region);
        sprite.setPosition(x, y);
        sprite.setSize(1L, 1L);
        return sprite;
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

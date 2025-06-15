package com.banew.factories;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.banew.entities.AnimatedEntity;
import com.banew.entities.MainHeroEntity;
import com.banew.entities.MovingEntity;
import com.banew.entities.SpriteEntity;
import com.banew.external.GeneralSettings;
import com.banew.other.records.MovingEntityTexturesPerDirectionPack;
import com.banew.utilites.TextureExtractor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityFactory {
    private final TextureAtlas textureAtlas;
    private final Map<String, TextureRegion[][]> cashedRegions;

    public EntityFactory(String atlas_path, GeneralSettings generalSettings) {
        textureAtlas = new TextureAtlas(Gdx.files.internal(atlas_path));
        cashedRegions = new HashMap<>();
    }

    public SpriteEntity createSimpleSprite(TextureExtractor textureSource, Float x, Float y) {
        Sprite sprite = generateBasicSprite(textureSource.extractRegions(textureAtlas), x, y);
        return new SpriteEntity(sprite);
    }

    public AnimatedEntity createAnimatedEntity(
        Float x, Float y,
        TextureExtractor waitingTextureSource,
        Float delayBetween,
        List<List<TextureExtractor>> rangeSources
    ) {
        List<List<TextureRegion>> regionsList = rangeSources.stream()
            .map(l -> l.stream()
                .map(src -> src.extractRegions(textureAtlas))
                .toList()
            ).toList();

        TextureRegion waitingRegion = waitingTextureSource.extractRegions(textureAtlas);
        Sprite sprite = generateBasicSprite(waitingRegion, x, y);

        return new AnimatedEntity(sprite, waitingRegion, delayBetween, regionsList);
    }

    public MovingEntity createMovingEntity(
        Float x, Float y,
        List<MovingEntityTexturesPerDirectionPack> texturePacks
    ) {
        Sprite sprite = generateBasicSprite(texturePacks.get(0).waitingTexture().extractRegions(textureAtlas), x, y);

        return new MovingEntity(
            sprite,
            texturePacks.stream()
                .map(MovingEntityTexturesPerDirectionPack::waitingTexture)
                .map(t -> t.extractRegions(textureAtlas))
                .toList(),
            texturePacks.stream()
                .map(range -> new Animation<TextureRegion>(
                    0.25f,
                    range.animation().stream()
                        .map(a -> a.extractRegions(textureAtlas))
                        .toList().toArray(new TextureRegion[0])
                ))
                .toList()
        );
    }

    public MainHeroEntity createMainHeroEntity(
        Float x, Float y,
        List<MovingEntityTexturesPerDirectionPack> texturePacks
    ) {
        Sprite sprite = generateBasicSprite(texturePacks.get(0).waitingTexture().extractRegions(textureAtlas), x, y);

        return new MainHeroEntity(
            sprite,
            texturePacks.stream()
                .map(MovingEntityTexturesPerDirectionPack::waitingTexture)
                .map(t -> t.extractRegions(textureAtlas))
                .toList(),
            texturePacks.stream()
                .map(range -> new Animation<TextureRegion>(
                    0.25f,
                    range.animation().stream()
                        .map(a -> a.extractRegions(textureAtlas))
                        .toList().toArray(new TextureRegion[0])
                ))
                .toList()
        );
    }

    private Sprite generateBasicSprite(TextureRegion region, Float x, Float y) {
        Sprite sprite = new Sprite(region);
        sprite.setPosition(x, y);
        sprite.setSize(1f, 1f);
        return sprite;
    }
}

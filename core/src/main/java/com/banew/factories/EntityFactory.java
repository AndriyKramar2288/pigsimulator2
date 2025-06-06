package com.banew.factories;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.banew.entities.AnimatedEntity;
import com.banew.entities.MainHeroEntity;
import com.banew.entities.MovingEntity;
import com.banew.entities.SpriteEntity;
import com.banew.other.records.MovingEntityTexturesPerDirectionPack;
import com.banew.other.records.TexturesRange;
import com.banew.utilites.TextureExtractor;

import java.util.*;

public class EntityFactory {
    private final TextureAtlas textureAtlas;
    private final Map<String, TextureRegion[][]> cashedRegions;

    public EntityFactory(String atlas_path) {
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
        List<TextureExtractor> ... rangeSources
    ) {
        List<List<TextureRegion>> regionsList = Arrays.stream(rangeSources)
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

    public MainHeroEntity createMainHeroEntity(
        Float x, Float y,
        List<MovingEntityTexturesPerDirectionPack> texturePacks
    ) {
        Sprite sprite = generateBasicSprite(textureAtlas.findRegion(texturePacks.get(0).waitingTexture()), x, y);

        return new MainHeroEntity(
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

    private List<TextureRegion> initWaitingAnimations(TexturesRange range) {
        List<TextureRegion> waitingAnimations = new ArrayList<>();

        for (int i = range.start(); i <= range.end(); i++) {
            String name = range.prefix() + String.format("%03d", i);
            waitingAnimations.add(textureAtlas.findRegion(name));
        }

        return waitingAnimations;
    }
}

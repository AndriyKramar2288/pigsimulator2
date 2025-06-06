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

import java.util.*;

public class EntityFactory {
    private final TextureAtlas textureAtlas;
    private final Map<String, TextureRegion[][]> cashedRegions;

    public EntityFactory(String atlas_path) {
        textureAtlas = new TextureAtlas(Gdx.files.internal(atlas_path));
        cashedRegions = new HashMap<>();
    }

    public SpriteEntity createSimpleSprite(String region, Float x, Float y) {
        Sprite sprite = generateBasicSprite(textureAtlas.createSprite(region), x, y);
        return new SpriteEntity(sprite);
    }

    public SpriteEntity createSimpleSprite(String textureRegion, int sizeX, int sizeY, int cordX, int cordY, Float x, Float y) {
        Sprite sprite = generateBasicSprite(resolveSubTexture(
            textureRegion,
            sizeX, sizeY,
            cordX, cordY
        ), x, y);
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

    private List<TextureRegion> initWaitingAnimations(String region, int sizeX, int sizeY, Vector2 ... cords) {
        return Arrays.stream(cords).map(cord -> {
            return resolveSubTexture(region, sizeX, sizeY, (int) cord.x, (int) cord.y);
        }).toList();
    } // TODO: to delete

    private TextureRegion resolveSubTexture(String region, int sizeX, int sizeY, int cordX, int cordY) {
        String key = region + "|" + sizeX + "|" + sizeY;
        TextureRegion[][] grid = cashedRegions.computeIfAbsent(key, s -> {

            TextureRegion fullRegion = textureAtlas.findRegion(region);
            int tileWidth = fullRegion.getRegionWidth() / sizeX;
            int tileHeight = fullRegion.getRegionHeight() / sizeY;
            return fullRegion.split(tileWidth, tileHeight);
        });

        return grid[cordY - 1][cordX - 1];
    } // TODO: delete this method
}

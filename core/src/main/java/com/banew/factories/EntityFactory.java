package com.banew.factories;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.banew.entities.AnimatedEntity;
import com.banew.entities.MainHeroEntity;
import com.banew.entities.MovingEntity;
import com.banew.entities.SpriteEntity;
import com.banew.external.GeneralSettings;
import com.banew.other.records.MovingEntityTexturesPerDirectionPack;
import com.banew.utilites.TextureExtractor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityFactory {
    private final TextureAtlas textureAtlas;
    private final Map<String, TextureRegion[][]> cashedRegions;
    private final World world;

    public EntityFactory(GeneralSettings generalSettings, World world) {
        this.world = world;
        String atlas_path = "textures-generated/game.atlas";
        textureAtlas = new TextureAtlas(Gdx.files.internal(atlas_path));
        cashedRegions = new HashMap<>();
    }

    public SpriteEntity createSimpleSprite(TextureExtractor textureSource, Float x, Float y, Float size_x, Float size_y) {
        Sprite sprite = generateBasicSprite(textureSource.extractRegions(textureAtlas), x, y, size_x, size_y);
        Body body = generateBasicBody(x, y, size_x, size_y);

        return new SpriteEntity(sprite, body);
    }

    public AnimatedEntity createAnimatedEntity(
        Float x, Float y,
        Float size_x, Float size_y,
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
        Sprite sprite = generateBasicSprite(waitingRegion, x, y, size_x, size_y);

        Body body = generateBasicBody(
            x, y,
            waitingTextureSource.getWidthScale() * size_x, waitingTextureSource.getHeightScale() * size_y
        );

        return new AnimatedEntity(sprite, body, waitingRegion, delayBetween, regionsList);
    }

    public MovingEntity createMovingEntity(
        Float x, Float y,
        Float width, Float height,
        List<MovingEntityTexturesPerDirectionPack> texturePacks
    ) {
        Sprite sprite = generateBasicSprite(texturePacks.get(0).waitingTexture().extractRegions(textureAtlas), x, y, 1f, 1f);
        Body body = generateDynamicBody(x, y, width, height);

        return new MovingEntity(
            sprite,
            body,
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
                .toList(),
            texturePacks.stream()
                .map(MovingEntityTexturesPerDirectionPack::waitingTexture)
                .map(w -> new Vector2(w.getWidthScale(), w.getHeightScale()))
                .toList()
        );
    }

    public MainHeroEntity createMainHeroEntity(
        Float x, Float y,
        List<MovingEntityTexturesPerDirectionPack> texturePacks
    ) {
        Sprite sprite = generateBasicSprite(texturePacks.get(0).waitingTexture().extractRegions(textureAtlas), x, y, 1f, 1f);
        Body body = generateDynamicBody(x, y, .1f, .1f);

        return new MainHeroEntity(
            sprite,
            body,
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
                .toList(),
            texturePacks.stream()
                .map(MovingEntityTexturesPerDirectionPack::scaleTexture)
                .toList()
        );
    }

    // -------------------- GENERATE BASIC SMTH ------------------------

    private Body generateBasicBody(Float x, Float y, Float size_x, Float size_y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;

        // позиція — ЦЕНТР фікстури!
        bodyDef.position.set(x + size_x / 2f, y + size_y / 2f);

        Body body = world.createBody(bodyDef);
        body.createFixture(generateBasicFicture(size_x, size_y));
        return body;
    }

    private Body generateDynamicBody(Float x, Float y, Float size_x, Float size_y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        // позиція — ЦЕНТР фікстури!
        bodyDef.position.set(x + size_x / 2f, y + size_y / 2f);

        Body body = world.createBody(bodyDef);
        body.createFixture(generateBasicFicture(size_x, size_y));
        return body;
    }

    private FixtureDef generateBasicFicture(float width, float height) {
        FixtureDef fixtureDef = new FixtureDef();

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2);

        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0.5f;
        return fixtureDef;
    }

    private Sprite generateBasicSprite(TextureRegion region, Float x, Float y, Float size_x, Float size_y) {
        Sprite sprite = new Sprite(region);
        sprite.setPosition(x, y);
        sprite.setSize(size_x, size_y);
        return sprite;
    }
}

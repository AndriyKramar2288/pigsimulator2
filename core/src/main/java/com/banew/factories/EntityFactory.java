package com.banew.factories;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.banew.containers.GameContainer;
import com.banew.containers.GameLevel;
import com.banew.entities.*;
import com.banew.external.GeneralSettings;
import com.banew.external.InitialGameLevel;
import com.banew.external.entities.InitialAnimatedEntity;
import com.banew.external.entities.InitialMainHeroEntity;
import com.banew.external.entities.InitialSpriteEntity;
import com.banew.external.entities.InitialZombie;
import com.banew.utilites.TextureExtractor;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityFactory {
    private final TextureAtlas textureAtlas;
    private final Map<String, TextureRegion[][]> cashedRegions;
    @Setter
    private MainHeroEntity mainHeroEntity;
    @Setter
    private GameLevel currentGameLevel;

    public EntityFactory(GeneralSettings generalSettings) {
        String atlas_path = "textures-generated/game.atlas";
        textureAtlas = new TextureAtlas(Gdx.files.internal(atlas_path));
        cashedRegions = new HashMap<>();
    }

    public SpriteEntity createSimpleSprite(InitialSpriteEntity src) {
        Sprite sprite = generateBasicSprite(
            src.getTexture().extractTextureExtractor().extractRegions(textureAtlas),
            src.getX(), src.getY(),
            src.getSize_x(), src.getSize_y()
        );
        Body body = generateBasicBody(
            src.getX(), src.getY(),
            src.getSize_x(), src.getSize_y()
        );

        return new SpriteEntity(sprite, body);
    }

    public AnimatedEntity createAnimatedEntity(InitialAnimatedEntity src) {
        List<List<TextureRegion>> regionsList = src.getAnimations().stream()
            .map(l -> l.stream()
                .map(t_src -> t_src.extractTextureExtractor().extractRegions(textureAtlas))
                .toList()
            ).toList();

        TextureExtractor extractor = src.getTexture().extractTextureExtractor();
        TextureRegion waitingRegion = extractor.extractRegions(textureAtlas);
        Sprite sprite = generateBasicSprite(
            waitingRegion,
            src.getX(), src.getY(),
            src.getSize_x(), src.getSize_y()
        );

        Body body = generateBasicBody(
            src.getX(), src.getY(),
            extractor.getWidthScale() * src.getSize_x(),
            extractor.getHeightScale() * src.getSize_y()
        );

        AnimatedEntity entity = new AnimatedEntity(
            sprite,
            body,
            waitingRegion,
            src.getAnimationDelay(),
            regionsList
        );

        entity.setCurrentScales(new Vector2(extractor.getWidthScale(), extractor.getHeightScale()));

        return entity;
    }

    public MainHeroEntity createMainHeroEntity(InitialMainHeroEntity src) {
        Sprite sprite = generateBasicSprite(
            src.getAnimations().get("down").getWaitingTexture().extractTextureExtractor().extractRegions(textureAtlas),
            src.getX(), src.getY(), src.getSize_x(), src.getSize_y()
        );
        Body body = generateDynamicBody(src.getX(), src.getY(), src.getSize_x(), src.getSize_y());

        mainHeroEntity = new MainHeroEntity(
            sprite,
            body,
            src.getAnimations(),
            textureAtlas
        );

        return mainHeroEntity;
    }

    public Zombie createZombie(InitialZombie src) {
        if (mainHeroEntity == null) {
            throw new RuntimeException("Головний перс ще не був створений!");
        }

        Sprite sprite = generateBasicSprite(
            src.getAnimations().get("down").getWaitingTexture().extractTextureExtractor().extractRegions(textureAtlas),
            src.getX(), src.getY(), src.getSize_x(), src.getSize_y()
        );
        Body body = generateDynamicBody(src.getX(), src.getY(), src.getSize_x(), src.getSize_y());

        return new Zombie(
            sprite,
            body,
            src.getAnimations(),
            textureAtlas,
            mainHeroEntity,
            currentGameLevel.getCollisions()
        );
    }

    public LevelsDoor createLevelsDoor(
        String levelFrom, String levelTo,
        String singleName
    ) {

        List<InitialGameLevel> levels = GeneralSettings.importSettings().getGameLevels();

        InitialGameLevel level = levels.stream()
            .filter(l -> l.getLevelName().equals(levelFrom))
            .findFirst()
            .orElseThrow(
            () -> new RuntimeException("Ти обісрався, братішка!")
        );

        TiledMap map = new TmxMapLoader().load(level.getMapName());

        Rectangle rectangle = GameLevel.fromMapObject(
            map.getLayers().get("Doors").getObjects().get(singleName)
        );

        Sprite invisibleSprite = new Sprite();
        invisibleSprite.setPosition(rectangle.getX(), rectangle.getY());
        invisibleSprite.setSize(0, 0);
        invisibleSprite.setTexture(GameContainer.WHITE_PIXEL);

        return new LevelsDoor(
            invisibleSprite,
            generateBasicBody(rectangle.getX(), rectangle.getY(), 0f, 0f),
            levelFrom, levelTo
        );
    }

    // -------------------- GENERATE BASIC SMTH ------------------------

    private Body generateBasicBody(Float x, Float y, Float size_x, Float size_y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;

        // позиція — ЦЕНТР фікстури!
        bodyDef.position.set(x + size_x / 2f, y + size_y / 2f);

        Body body = currentGameLevel.getWorld().createBody(bodyDef);
        body.createFixture(generateBasicFicture(size_x, size_y));
        return body;
    }

    private Body generateDynamicBody(Float x, Float y, Float size_x, Float size_y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        // позиція — ЦЕНТР фікстури!
        bodyDef.position.set(x + size_x / 2f, y + size_y / 2f);
        bodyDef.bullet = true;

        Body body = currentGameLevel.getWorld().createBody(bodyDef);
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

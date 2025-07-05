package com.banew.factories;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
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
import com.banew.other.records.MovingEntityTexturesPerDirectionPack;
import com.banew.utilites.TextureExtractorDeep;
import lombok.Setter;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;

public class EntityFactory {
    private final TextureAtlas textureAtlas;
    private final Map<String, TextureRegion[][]> cashedRegions;
    @Setter
    private GameLevel currentGameLevel;

    public EntityFactory(GeneralSettings generalSettings) {
        String atlas_path = generalSettings.getMain_atlas_src();
        textureAtlas = new TextureAtlas(Gdx.files.internal(atlas_path));
        cashedRegions = new HashMap<>();
    }

    private final Map<String, Function<MapObject, SpriteEntity>> resolver = Map.of(
        "torch", this::createTorch,
        "main_hero", this::createMainHeroEntity,
        "door", this::createLevelsDoor,
        "zombie", this::createZombie
    );

    public Set<SpriteEntity> resolveMapObjects(MapObjects objects) {
        Set<SpriteEntity> result = new HashSet<>();

        for (MapObject o : objects) {
            if (o.getProperties().get("Class") != null) {
                var function = resolver.get(o.getProperties().get("Class"));
                if (function != null) {
                    result.add(function.apply(o));
                }
            }
        }

        return result;
    }

    public Torch createTorch(MapObject object) {

        Rectangle rectangle = GameLevel.fromMapObject(object);

        TextureRegion waiting = new TextureExtractorDeep(
            "Characters/Torch Animated",
            4, 2, new Point(1, 1)
        ).extractRegions(textureAtlas);

        List<TextureRegion> animation = TextureExtractorDeep.fromOneSubtexture(
            "Characters/Torch Animated",
            4, 2, textureAtlas, 1, 2, 3, 4, 5, 6, 7, 8
        );

        return new Torch(
            generateBasicSprite(rectangle, waiting),
            generateBasicBody(rectangle, .15f, .35f),
            waiting,
            0f,
            List.of(animation),
            new Vector2(.15f, .35f)
        );
    }

    public MainHeroEntity createMainHeroEntity(MapObject object) {
        Rectangle rectangle = GameLevel.fromMapObject(object);

        Map<String, MovingEntityTexturesPerDirectionPack> textures = Map.of(
            "right", MovingEntityTexturesPerDirectionPack.fromOneSubtexture(
                "Characters/Basic Charakter Spritesheet", 4, 4, textureAtlas,
                14, new Vector2(.25f, .35f), 13, 15, 16
            ),
            "up", MovingEntityTexturesPerDirectionPack.fromOneSubtexture(
                "Characters/Basic Charakter Spritesheet", 4, 4, textureAtlas,
                6, new Vector2(.32f, .35f), 5, 7, 8
            ),
            "left", MovingEntityTexturesPerDirectionPack.fromOneSubtexture(
                "Characters/Basic Charakter Spritesheet", 4, 4, textureAtlas,
                10, new Vector2(.25f, .35f), 9, 11, 12
            ),
            "down", MovingEntityTexturesPerDirectionPack.fromOneSubtexture(
                "Characters/Basic Charakter Spritesheet", 4, 4, textureAtlas,
                2, new Vector2(.32f, .35f), 1, 3, 4
            )
        );

        return new MainHeroEntity(
            generateBasicSprite(rectangle, textures.get("down").waitingTexture()),
            generateDynamicBody(rectangle, textures.get("down").scaleTexture().x, textures.get("down").scaleTexture().y),
            textures,
            textureAtlas
        );
    }

    public Zombie createZombie(MapObject mapObject) {
        Rectangle rectangle = GameLevel.fromMapObject(mapObject);

        Map<String, MovingEntityTexturesPerDirectionPack> textures = Map.of(
            "left", MovingEntityTexturesPerDirectionPack.fromOneSubtexture(
                "Characters/zombie_n_skeleton2", 9, 4, textureAtlas,
                11, new Vector2(.7f, .7f), 10, 11, 12
            ),
            "up", MovingEntityTexturesPerDirectionPack.fromOneSubtexture(
                "Characters/zombie_n_skeleton2", 9, 4, textureAtlas,
                29, new Vector2(.7f, .7f), 28, 29, 30
            ),
            "right", MovingEntityTexturesPerDirectionPack.fromOneSubtexture(
                "Characters/zombie_n_skeleton2", 9, 4, textureAtlas,
                20, new Vector2(.7f, .7f), 19, 20, 21
            ),
            "down", MovingEntityTexturesPerDirectionPack.fromOneSubtexture(
                "Characters/zombie_n_skeleton2", 9, 4, textureAtlas,
                2, new Vector2(.7f, .7f), 1, 2, 3
            )
        );

        return new Zombie(
            generateBasicSprite(rectangle, textures.get("down").waitingTexture()),
            generateDynamicBody(rectangle, textures.get("down").scaleTexture().x, textures.get("down").scaleTexture().y),
            textures,
            textureAtlas,
            currentGameLevel.getCollisions()
        );
    }

    public LevelsDoor createLevelsDoor(MapObject mapObject) {
        String from = mapObject.getProperties().get("from", String.class);
        String to = mapObject.getProperties().get("to", String.class);

        Rectangle rectangle = GameLevel.fromMapObject(mapObject);

        Sprite invisibleSprite = new Sprite();
        invisibleSprite.setPosition(rectangle.getX(), rectangle.getY());
        invisibleSprite.setSize(rectangle.getWidth(), rectangle.getHeight());
        invisibleSprite.setTexture(GameContainer.WHITE_PIXEL);

        return new LevelsDoor(
            invisibleSprite,
            from, to, mapObject.getName()
        );
    }

    // -------------------- GENERATE BASIC SMTH ------------------------

    private Body generateBasicBody(Float x, Float y, Float size_x, Float size_y) {
        return generateBasicBody(x, y, size_x, size_y, 1f, 1f);
    }

    private Body generateBasicBody(Rectangle rectangle, Float scaleX, Float scaleY) {
        return generateBasicBody(
            rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight(),
            scaleX, scaleY
        );
    }

    private Body generateBasicBody(Float x, Float y, Float size_x, Float size_y, Float scaleX, Float scaleY) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.fixedRotation = true;
        // позиція — ЦЕНТР фікстури!
        bodyDef.position.set(x + size_x / 2f, y + size_y / 2f);

        Body body = currentGameLevel.getWorld().createBody(bodyDef);
        body.createFixture(generateBasicFicture(size_x * scaleX, size_y * scaleY));
        return body;
    }

    private Body generateDynamicBody(Rectangle rectangle, Float scaleX, Float scaleY) {
        return generateDynamicBody(
            rectangle.x, rectangle.y,
            rectangle.getWidth(), rectangle.getHeight(),
            scaleX, scaleY
        );
    }

    private Body generateDynamicBody(Float x, Float y, Float size_x, Float size_y) {
        return generateDynamicBody(x, y, size_x, size_y, 1f, 1f);
    }

    private Body generateDynamicBody(Float x, Float y, Float size_x, Float size_y, Float scaleX, Float scaleY) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.fixedRotation = true;
        // позиція — ЦЕНТР фікстури!
        bodyDef.position.set(x + size_x / 2f, y + size_y / 2f);
        bodyDef.bullet = true;

        Body body = currentGameLevel.getWorld().createBody(bodyDef);
        body.createFixture(generateBasicFicture(size_x * scaleX, size_y * scaleY));
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

    private Sprite generateBasicSprite(Rectangle rectangle, TextureRegion region) {
        Sprite sprite = new Sprite(region);
        sprite.setPosition(rectangle.getX(), rectangle.getY());
        sprite.setSize(rectangle.getWidth(), rectangle.getHeight());
        return sprite;
    }


}

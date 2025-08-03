package com.banew.factories;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.banew.containers.game.CursorsContainer;
import com.banew.containers.game.GameLevel;
import com.banew.entities.LevelsDoor;
import com.banew.entities.SpriteEntity;
import com.banew.entities.Torch;
import com.banew.entities.alive.MainHeroEntity;
import com.banew.entities.alive.Zombie;
import com.banew.entities.containers.Chest;
import com.banew.external.GeneralSettings;
import com.banew.items.StupidItem;
import com.banew.items.weapon.Sword;
import com.banew.other.dto.AliveEntityInfo;
import com.banew.other.enums.Race;
import com.banew.other.records.MovingEntityTexturesPerDirectionPack;
import com.banew.utilites.TextureExtractorDeep;
import lombok.Setter;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;

import static com.banew.other.records.MovingEntityTexturesPerDirectionPack.fromOneSubtexture;

public class EntityFactory {
    private final TextureAtlas textureAtlas;
    private final Map<String, TextureRegion[][]> cashedRegions;
    private final CursorsContainer cursorsContainer;
    @Setter
    private GameLevel currentGameLevel;

    public EntityFactory(GeneralSettings generalSettings, TextureAtlas textureAtlas) {
        this.textureAtlas = textureAtlas;
        cashedRegions = new HashMap<>();
        cursorsContainer = new CursorsContainer();
    }

    private final Map<String, Function<MapObject, SpriteEntity>> resolver = Map.of(
        "torch", this::createTorch,
        "main_hero", this::createMainHeroEntity,
        "door", this::createLevelsDoor,
        "zombie", this::createZombie,
        "chest", this::createChest
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

    private Chest createChest(MapObject object) {
        Rectangle rectangle = GameLevel.fromMapObject(object);

        List<TextureRegion> chests = TextureExtractorDeep.fromOneSubtexture(
            "Objects/Chest", 5, 2, textureAtlas, 1, 3
        );

        return new Chest(
            generateBasicSprite(rectangle, chests.get(0)),
            generateBasicBody(rectangle, .7f, .5f),
            new Vector2(.7f, .5f),
            cursorsContainer.getCursorPair("chest", "bad_chest"),
            chests
        );
    }

    public MainHeroEntity createMainHeroEntity(MapObject object) {
        Rectangle rectangle = GameLevel.fromMapObject(object);

        Map<String, MovingEntityTexturesPerDirectionPack> textures = Race.JEW.getTextures();

        MainHeroEntity mainHeroEntity = new MainHeroEntity(
            generateBasicSprite(rectangle, textures.get("down").waitingTexture()),
            generateDynamicBody(rectangle, textures.get("down").scaleTexture().x, textures.get("down").scaleTexture().y),
            textures
        );
        mainHeroEntity.getInventory().put(
            3, new StupidItem(textureAtlas.findRegion("hryak1/tile002"), "Хрюкающий подсвинок")
        );

        mainHeroEntity.getInventory().put(
            4, new Sword(
                TextureExtractorDeep.fromOneSubtexture(
                    "Objects/swords", 5, 1, textureAtlas, 1
                ).get(0),
                "клинок Аллаха"
            )
        );

        return mainHeroEntity;
    }

    public Zombie createZombie(MapObject mapObject) {
        Rectangle rectangle = GameLevel.fromMapObject(mapObject);

//        Map<String, MovingEntityTexturesPerDirectionPack> textures = Map.of(
//            "left", fromOneSubtexture(
//                "Characters/zombie_n_skeleton2", 9, 4, textureAtlas,
//                11, new Vector2(.7f, .7f), 10, 11, 12
//            ),
//            "up", fromOneSubtexture(
//                "Characters/zombie_n_skeleton2", 9, 4, textureAtlas,
//                29, new Vector2(.7f, .7f), 28, 29, 30
//            ),
//            "right", fromOneSubtexture(
//                "Characters/zombie_n_skeleton2", 9, 4, textureAtlas,
//                20, new Vector2(.7f, .7f), 19, 20, 21
//            ),
//            "down", fromOneSubtexture(
//                "Characters/zombie_n_skeleton2", 9, 4, textureAtlas,
//                2, new Vector2(.7f, .7f), 1, 2, 3
//            )
//        );

        Map<String, MovingEntityTexturesPerDirectionPack> textures = Map.of(
            "down", fromOneSubtexture(
                "Characters/64X128_Walking_Free", 10, 4, textureAtlas,
                1, new Vector2(.7f, .7f), 1, 2, 3, 4, 5, 6, 7, 8, 9, 10
            ),
            "left", fromOneSubtexture(
                "Characters/64X128_Walking_Free", 10, 4, textureAtlas,
                11, new Vector2(.7f, .7f), 11, 12, 13, 14, 15, 16, 17, 18, 19, 20
            ),
            "right", fromOneSubtexture(
                "Characters/64X128_Walking_Free", 10, 4, textureAtlas,
                21, new Vector2(.7f, .7f), 21, 22, 23, 24, 25, 26, 27, 28, 29, 30
            ),
            "up", fromOneSubtexture(
                "Characters/64X128_Walking_Free", 10, 4, textureAtlas,
                31, new Vector2(.7f, .7f), 31, 32, 33, 34, 35, 36, 37, 38, 39, 40
            )
        );

        AliveEntityInfo aliveEntityInfo = AliveEntityInfo.builder()
            .attackDistance(.2f)
            .maxHp(100)
            .health(100)
            .stamina(100)
            .maxStamina(100)
            .build();

        return new Zombie(
            generateBasicSprite(rectangle, textures.get("down").waitingTexture()),
            generateDynamicBody(rectangle, textures.get("down").scaleTexture().x, textures.get("down").scaleTexture().y),
            textures, cursorsContainer.getCursorPair("sword", "bad_sword"), aliveEntityInfo
        );
    }

    public LevelsDoor createLevelsDoor(MapObject mapObject) {
        String from = mapObject.getProperties().get("from", String.class);
        String to = mapObject.getProperties().get("to", String.class);
        String name = mapObject.getProperties().get("singleName", String.class);

        if (from == null || to == null || name == null) {
            throw new RuntimeException("Для дверей " + mapObject.getName() + " не було вказано поля 'from' або 'to'");
        }

        Rectangle rectangle = GameLevel.fromMapObject(mapObject);

        Sprite invisibleSprite = new Sprite();
        invisibleSprite.setPosition(rectangle.getX(), rectangle.getY());
        invisibleSprite.setSize(rectangle.getWidth(), rectangle.getHeight());
        invisibleSprite.setTexture(generateInvisibleTexture());

        return new LevelsDoor(
            invisibleSprite,
            from, to, name
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

        Body body = currentGameLevel.createBody(bodyDef);
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

        Body body = currentGameLevel.createBody(bodyDef);
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

    private Texture generateInvisibleTexture() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA4444);
        pixmap.setColor(new Color(1, 1, 1, 0f));
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }
}

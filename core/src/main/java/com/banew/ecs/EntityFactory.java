package com.banew.ecs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Engine;
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
import com.banew.ecs.components.*;
import com.banew.external.GeneralSettings;
import com.banew.items.StupidItem;
import com.banew.items.weapon.Sword;
import com.banew.other.dto.AliveEntityInfo;
import com.banew.other.enums.Race;
import com.banew.other.records.MovingEntityTexturesPerDirectionPack;
import com.banew.utilites.TextureExtractorDeep;
import lombok.Setter;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class EntityFactory {
    @Setter
    private Engine engine;
    private final TextureAtlas textureAtlas;
    private final Map<String, TextureRegion[][]> cashedRegions;
    private final CursorsContainer cursorsContainer;
    @Setter
    private GameLevel currentGameLevel;

    public EntityFactory(Engine engine,
                         GeneralSettings generalSettings,
                         TextureAtlas textureAtlas) {
        this.engine = engine;
        this.textureAtlas = textureAtlas;
        cashedRegions = new HashMap<>();
        cursorsContainer = new CursorsContainer();
    }

    private final Map<String, Function<MapObject, Entity>> resolver = Map.of(
        "torch", this::createTorch,
        "main_hero", this::createMainHeroEntity,
        "door", this::createLevelsDoor,
        "zombie", this::createZombie,
        "chest", this::createChest
    );

    public Set<Entity> resolveMapObjects(MapObjects objects) {
        Set<Entity> result = new HashSet<>();

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

    public Entity createTorch(MapObject object) {
        Rectangle rectangle = GameLevel.fromMapObject(object);

        TextureRegion waiting = new TextureExtractorDeep(
            "Characters/Torch Animated",
            4, 2, new Point(1, 1)
        ).extractRegions(textureAtlas);

        List<TextureRegion> animation = TextureExtractorDeep.fromOneSubtexture(
            "Characters/Torch Animated",
            4, 2, textureAtlas, 1, 2, 3, 4, 5, 6, 7, 8
        );

        var e = new Entity();

        var sc = new SpriteComponent();
        sc.sprite = generateBasicSprite(rectangle, waiting);
        sc.body = generateBasicBody(rectangle, .15f, .35f);
        sc.currentScales = new Vector2(.15f, .35f);
        e.add(sc);

        var ac = new AnimatedComponent();
        ac.init(waiting, 0f, List.of(animation));
        e.add(ac);

        return e;
    }

    private Entity createChest(MapObject object) {
        Rectangle rectangle = GameLevel.fromMapObject(object);

        List<TextureRegion> chests = TextureExtractorDeep.fromOneSubtexture(
            "Objects/Chest", 5, 2, textureAtlas, 1, 3
        );

        Entity e = new Entity();

        var sc = new SpriteComponent();
        sc.sprite = generateBasicSprite(rectangle, chests.get(0));
        sc.body = generateBasicBody(rectangle, .7f, .5f);
        sc.currentScales = new Vector2(.7f, .5f);
        e.add(sc);

        var ic = new InteractableComponent();
        ic.init("Скриня", cursorsContainer.getCursorPair("chest", "bad_chest"));
        e.add(ic);

        var invC = new InventoryComponent();
        invC.init(12);
        e.add(invC);

        var cc = new ChestComponent();
        cc.init(chests);
        e.add(cc);

        return e;
    }

    public Entity createMainHeroEntity(MapObject object) {
        Rectangle rectangle = GameLevel.fromMapObject(object);

        Map<String, MovingEntityTexturesPerDirectionPack> textures = Race.DN.getTextures();

        Entity e = new Entity();

        var sc = new SpriteComponent();
        sc.sprite = generateBasicSprite(rectangle, textures.get("down").waitingTexture());
        sc.body = generateDynamicBody(rectangle, textures.get("down").scaleTexture().x, textures.get("down").scaleTexture().y);
        sc.currentScales = textures.get("down").scaleTexture();
        e.add(sc);

        var mc = new MovingComponent();
        List<String> directions = List.of("up", "left", "down", "right");
        directions.forEach(direction -> {
            MovingEntityTexturesPerDirectionPack pack = textures.get(direction);
            mc.waitingRegions.add(pack.waitingTexture());
            mc.animationList.add(new com.badlogic.gdx.graphics.g2d.Animation<>(
                1f / pack.animation().size(), pack.animation().toArray(new TextureRegion[0])
            ));
            mc.animationsScales.add(pack.scaleTexture());
        });
        e.add(mc);

        var alive = new AliveParamsComponent();
        AliveEntityInfo info = new com.banew.other.dto.PlayerInfo();
        alive.info = info;
        alive.getReloadHpTime = 3.33f;
        alive.getReloadHpSpeed = 20f;
        alive.reloadStaminaTime = 3f;
        alive.getReloadStaminaSpeed = 10f;
        e.add(alive);

        var invC = new InventoryComponent();
        invC.init(0);
        invC.container.put(3, new StupidItem(textureAtlas.findRegion("hryak1/tile002"), "Хрюкающий подсвинок"));
        invC.container.put(4, new Sword(
            TextureExtractorDeep.fromOneSubtexture("Objects/swords", 5, 1, textureAtlas, 1).get(0),
            "клинок Аллаха"
        ));
        e.add(invC);

        var heroComp = new MainHeroComponent();
        heroComp.playerInfo = (com.banew.other.dto.PlayerInfo) info;
        e.add(heroComp);

        return e;
    }

    public Entity createZombie(MapObject mapObject) {
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

        AliveEntityInfo aliveEntityInfo = AliveEntityInfo.builder()
            .attackDistance(.2f)
            .maxHp(100)
            .health(100)
            .stamina(100)
            .maxStamina(100)
            .build();

        Entity e = new Entity();

        var sc = new SpriteComponent();
        sc.sprite = generateBasicSprite(rectangle, textures.get("down").waitingTexture());
        sc.body = generateDynamicBody(rectangle, textures.get("down").scaleTexture().x, textures.get("down").scaleTexture().y);
        sc.currentScales = textures.get("down").scaleTexture();
        e.add(sc);

        var mc = new MovingComponent();
        List<String> directions = List.of("up", "left", "down", "right");
        directions.forEach(direction -> {
            MovingEntityTexturesPerDirectionPack pack = textures.get(direction);
            mc.waitingRegions.add(pack.waitingTexture());
            mc.animationList.add(new com.badlogic.gdx.graphics.g2d.Animation<>(
                1f / pack.animation().size(), pack.animation().toArray(new TextureRegion[0])
            ));
            mc.animationsScales.add(pack.scaleTexture());
        });
        e.add(mc);

        var alive = new AliveParamsComponent();
        alive.info = aliveEntityInfo;
        alive.getReloadHpTime = 5f;
        alive.getReloadHpSpeed = 30f;
        alive.reloadStaminaTime = 3f;
        alive.getReloadStaminaSpeed = 10f;
        alive.attackCursor = cursorsContainer.getCursorPair("sword", "bad_sword");
        e.add(alive);

        var zc = new ZombieComponent();
        e.add(zc);

        return e;
    }

    public Entity createLevelsDoor(MapObject mapObject) {
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

        Entity e = new Entity();

        var sc = new SpriteComponent();
        sc.sprite = invisibleSprite;
        sc.body = null;
        sc.currentScales = new Vector2(1, 1);
        e.add(sc);

        var ldc = new LevelsDoorComponent();
        ldc.levelFrom = from;
        ldc.levelTo = to;
        ldc.singleName = name;
        e.add(ldc);

        return e;
    }

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

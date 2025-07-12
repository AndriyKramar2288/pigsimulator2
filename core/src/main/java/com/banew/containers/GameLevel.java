package com.banew.containers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Disposable;
import com.banew.containers.lightModes.DayNightLightMode;
import com.banew.containers.lightModes.LightMode;
import com.banew.containers.lightModes.OblivionLightMode;
import com.banew.entities.LevelsDoor;
import com.banew.entities.MainHeroEntity;
import com.banew.entities.SpriteEntity;
import com.banew.external.InitialGameLevel;
import com.banew.factories.EntityFactory;
import com.banew.other.records.GameContext;
import com.banew.utilites.WalkingAreaResolver;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Інкапсулює усі дані про ігровий рівень, ще вже створено
 * @author Banew_
 * */
public class GameLevel implements Disposable {
    @Getter
    private final Set<SpriteEntity> entitySet;
    @Getter
    private MainHeroEntity mainHeroEntity;
    @Getter
    private final TiledMap map;
    private final OrthoCachedTiledMapRenderer renderer;
    @Getter
    private final Set<Rectangle> collisions = new HashSet<>();
    public static final float unitScaleMap = 32f;
    @Getter
    private final World world;
    @Getter
    private final String levelName;
    @Getter
    private final LightMode lightMode;
    private final Set<MusicPattern> musicSet;
    private final WalkingAreaResolver walkingAreaResolver;

    public GameLevel(InitialGameLevel initLevel, EntityFactory factory, Map<String, MusicPattern> musicMap) {
        levelName = initLevel.getLevelName();
        world = new World(
            new Vector2(0, 0),
            false
        );

        musicSet = musicMap.entrySet().stream()
            .filter(e -> initLevel.getMusicPatterns().contains(e.getKey()))
            .map(Map.Entry::getValue)
            .collect(Collectors.toSet());

        entitySet = new TreeSet<>(Comparator.comparingInt(SpriteEntity::getPriority)
            .thenComparingInt(Object::hashCode));

        TmxMapLoader.Parameters params = new TmxMapLoader.Parameters();
        params.textureMinFilter = Texture.TextureFilter.Nearest;
        params.textureMagFilter = Texture.TextureFilter.Nearest;
        params.generateMipMaps = false;
        params.convertObjectToTileSpace = false;

        map = new TmxMapLoader().load(initLevel.getMapName(), params);
        renderer = new OrthoCachedTiledMapRenderer(map, 1f / unitScaleMap);
        renderer.setBlending(true);

        loadCollisions();

        factory.setCurrentGameLevel(this);
        entitySet.addAll(factory.resolveMapObjects(map.getLayers().get("Objects").getObjects()));

        lightMode = switch (initLevel.getLightMode()) {
            case "oblivion" -> new OblivionLightMode(this);
            default -> new DayNightLightMode(this);
        };

        walkingAreaResolver = new WalkingAreaResolver(
            map.getLayers().get("walkAreas"), initLevel.getDefaultWalkSound()
        );
    }

    public String getCurrentWalkSound() {
        if (mainHeroEntity != null) {
            return walkingAreaResolver.getCurrentAreaSound(mainHeroEntity.getCenterCoordinates());
        }
        else throw new RuntimeException("Вказаний рівень не активний! " + levelName);
    }

    public void renderMap(OrthographicCamera camera) {
        renderer.setView(camera);
        renderer.render();
        musicSet.forEach(MusicPattern::render);
    }

    public LevelsDoor getDoorByName(String name) {
        return (LevelsDoor) entitySet.stream()
            .filter(e -> e instanceof LevelsDoor)
            .filter(e -> ((LevelsDoor) e).getSingleName().equals(name))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Нема двері на рівні " + getLevelName() + " з назвою " + name));
    }

    public void switchTo(MainHeroEntity mainHeroEntity, Vector2 newPosition, GameContext context) {
        // присвоєння
        context.currentLevel().getEntitySet().remove(mainHeroEntity);
        entitySet.add(mainHeroEntity);
        this.mainHeroEntity = mainHeroEntity;
        context.currentLevelRef().setElement(this);

        // оновити тіло / перемістити спрайт
        mainHeroEntity.setBody(
            replaceBody(mainHeroEntity.getBody(), mainHeroEntity.generateFixtureDef(), newPosition)
        );
        mainHeroEntity.setSpritePosition(newPosition);

        // зупинити музику, яку треба зупинити
        context.levels().stream().filter(l -> l != this).forEach(l -> {
            l.musicSet.stream()
                .filter(p -> musicSet.stream().allMatch(my_p -> my_p != p))
                .forEach(MusicPattern::stopPlay);
        });
        // перемкнути світло
        lightMode.switchTo();
    }

    /**
     * Переміщує Body у world поточного ігрового рівня, перемістивши при цьому тіло на нову позицію
     * @param oldBody старе тіло
     * @param newFixture нова форма
     * @param newPosition нова позиція
     * @return нове тіло (на основі старого)
     */
    private Body replaceBody(Body oldBody, FixtureDef newFixture, Vector2 newPosition) {
        // Створюємо нове тіло в іншому світі:
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = oldBody.getType();
        bodyDef.position.set(newPosition);
        bodyDef.angle = oldBody.getAngle();
        bodyDef.linearVelocity.set(oldBody.getLinearVelocity());
        bodyDef.angularVelocity = oldBody.getAngularVelocity();
        bodyDef.fixedRotation = oldBody.isFixedRotation();
        bodyDef.bullet = oldBody.isBullet();
        bodyDef.gravityScale = oldBody.getGravityScale();

        Body newBody = world.createBody(bodyDef);
        newBody.createFixture(newFixture);

        // Переносимо юзер дату
        newBody.setUserData(oldBody.getUserData());

        // ВИДАЛЯЄМО старе тіло зі старого світу (якщо потрібно)
        oldBody.getWorld().destroyBody(oldBody);
        return newBody;
    }

    private void loadCollisions() {
        MapLayer layer = getMap().getLayers().get("Колізіонєри");
        layer.getObjects().forEach(object -> {
            Rectangle e = fromMapObject(object);

            collisions.add(e);

            BodyDef def = new BodyDef();
            def.type = BodyDef.BodyType.StaticBody;
            def.position.x = e.getX() + e.getWidth() / 2;
            def.position.y = e.getY() + e.getHeight() / 2;

            Body body = world.createBody(def);

            FixtureDef fDef = new FixtureDef();
            PolygonShape shape = new PolygonShape();
            shape.setAsBox(e.getWidth() / 2, e.getHeight() / 2);
            fDef.shape = shape;
            fDef.density = 100f;
            fDef.friction = 0.5f;
            if ("wall".equals(object.getProperties().get("Class", String.class))) {
                fDef.filter.categoryBits = 0x0002;
            }
            body.createFixture(fDef);
        });
    }

    public static Rectangle fromMapObject(MapObject obj) {
        final float PPU = GameLevel.unitScaleMap;
        Rectangle rectCollisionPixels = ((RectangleMapObject) obj).getRectangle();

        // Нормалізуємо
        return new Rectangle(
            rectCollisionPixels.x / PPU,
            rectCollisionPixels.y / PPU,
            rectCollisionPixels.width / PPU,
            rectCollisionPixels.height / PPU
        );
    }

    @Override
    public void dispose() {
        world.dispose();
        lightMode.dispose();
        renderer.dispose();
    }
}

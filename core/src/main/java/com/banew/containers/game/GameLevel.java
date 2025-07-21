package com.banew.containers.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Disposable;
import com.banew.containers.game.lightModes.DayNightLightMode;
import com.banew.containers.game.lightModes.LightMode;
import com.banew.containers.game.lightModes.OblivionLightMode;
import com.banew.entities.LevelsDoor;
import com.banew.entities.MovingEntity;
import com.banew.entities.SpriteEntity;
import com.banew.entities.alive.AliveEntity;
import com.banew.entities.alive.MainHeroEntity;
import com.banew.external.GeneralSettings;
import com.banew.external.InitialGameLevel;
import com.banew.factories.EntityFactory;
import com.banew.other.records.GameContext;
import com.banew.utilites.WalkingAreaResolver;
import com.banew.utilites.path_search.PathFinder;
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
    private MainHeroEntity mainHeroEntity;
    @Getter
    private final TiledMap map;
    private final OrthoCachedTiledMapRenderer renderer;
    private final Set<Rectangle> collisions = new HashSet<>();
    public static final float unitScaleMap = 32f;
    private final World world;
    @Getter
    private final String levelName;
    private final LightMode lightMode;
    private final Set<MusicPattern> musicSet;
    private final WalkingAreaResolver walkingAreaResolver;
    private final PathFinder pathFinder;

    public GameLevel(InitialGameLevel initLevel, EntityFactory factory, Map<String, MusicPattern> musicMap, GeneralSettings generalSettings) {
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

        loadCollisions(generalSettings.getCollision_level_name());

        factory.setCurrentGameLevel(this);
        entitySet.addAll(factory.resolveMapObjects(map.getLayers().get("Objects").getObjects()));

        lightMode = switch (initLevel.getLightMode()) {
            case "oblivion" -> new OblivionLightMode(this, world);
            default -> new DayNightLightMode(this, world);
        };

        walkingAreaResolver = new WalkingAreaResolver(
            map.getLayers().get("walkAreas"), initLevel.getDefaultWalkSound()
        );

        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(0);
        float realWidth = (layer.getWidth() * layer.getTileWidth()) / unitScaleMap;
        float realHeight = (layer.getHeight() * layer.getTileHeight()) / unitScaleMap;
        pathFinder = new PathFinder(collisions, (int) realWidth, (int) realHeight);
    }

    public List<Vector2> findPath(Vector2 start, Vector2 end) {
        return pathFinder.findPath(start, end);
    }

    public Optional<AliveEntity> getFocusEntity(GameContext gameContext) {
        return entitySet.stream()
            .filter(e -> e instanceof AliveEntity && !(e instanceof MainHeroEntity))
            .filter(e -> e.cursorTouchDown(gameContext))
            .map(e -> (AliveEntity) e)
            .findFirst();
    }

    public void killAliveEntity(AliveEntity victim) {
        world.destroyBody(victim.getBody());
        entitySet.remove(victim);
    }

    public String getCurrentWalkSound() {
        if (mainHeroEntity != null) {
            return walkingAreaResolver.getCurrentAreaSound(mainHeroEntity.getCenterCoordinates());
        }
        else throw new RuntimeException("Вказаний рівень не активний! " + levelName);
    }

    public void render(GameContext context, SpriteBatch batch) {
        renderer.setView(context.camera());
        renderer.render();
        musicSet.forEach(MusicPattern::render);

        batch.begin();
        new HashSet<>(entitySet).forEach(e -> {
            e.draw(batch);
            e.render(context);
        });
        batch.end();

        lightMode.render(context);
    }

    public LevelsDoor getDoorByName(String name) {
        return (LevelsDoor) entitySet.stream()
            .filter(e -> e instanceof LevelsDoor)
            .filter(e -> ((LevelsDoor) e).getSingleName().equals(name))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Нема двері на рівні " + getLevelName() + " з назвою " + name));
    }

    public void stealEntity(GameLevel sourceLevel, MovingEntity entity, Vector2 newPosition) {
        sourceLevel.entitySet.remove(entity);
        entitySet.add(entity);

        // оновити тіло / перемістити спрайт
        entity.setBody(
            replaceBody(entity.getBody(), entity.generateFixtureDef(), newPosition)
        );
        entity.setSpritePosition(newPosition);
    }

    public void switchTo(MainHeroEntity mainHeroEntity, Vector2 newPosition, GameContext context) {
        // присвоєння
        stealEntity(context.currentLevel(), mainHeroEntity, newPosition);
        this.mainHeroEntity = mainHeroEntity;
        context.currentLevelRef().setElement(this);

        // зупинити музику, яку треба зупинити
        context.levels().stream().filter(l -> l != this).forEach(l -> {
            l.musicSet.stream()
                .filter(p -> musicSet.stream().allMatch(my_p -> my_p != p))
                .forEach(MusicPattern::stopPlay);
        });
        // перемкнути світло
        lightMode.switchTo(context);
        context.effectAnimationsContainer().clear();
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

    public void drawCollisions(SpriteBatch spriteBatch, Texture texture) {
        spriteBatch.begin();
        collisions.forEach(r -> {
            spriteBatch.draw(texture, r.x, r.y, r.width, r.height);
        });
        entitySet.forEach(e -> e.getCollisionSprite(texture).draw(spriteBatch));
        spriteBatch.end();
    }

    public void step(GameContext context) {
        world.step(Gdx.graphics.getDeltaTime(), 1, 1);
        lightMode.step(Gdx.graphics.getDeltaTime());
        new HashSet<>(entitySet).forEach(
            entity -> entity.step(context, this)
        );
    }

    public float getBrightness() {
        return lightMode.getBrightness();
    }

    public String getGuiWatchText() {
        return lightMode.getGuiWatchText();
    }

    public Body createBody(BodyDef bodyDef) {
        return world.createBody(bodyDef);
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

    private void loadCollisions(String collisionLevelName) {
        MapLayer layer = getMap().getLayers().get(collisionLevelName);
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
}

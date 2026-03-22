package com.banew.containers.game;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
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
import com.banew.ecs.EntityFactory;
import com.banew.ecs.components.AliveParamsComponent;
import com.banew.ecs.components.LevelsDoorComponent;
import com.banew.ecs.components.MainHeroComponent;
import com.banew.ecs.components.SpriteComponent;
import com.banew.ecs.systems.*;
import com.banew.external.GeneralSettings;
import com.banew.external.InitialGameLevel;
import com.banew.other.records.GameContext;
import com.banew.utilites.WalkingAreaResolver;
import com.banew.utilites.path_search.PathFinder;
import lombok.Getter;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class GameLevel implements Disposable {
    @Getter
    private final Engine engine; // ЗВИЧАЙНИЙ ENGINE!
    private Entity mainHeroEntity;

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

    private DrawSystem drawSystem;

    public GameLevel(InitialGameLevel initLevel, EntityFactory factory, Map<String, MusicPattern> musicMap, GeneralSettings generalSettings) {
        levelName = initLevel.getLevelName();
        world = new World(new Vector2(0, 0), false);
        engine = new Engine();

        musicSet = musicMap.entrySet().stream()
            .filter(e -> initLevel.getMusicPatterns().contains(e.getKey()))
            .map(Map.Entry::getValue)
            .collect(Collectors.toSet());

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
        factory.setEngine(engine);

        for (Entity e : factory.resolveMapObjects(map.getLayers().get("Objects").getObjects())) {
            engine.addEntity(e);
        }

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

    public void initSystems(GameContext context, SpriteBatch batch) {
        engine.addSystem(new StepSystem());
        engine.addSystem(new MovingSystem());
        engine.addSystem(new AnimatedSystem());
        engine.addSystem(new AliveSystem(context.globalGameContext().getCursorsContainer().getCursorPair("sword", "bad_sword"), context));
        engine.addSystem(new ZombieAISystem(context));
        engine.addSystem(new InteractionSystem(context));
        engine.addSystem(new LevelsDoorSystem(context, this));

        drawSystem = new DrawSystem(batch);
        engine.addSystem(drawSystem);

        setActive(false);
    }

    public void setActive(boolean isActive) {
        if (drawSystem != null) {
            drawSystem.setProcessing(isActive);
        }
    }

    public List<Vector2> findPath(Vector2 start, Vector2 end) {
        return pathFinder.findPath(start, end);
    }

    public void killAliveEntity(Entity victim) {
        world.destroyBody(victim.getComponent(SpriteComponent.class).body);
        engine.removeEntity(victim);
    }

    public String getCurrentWalkSound() {
        if (mainHeroEntity != null) {
            return walkingAreaResolver.getCurrentAreaSound(mainHeroEntity.getComponent(SpriteComponent.class).getCenterCoordinates());
        }
        else throw new RuntimeException("Вказаний рівень не активний! " + levelName);
    }

    public Optional<Entity> getFocusEntity(GameContext gameContext) {
        for (Entity e : engine.getEntitiesFor(com.badlogic.ashley.core.Family.all(AliveParamsComponent.class, SpriteComponent.class).exclude(MainHeroComponent.class).get())) {
            var sc = e.getComponent(SpriteComponent.class);
            if (sc.cursorTouchDown(gameContext)) {
                return Optional.of(e);
            }
        }
        return Optional.empty();
    }

    public void renderVisuals(GameContext context) {
        renderer.setView(context.camera());
        renderer.render();
        musicSet.forEach(MusicPattern::render);
        lightMode.render(context);
    }

    public Entity getDoorByName(String name) {
        for (Entity e : engine.getEntitiesFor(com.badlogic.ashley.core.Family.all(LevelsDoorComponent.class).get())) {
            if (e.getComponent(LevelsDoorComponent.class).singleName.equals(name)) return e;
        }
        throw new RuntimeException("Нема двері на рівні " + getLevelName() + " з назвою " + name);
    }

    public void stealEntity(GameLevel sourceLevel, Entity entity, Vector2 newPosition) {
        sourceLevel.getEngine().removeEntity(entity); // Зі звичайним Engine це БЕЗПЕЧНО!
        engine.addEntity(entity);

        var sc = entity.getComponent(SpriteComponent.class);
        sc.body = replaceBody(sc.body, generateFixtureDefForEntity(sc), newPosition);
        sc.sprite.setPosition(newPosition.x, newPosition.y);
    }

    public void switchTo(Entity mainHeroEntity, Vector2 newPosition, GameContext context) {
        stealEntity(context.currentLevel(), mainHeroEntity, newPosition);
        this.mainHeroEntity = mainHeroEntity;

        context.currentLevel().setActive(false);
        this.setActive(true);

        context.currentLevelRef().setElement(this);

        context.levels().stream().filter(l -> l != this).forEach(l -> {
            l.musicSet.stream()
                .filter(p -> musicSet.stream().allMatch(my_p -> my_p != p))
                .forEach(MusicPattern::stopPlay);
        });

        lightMode.switchTo(context);
        context.effectAnimationsContainer().clear();
    }

    public void step(float deltaTime) {
        world.step(deltaTime, 6, 2);
        lightMode.step(deltaTime);
        engine.update(deltaTime);
    }

    private FixtureDef generateFixtureDefForEntity(SpriteComponent sc) {
        FixtureDef def = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(
            (sc.sprite.getWidth() / 2f) * sc.currentScales.x,
            (sc.sprite.getHeight() / 2f) * sc.currentScales.y
        );
        def.shape = shape;
        def.density = 1f;
        def.friction = 0.5f;
        return def;
    }

    public static Rectangle fromMapObject(MapObject obj) {
        final float PPU = GameLevel.unitScaleMap;
        Rectangle rectCollisionPixels = ((RectangleMapObject) obj).getRectangle();
        return new Rectangle(
            rectCollisionPixels.x / PPU, rectCollisionPixels.y / PPU,
            rectCollisionPixels.width / PPU, rectCollisionPixels.height / PPU
        );
    }

    @Override
    public void dispose() {
        musicSet.forEach(MusicPattern::stopPlay);
        world.dispose();
        lightMode.dispose();
        renderer.dispose();
    }

    public void drawCollisions(SpriteBatch spriteBatch, Texture texture) {
        spriteBatch.begin();
        collisions.forEach(r -> {
            spriteBatch.draw(texture, r.x, r.y, r.width, r.height);
        });
        for (Entity e : engine.getEntitiesFor(com.badlogic.ashley.core.Family.all(SpriteComponent.class).get())) {
            e.getComponent(SpriteComponent.class).getCollisionSprite(texture).draw(spriteBatch);
        }
        spriteBatch.end();
    }

    public float getBrightness() { return lightMode.getBrightness(); }
    public String getGuiWatchText() { return lightMode.getGuiWatchText(); }
    public Body createBody(BodyDef bodyDef) { return world.createBody(bodyDef); }

    private Body replaceBody(Body oldBody, FixtureDef newFixture, Vector2 newPosition) {
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
        newBody.setUserData(oldBody.getUserData());
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

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
import com.banew.entities.MainHeroEntity;
import com.banew.entities.SpriteEntity;
import com.banew.external.GeneralSettings;
import com.banew.external.InitialGameLevel;
import com.banew.factories.EntityFactory;
import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

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
    private Set<Rectangle> collisions;
    public static final float unitScaleMap = 32f;
    @Getter
    private final World world;
    @Getter
    private final String levelName;

    public void renderMap(OrthographicCamera camera) {
        renderer.setView(camera);
        renderer.render();
    }

    private final InitialGameLevel initLevel;

    public void setMainHeroEntity(MainHeroEntity mainHeroEntity) {
        entitySet.add(mainHeroEntity);
        mainHeroEntity.setBody(
            replaceBody(mainHeroEntity.getBody(), mainHeroEntity.generateFixtureDef())
        );

        this.mainHeroEntity = mainHeroEntity;
    }

    private Body replaceBody(Body oldBody, FixtureDef newFixture) {
        // Створюємо нове тіло в іншому світі:
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(oldBody.getPosition());
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

    public GameLevel(InitialGameLevel initLevel, GeneralSettings generalSettings) {
        levelName = initLevel.getLevelName();
        this.initLevel = initLevel;
        world = new World(
            new Vector2(0, 0),
            false
        );

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
    }

    public void loadEntites(GeneralSettings generalSettings, EntityFactory factory) {
        factory.setCurrentGameLevel(this);
        entitySet.addAll(initLevel.getEntities(factory));
    }

    private void loadCollisions() {
        collisions = generateCollisions("Колізіонєри");

        collisions.forEach(e -> {
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
            body.createFixture(fDef);
        });
    }

    public Set<Rectangle> generateCollisions(String collisionLayerName) {
        MapLayer layer = getMap().getLayers().get(collisionLayerName);

        Set<Rectangle> result = new HashSet<>();

        if (layer != null) {
            layer.getObjects().forEach(obj -> {

                Rectangle rectCollision = fromMapObject(obj);
                result.add(rectCollision);
            });
        }

        return result;
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
        renderer.dispose();
    }
}

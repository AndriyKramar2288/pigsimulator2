package com.banew.containers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.banew.entities.MainHeroEntity;
import com.banew.external.GeneralSettings;
import com.banew.factories.EntityFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EntityContainer implements Disposable {
    private final World world;
    private MainHeroEntity mainHeroEntity;
    private final EntityFactory entityFactory;
    private final OrthographicCamera camera;
    private GameLevel currentLevel;
    private boolean isMoving = false;
    private final String COLLISION_LAYER_NAME;

    public EntityContainer(Camera camera, GeneralSettings generalSettings) {

        this.camera = (OrthographicCamera) camera;
        this.COLLISION_LAYER_NAME = generalSettings.getCollision_level_name();

        world = new World(
            new Vector2(0, 0),
            false
        );
        this.entityFactory = new EntityFactory(generalSettings, world);

        this.currentLevel = generalSettings.getLevels(entityFactory).stream().toList().get(0);
        //initMainHero();

        mainHeroEntity = (MainHeroEntity) generalSettings.getMainHero().extractEntity(this.entityFactory);
        currentLevel.getEntitySet().add(mainHeroEntity);
        currentLevel.loadCollisions(world, COLLISION_LAYER_NAME);
    }

    public void render(SpriteBatch spriteBatch) {
        isMoving = false;

        world.step(Gdx.graphics.getDeltaTime(), 1, 1);

        movingRender();
        drawScene();
        drawVisibleEntities(spriteBatch);
    }

    private void drawScene() {
        if (currentLevel != null) {
            currentLevel.renderMap(camera);
        }
    }

    private Set<Rectangle> getCollisionObjects() {
        MapLayer mapCollisions = currentLevel.getMap().getLayers().get(COLLISION_LAYER_NAME);
        Set<Rectangle> result = new HashSet<>();

        if (mapCollisions != null) {
            mapCollisions.getObjects().forEach(obj -> {
                final float PPU = GameLevel.unitScaleMap;
                Rectangle rectCollisionPixels = ((RectangleMapObject) obj).getRectangle();

                // Нормалізуємо
                Rectangle rectCollision = new Rectangle(
                    rectCollisionPixels.x / PPU,
                    rectCollisionPixels.y / PPU,
                    rectCollisionPixels.width / PPU,
                    rectCollisionPixels.height / PPU
                );

                result.add(rectCollision);
            });
        }

        return result;
    }

    private void moveAllExceptMain(float x, float y) {
        mainHeroEntity.move(-x, -y);
    }

    private float computeStep() {
        float speed = .3f;
        return speed * Gdx.graphics.getDeltaTime();
    }

    private final Map<Integer, Runnable> keysMovementAction = Map.of(
        Input.Keys.W, () -> moveAllExceptMain(0, -computeStep()),
        Input.Keys.S, () -> moveAllExceptMain(0, computeStep()),
        Input.Keys.A, () -> moveAllExceptMain(computeStep(), 0),
        Input.Keys.D, () -> moveAllExceptMain(-computeStep(), 0)
    );

    private void movingRender() {
        mainHeroEntity.donotMove();

        keysMovementAction.forEach((key, value) -> {
            if (Gdx.input.isKeyPressed(key)) {
                value.run();
                isMoving = true;
            }
        });

        camera.position.lerp(new Vector3(mainHeroEntity.getCenterCoordinates(), 0f), .125f);
        camera.zoom = isMoving ? smoothZoom(1.05f) : smoothZoom(1f);

        camera.update();
    }

    private float smoothZoom(float targetZoom) {
        // Швидкість наближення (чим менше, тим плавніше)
        float zoomSpeed = 4.5f; // одиниці за секунду

        // Поточний зум → поступово тягнемо до цілі
        return camera.zoom + (targetZoom - camera.zoom) * zoomSpeed * Gdx.graphics.getDeltaTime();
    }

    private void drawVisibleEntities(SpriteBatch spriteBatch) {
        currentLevel.getEntitySet().forEach(e -> e.draw(spriteBatch));
    }

    @Override
    public void dispose() {
        currentLevel.dispose();
    }
}

package com.banew.containers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.banew.entities.MainHeroEntity;
import com.banew.entities.SpriteEntity;
import com.banew.external.GeneralSettings;
import com.banew.factories.EntityFactory;
import com.banew.other.records.MatrixVector;
import com.banew.other.records.MovingEntityTexturesPerDirectionPack;
import com.banew.utilites.TextureExtractorDeep;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EntityContainer implements Disposable {
    private Set<SpriteEntity> allEntities;
    private MainHeroEntity mainHeroEntity;
    private final EntityFactory entityFactory;
    private final OrthographicCamera camera;
    private GameLevel currentLevel;
    private boolean isMoving = false;
    private final String COLLISION_LAYER_NAME;

    public EntityContainer(EntityFactory entityFactory, Camera camera, GeneralSettings generalSettings) {
        this.entityFactory = entityFactory;
        this.camera = (OrthographicCamera) camera;
        this.allEntities = new HashSet<>();
        this.COLLISION_LAYER_NAME = generalSettings.getCollision_level_name();

        updateLevel(generalSettings.getLevels(entityFactory).stream().toList().get(0));
        initMainHero();
    }

    private void updateLevel(GameLevel currentLevel) {
        this.currentLevel = currentLevel;
        allEntities = currentLevel.getEntitySet();
    }

    private void initMainHero() {
        mainHeroEntity = entityFactory.createMainHeroEntity(
            5f, 5f,
            List.of(
                new MovingEntityTexturesPerDirectionPack(
                    new TextureExtractorDeep(
                        "Characters/Basic Charakter Spritesheet",
                        new MatrixVector(4, 4),
                        new MatrixVector(1, 2)
                    ),
                    TextureExtractorDeep.fromOneSubtexture(
                        "Characters/Basic Charakter Spritesheet",
                        new MatrixVector(4, 4),
                        new MatrixVector(1, 2),
                        new MatrixVector(3, 2),
                        new MatrixVector(4, 2)
                    )
                ),
                new MovingEntityTexturesPerDirectionPack(
                    new TextureExtractorDeep(
                        "Characters/Basic Charakter Spritesheet",
                        new MatrixVector(4, 4),
                        new MatrixVector(2, 3)
                    ),
                    TextureExtractorDeep.fromOneSubtexture(
                        "Characters/Basic Charakter Spritesheet",
                        new MatrixVector(4, 4),
                        new MatrixVector(1, 3),
                        new MatrixVector(3, 3),
                        new MatrixVector(4, 3)
                    )
                ),
                new MovingEntityTexturesPerDirectionPack(
                    new TextureExtractorDeep(
                        "Characters/Basic Charakter Spritesheet",
                        new MatrixVector(4, 4),
                        new MatrixVector(1, 1)
                    ),
                    TextureExtractorDeep.fromOneSubtexture(
                        "Characters/Basic Charakter Spritesheet",
                        new MatrixVector(4, 4),
                        new MatrixVector(1, 1),
                        new MatrixVector(3, 1),
                        new MatrixVector(4, 1)
                    )
                ),
                new MovingEntityTexturesPerDirectionPack(
                    new TextureExtractorDeep(
                        "Characters/Basic Charakter Spritesheet",
                        new MatrixVector(4, 4),
                        new MatrixVector(1, 4)
                    ),
                    TextureExtractorDeep.fromOneSubtexture(
                        "Characters/Basic Charakter Spritesheet",
                        new MatrixVector(4, 4),
                        new MatrixVector(1, 4),
                        new MatrixVector(3, 4),
                        new MatrixVector(4, 4)
                    )
                )
            )
        );
        mainHeroEntity.setSize(.4f, .4f);
        mainHeroEntity.setTextureScale(3.0f);

        allEntities.add(mainHeroEntity);
    }

    public void render(SpriteBatch spriteBatch) {
        isMoving = false;

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
        mainHeroEntity.move(-x, -y, getCollisionObjects());
    }

    private float computeStep() {
        float speed = 3f;
        return speed * Gdx.graphics.getDeltaTime();
    }

    private final Map<Integer, Runnable> keysMovementAction = Map.of(
        Input.Keys.W, () -> moveAllExceptMain(0, -computeStep()),
        Input.Keys.S, () -> moveAllExceptMain(0, computeStep()),
        Input.Keys.A, () -> moveAllExceptMain(computeStep(), 0),
        Input.Keys.D, () -> moveAllExceptMain(-computeStep(), 0)
    );

    private void movingRender() {
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
        allEntities.forEach(e -> e.draw(spriteBatch));
    }

    @Override
    public void dispose() {
        currentLevel.dispose();
    }
}

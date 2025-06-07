package com.banew.containers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.banew.entities.MainHeroEntity;
import com.banew.entities.MovingEntity;
import com.banew.entities.SpriteEntity;
import com.banew.factories.EntityFactory;
import com.banew.other.records.MatrixVector;
import com.banew.other.records.MovingEntityTexturesPerDirectionPack;
import com.banew.utilites.TextureExtractorClassic;
import com.banew.utilites.TextureExtractorDeep;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EntityContainer {
    private final Set<SpriteEntity> allEntities;
    private MainHeroEntity mainHeroEntity;
    private final EntityFactory entityFactory;
    private final SpriteBatch spriteBatch;
    private final OrthographicCamera camera;
    private MovingEntity moving_pig;

    private boolean isMoving = false;

    public EntityContainer(EntityFactory entityFactory, SpriteBatch spriteBatch, Camera camera) {
        this.entityFactory = entityFactory;
        this.spriteBatch = spriteBatch;
        this.camera = (OrthographicCamera) camera;
        allEntities = new HashSet<>();

        initMainHero();
        initOtherEntities();
    }

    private void initOtherEntities() {
        allEntities.add(entityFactory.createSimpleSprite(
            new TextureExtractorClassic("hryak1/tile005"),
            4f, 1f
        ));

        allEntities.add(entityFactory.createSimpleSprite(
            new TextureExtractorDeep(
                "Characters/Free Cow Sprites",
                new MatrixVector(3, 2),
                new MatrixVector(1, 1)
            ),
            -3f,
            -1f
        ));

        allEntities.add(entityFactory.createAnimatedEntity(
            -4f, 1f,
            new TextureExtractorClassic("hryak1/tile000"),
            .5f,
            List.of(
                new TextureExtractorClassic("hryak1/tile000"),
                new TextureExtractorClassic("hryak1/tile001"),
                new TextureExtractorClassic("hryak1/tile002")
            ),
            List.of(
                new TextureExtractorClassic("hryak1/tile004"),
                new TextureExtractorClassic("hryak1/tile005"),
                new TextureExtractorClassic("hryak1/tile006")
            )
        ));


        moving_pig = entityFactory.createMovingEntity(
            0f, -5f,
            List.of(
                new MovingEntityTexturesPerDirectionPack(
                    new TextureExtractorClassic("hryak2/pig002"),
                    Set.of(
                        new TextureExtractorClassic("hryak2/pig001"),
                        new TextureExtractorClassic("hryak2/pig001"),
                        new TextureExtractorClassic("hryak2/pig003")
                    )
                ),
                new MovingEntityTexturesPerDirectionPack(
                    new TextureExtractorClassic("hryak2/pig011"),
                    Set.of(
                        new TextureExtractorClassic("hryak2/pig010"),
                        new TextureExtractorClassic("hryak2/pig011"),
                        new TextureExtractorClassic("hryak2/pig012")
                    )
                ),
                new MovingEntityTexturesPerDirectionPack(
                    new TextureExtractorClassic("hryak2/pig008"),
                    Set.of(
                        new TextureExtractorClassic("hryak2/pig007"),
                        new TextureExtractorClassic("hryak2/pig008"),
                        new TextureExtractorClassic("hryak2/pig009")
                    )
                ),
                new MovingEntityTexturesPerDirectionPack(
                    new TextureExtractorClassic("hryak2/pig005"),
                    Set.of(
                        new TextureExtractorClassic("hryak2/pig004"),
                        new TextureExtractorClassic("hryak2/pig005"),
                        new TextureExtractorClassic("hryak2/pig006")
                    )
                )
            )
        );
        allEntities.add(moving_pig);
    }

    private void initMainHero() {
        mainHeroEntity = entityFactory.createMainHeroEntity(
            0f, -0f,
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
        allEntities.add(mainHeroEntity);
    }

    public void renderEntites() {
        isMoving = false;
        movingRender();
        drawVisibleEntities();
        moving_pig.move(0, .005f);
    }

    private void moveAllExceptMain(float x, float y) {
        mainHeroEntity.move(-x, -y);
    }

    private final float speed = 3f;
    private float computeStep() {
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

        camera.position.lerp(new Vector3(mainHeroEntity.getCenterCoordinates(), 0f), .075f);
        camera.zoom = isMoving ? smoothZoom(1.05f) : smoothZoom(1f);
        camera.update();
    }

    private float smoothZoom(float targetZoom) {
        // Швидкість наближення (чим менше, тим плавніше)
        float zoomSpeed = 3f; // одиниці за секунду

        // Поточний зум → поступово тягнемо до цілі
        return camera.zoom + (targetZoom - camera.zoom) * zoomSpeed * Gdx.graphics.getDeltaTime();
    }

    private void drawVisibleEntities() {
        allEntities.forEach(e -> e.draw(spriteBatch));
    }

}

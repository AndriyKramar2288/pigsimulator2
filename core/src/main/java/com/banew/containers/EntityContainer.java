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
import com.banew.other.records.MovingEntityTexturesPerDirectionPack;
import com.banew.other.records.TexturesRange;

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
            "hryak1/tile005",
            4f, 1f
        ));

        allEntities.add(entityFactory.createSimpleSprite(
            "Characters/Free Cow Sprites",
            3, 2,
            2, 2,
            -3f,
            -1f
        ));

        allEntities.add(entityFactory.createAnimatedEntity(
            -4f, 1f,
            "hryak1/tile000",
                .5f,
            new TexturesRange(
                0, 2,
                "hryak1/tile"
            ),
            new TexturesRange(
                3, 6,
                "hryak1/tile"
            )
        ));


        moving_pig = entityFactory.createMovingEntity(
            0f, -5f,
            List.of(
                new MovingEntityTexturesPerDirectionPack(
                    "hryak2/pig002",
                    new TexturesRange(1, 3, "hryak2/pig")
                ),
                new MovingEntityTexturesPerDirectionPack(
                    "hryak2/pig011",
                    new TexturesRange(10, 12, "hryak2/pig")
                ),
                new MovingEntityTexturesPerDirectionPack(
                    "hryak2/pig008",
                    new TexturesRange(7, 9, "hryak2/pig")
                ),
                new MovingEntityTexturesPerDirectionPack(
                    "hryak2/pig005",
                    new TexturesRange(4, 5, "hryak2/pig")
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
                    "hryak2/pig002",
                    new TexturesRange(1, 3, "hryak2/pig")
                ),
                new MovingEntityTexturesPerDirectionPack(
                    "hryak2/pig011",
                    new TexturesRange(10, 12, "hryak2/pig")
                ),
                new MovingEntityTexturesPerDirectionPack(
                    "hryak2/pig008",
                    new TexturesRange(7, 9, "hryak2/pig")
                ),
                new MovingEntityTexturesPerDirectionPack(
                    "hryak2/pig005",
                    new TexturesRange(4, 6, "hryak2/pig")
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

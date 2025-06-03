package com.banew.containers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
    private MovingEntity moving_pig;

    public EntityContainer(EntityFactory entityFactory, SpriteBatch spriteBatch) {
        this.entityFactory = entityFactory;
        this.spriteBatch = spriteBatch;

        allEntities = new HashSet<>();

        initMainHero();
        initOtherEntities();
    }

    private void initOtherEntities() {
        allEntities.add(entityFactory.createSimpleSprite(
            "hryak1/tile005",
            4f, 1f
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
                    new TexturesRange(4, 5, "hryak2/pig")
                )
            )
        );
        allEntities.add(mainHeroEntity);
    }

    public void renderEntites() {
        movingRender();
        drawVisibleEntities();
        moving_pig.move(0, .005f);
    }

    private void moveAllExceptMain(float x, float y) {
        allEntities.forEach(e -> {
            e.replace(x, y);
        });
    }

    private final float speed = 2f;
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
            }
        });
    }

    private void drawVisibleEntities() {
        allEntities.forEach(e -> e.draw(spriteBatch));
    }

}

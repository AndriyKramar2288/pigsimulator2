package com.banew.containers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.banew.entities.SpriteEntity;
import com.banew.factories.EntityFactory;
import com.banew.other.records.TexturesRange;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EntityContainer {
    private final Set<SpriteEntity> allEntities;
    //private MainHeroEntity mainHeroEntity;
    private final EntityFactory entityFactory;
    private final SpriteBatch spriteBatch;

    public EntityContainer(EntityFactory entityFactory, SpriteBatch spriteBatch) {
        this.entityFactory = entityFactory;
        this.spriteBatch = spriteBatch;

        allEntities = new HashSet<>();

        //initMainHero();
        initOtherEntities();
    }

    private void initOtherEntities() {
        allEntities.add(entityFactory.createSimpleSprite(
            "hryak1/tile005",
            4L, 1L
        ));

        allEntities.add(entityFactory.createAnimatedEntity(
            -4L, 1L,
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
    }

//    private void initMainHero() {
//        TexturesRange range = new TexturesRange(0, 6, "hryak1/tile");
//        mainHeroEntity = entityFactory.createMainHeroEntity(range);
//        allEntities.add(mainHeroEntity);
//    }

    public void renderEntites() {
        movingRender();
        drawVisibleEntities();
    }

    private void moveAllExceptMain(float x, float y) {
        allEntities.forEach(e -> {
            //if (e != mainHeroEntity) {
                e.move(x, y);
            //}
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

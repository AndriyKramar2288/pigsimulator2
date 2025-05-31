package com.banew.containers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.banew.entities.MainHeroEntity;
import com.banew.entities.SpriteEntity;
import com.banew.factories.EntityFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EntityContainer {
    private final Set<SpriteEntity> allEntities;
    private MainHeroEntity mainHeroEntity;
    private final EntityFactory entityFactory;
    private final SpriteBatch spriteBatch;

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
            4L, 1L
        ));
    }

    private void initMainHero() {
        mainHeroEntity = entityFactory.createMainHeroEntity();
        allEntities.add(mainHeroEntity);
    }

    public void renderEntites() {
        movingRender();
        drawVisibleEntities();
    }

    private void moveAllExceptMain(float x, float y) {
        allEntities.forEach(e -> {
            if (e != mainHeroEntity) {
                e.move(x, y);
            }
        });
    }

    private final float step = .05f;

    private final Map<Integer, Runnable> keysMovementAction = Map.of(
        Input.Keys.W, () -> moveAllExceptMain(0, -step),
        Input.Keys.S, () -> moveAllExceptMain(0, step),
        Input.Keys.A, () -> moveAllExceptMain(step, 0),
        Input.Keys.D, () -> moveAllExceptMain(-step, 0)
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

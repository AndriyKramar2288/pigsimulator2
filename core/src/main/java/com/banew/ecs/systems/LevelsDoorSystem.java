package com.banew.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.banew.containers.game.GameLevel;
import com.banew.ecs.components.LevelsDoorComponent;
import com.banew.ecs.components.MainHeroComponent;
import com.banew.ecs.components.MovingComponent;
import com.banew.ecs.components.SpriteComponent;
import com.banew.other.records.GameContext;

import java.util.Set;

public class LevelsDoorSystem extends IteratingSystem {

    private final static float REOPEN_DISTANCE = 1f;
    private final static float TELEPORT_DISTANCE = .2f;
    private final GameContext gameContext;
    private final GameLevel entityLevel;

    public LevelsDoorSystem(GameContext gameContext, GameLevel entityLevel) {
        super(Family.all(LevelsDoorComponent.class, SpriteComponent.class).get());
        this.gameContext = gameContext;
        this.entityLevel = entityLevel;
    }

    @Override
    protected void processEntity(Entity entity, float v) {
        var c = entity.getComponent(LevelsDoorComponent.class);
        var sc = entity.getComponent(SpriteComponent.class);

        Set<GameLevel> levels = gameContext.levels();
        GameLevel currentLevel = gameContext.currentLevel();

        getEngine().getEntitiesFor(Family.all(MovingComponent.class).get())
            .forEach(e -> {
                var moverSprite = e.getComponent(SpriteComponent.class);

                // ТУТ БУВ БАГ! Вираховуємо відстань між тим, хто йде, і дверима
                if (moverSprite != null && moverSprite.getCenterCoordinates().sub(sc.getCenterCoordinates()).len2() < TELEPORT_DISTANCE && !c.isClosedSet.contains(e)) {
                    GameLevel targetLevel = levels.stream()
                        .filter(l -> l.getLevelName().equals(c.levelTo))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Рівня такого нема, довбойоб. " + c.levelTo));

                    Entity targetDoor = targetLevel.getDoorByName(c.singleName);
                    targetDoor.getComponent(LevelsDoorComponent.class).isClosedSet.add(e);

                    if (e.getComponent(MainHeroComponent.class) != null) {
                        if (gameContext.currentLevel() == entityLevel) {
                            targetLevel.switchTo(e, targetDoor
                                .getComponent(SpriteComponent.class)
                                .getCenterCoordinates().add(
                                    new Vector2(moverSprite.getCenterCoordinates()).sub(sc.getCenterCoordinates())
                                ), gameContext);

                            System.out.println(
                                "Рівень змінюється з " + currentLevel.getLevelName() + " на " + targetLevel.getLevelName()
                            );
                        }
                    }
                    else {
                        targetLevel.stealEntity(entityLevel, e, targetDoor.getComponent(SpriteComponent.class).getCenterCoordinates());
                    }
                }

                if (moverSprite != null && moverSprite.getCenterCoordinates().sub(sc.getCenterCoordinates()).len2() > REOPEN_DISTANCE) {
                    c.isClosedSet.remove(e);
                }
            });
    }
}

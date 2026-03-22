package com.banew.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.banew.containers.game.GameLevel;
import com.banew.ecs.components.LevelsDoorComponent;
import com.banew.ecs.components.SpriteComponent;
import com.banew.entities.LevelsDoor;
import com.banew.entities.MovingEntity;
import com.banew.entities.alive.MainHeroEntity;
import com.banew.other.records.GameContext;

import java.util.HashSet;
import java.util.Set;

public class LevelsDoorSystem extends IteratingSystem {

    private final static float REOPEN_DISTANCE = 1f;
    private final static float TELEPORT_DISTANCE = .2f;

    private final GameContext context;

    public LevelsDoorSystem(GameContext context) {
        super(Family.all(LevelsDoorComponent.class, SpriteComponent.class).get());
        this.context = context;
    }

    @Override
    protected void processEntity(Entity entity, float v) {
        var c = entity.getComponent(LevelsDoorComponent.class);
        var sc = entity.getComponent(SpriteComponent.class);

        Set<GameLevel> levels = context.levels();
        GameLevel currentLevel = context.currentLevel();

        new HashSet<>(entityLevel.getEntitySet()).stream()
            .filter(entity -> entity instanceof MovingEntity)
            .forEach(entity -> {
                if (sc.getCenterCoordinates().sub(sc.getCenterCoordinates()).len2() < TELEPORT_DISTANCE && !c.isClosedSet.contains(entity)) {
                    GameLevel targetLevel = levels.stream()
                        .filter(l -> l.getLevelName().equals(getLevelTo()))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Рівня такого нема, довбойоб. " + getLevelTo()));

                    LevelsDoor targetDoor = targetLevel.getDoorByName(singleName);
                    targetDoor.isClosedSet.add((MovingEntity) entity);

                    if (entity instanceof MainHeroEntity) {
                        if (context.currentLevel() == entityLevel) {
                            targetLevel.switchTo((MainHeroEntity) entity, targetDoor.getCenterCoordinates().add(
                                new Vector2(entity.getCenterCoordinates()).sub(getCenterCoordinates())
                            ), context);

                            System.out.println(
                                "Рівень змінюється з " + currentLevel.getLevelName() + " на " + targetLevel.getLevelName()
                            );
                        }
                    }
                    else {
                        targetLevel.stealEntity(entityLevel, (MovingEntity) entity, targetDoor.getCenterCoordinates());
                    }
                }

                if (entity.getCenterCoordinates().sub(getCenterCoordinates()).len2() > REOPEN_DISTANCE) {
                    isClosedSet.remove(entity);
                }
            });
    }
}

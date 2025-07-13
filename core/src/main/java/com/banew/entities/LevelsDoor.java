package com.banew.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.banew.containers.GameLevel;
import com.banew.other.records.GameContext;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class LevelsDoor extends SpriteEntity {

    private final static float REOPEN_DISTANCE = 1f;
    private final static float TELEPORT_DISTANCE = .2f;

    private final String levelFrom;
    private final String levelTo;
    private final String singleName;
    @Getter
    private final Set<MovingEntity> isClosedSet = new HashSet<>();


    public LevelsDoor(Sprite sprite, String levelFrom, String levelTo, String singleName) {
        super(sprite, null, new Vector2());

        this.levelFrom = levelFrom;
        this.levelTo = levelTo;
        this.singleName = singleName;
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        getSprite().draw(spriteBatch);
    }

    @Override
    public void render(GameContext context) {

    }

    @Override
    public void step(GameContext context, GameLevel entityLevel) {
        super.step(context, entityLevel);

        Set<GameLevel> levels = context.levels();
        GameLevel currentLevel = context.currentLevel();

        new HashSet<>(entityLevel.getEntitySet()).stream()
            .filter(entity -> entity instanceof MovingEntity)
            .forEach(entity -> {
                if (entity.getCenterCoordinates().sub(getCenterCoordinates()).len2() < TELEPORT_DISTANCE && !isClosedSet.contains(entity)) {
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

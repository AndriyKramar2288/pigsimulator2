package com.banew.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.banew.containers.GameLevel;
import com.banew.other.records.GameContext;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
public class LevelsDoor extends SpriteEntity {

    private final static float REOPEN_DISTANCE = 1f;
    private final static float TELEPORT_DISTANCE = .5f;

    private final String levelFrom;
    private final String levelTo;
    private final String singleName;

    @Setter
    private boolean isOpen = false;


    public LevelsDoor(Sprite sprite, String levelFrom, String levelTo, String singleName) {
        super(sprite, null);

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
        MainHeroEntity mainHeroEntity = context.currentLevel().getMainHeroEntity();
        Set<GameLevel> levels = context.levels();
        GameLevel currentLevel = context.currentLevel();

        if (mainHeroEntity.getCenterCoordinates().sub(getCenterCoordinates()).len2() < TELEPORT_DISTANCE && isOpen()) {
            GameLevel targetLevel = levels.stream()
                .filter(l -> l.getLevelName().equals(getLevelTo()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Рівня такого нема, довбойоб"));

            System.out.println(
                "Рівень змінюється з " + currentLevel.getLevelName() + " на " + targetLevel.getLevelName()
            );

            LevelsDoor targetDoor = targetLevel.getDoorByName(singleName);
            targetDoor.setOpen(false);
            targetLevel.setMainHeroEntity(mainHeroEntity, targetDoor.getCenterCoordinates());

            context.currentLevelRef().setGameLevel(targetLevel);
            context.lightContainer().updateLevel();

            setOpen(false);
        }
        if (mainHeroEntity.getCenterCoordinates().sub(getCenterCoordinates()).len2() > REOPEN_DISTANCE) {
            setOpen(true);
        }
    }
}

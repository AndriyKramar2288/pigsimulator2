package com.banew.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.banew.containers.GameLevel;
import com.banew.other.records.GameContext;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

public class LevelsDoor extends SpriteEntity {
    @Getter
    private final String lavelFrom;
    @Getter
    private final String lavelTo;

    @Getter
    @Setter
    private boolean isOpen = false;

    public LevelsDoor(Sprite sprite, Body body, String lavelFrom, String lavelTo) {
        super(sprite, body);

        this.lavelFrom = lavelFrom;
        this.lavelTo = lavelTo;
    }

    @Override
    public GameContext render(GameContext context) {
        MainHeroEntity mainHeroEntity = context.currentLevel().getMainHeroEntity();
        Set<GameLevel> levels = context.levels();
        GameLevel currentLevel = context.currentLevel();

        if (mainHeroEntity.getCenterCoordinates().sub(getCenterCoordinates()).len2() < .5f && isOpen()) {
            GameLevel targetLevel = levels.stream()
                .filter(l -> l.getLevelName().equals(getLavelTo()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Рівня такого нема, довбойоб"));

            System.out.println(
                "Рівень змінюється з " + currentLevel.getLevelName() + " на " + targetLevel.getLevelName()
            );

            targetLevel.setMainHeroEntity(mainHeroEntity);
            context = context.withCurrentLevel(targetLevel);
            context.lightContainer().setWorld(targetLevel.getWorld(), mainHeroEntity);
            setOpen(false);
        }
        if (mainHeroEntity.getCenterCoordinates().sub(getCenterCoordinates()).len2() > 3f) {
            setOpen(true);
        }

        return super.render(context);
    }
}

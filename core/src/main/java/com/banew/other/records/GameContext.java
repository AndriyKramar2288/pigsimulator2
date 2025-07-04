package com.banew.other.records;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.banew.containers.GameLevel;
import com.banew.containers.LightContainer;
import com.banew.entities.MainHeroEntity;
import com.banew.other.dto.PlayerInfo;
import com.banew.utilites.GameLevelRef;

import java.util.Set;

public record GameContext(
    MainHeroEntity mainHeroEntity,
    OrthographicCamera camera,
    LightContainer lightContainer,
    GameLevelRef currentLevelRef,
    Set<GameLevel> levels,
    PlayerInfo playerInfo
) {
    public GameLevel currentLevel() {
        return currentLevelRef.getGameLevel();
    }
}

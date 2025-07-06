package com.banew.other.records;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.banew.containers.GameLevel;
import com.banew.entities.MainHeroEntity;
import com.banew.other.dto.PlayerInfo;
import com.banew.utilites.GameLevelRef;

import java.util.Set;

public record GameContext(
    MainHeroEntity mainHeroEntity,
    Viewport viewport,
    GameLevelRef currentLevelRef,
    Set<GameLevel> levels,
    PlayerInfo playerInfo
) {
    public GameLevel currentLevel() {
        return currentLevelRef.getGameLevel();
    }

    public OrthographicCamera camera() {
        return (OrthographicCamera) viewport.getCamera();
    }
}

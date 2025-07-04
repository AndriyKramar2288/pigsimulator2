package com.banew.other.records;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.banew.containers.GameLevel;
import com.banew.containers.LightContainer;
import com.banew.entities.MainHeroEntity;
import com.banew.other.dto.PlayerInfo;

import java.util.Set;

public record GameContext(
    MainHeroEntity mainHeroEntity,
    OrthographicCamera camera,
    LightContainer lightContainer,
    GameLevel currentLevel,
    Set<GameLevel> levels,
    PlayerInfo playerInfo
) {
    public GameContext withCurrentLevel(GameLevel newLevel) {
        return new GameContext(
            mainHeroEntity,
            camera,
            lightContainer,
            newLevel,
            levels,
            playerInfo
        );
    }
}

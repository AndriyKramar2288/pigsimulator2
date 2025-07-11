package com.banew.other.records;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.banew.containers.GameLevel;
import com.banew.containers.SoundContainer;
import com.banew.entities.MainHeroEntity;
import com.banew.other.dto.PlayerInfo;
import com.banew.utilites.Reference;

import java.util.Set;

public record GameContext(
    MainHeroEntity mainHeroEntity,
    Viewport viewport,
    Reference<GameLevel> currentLevelRef,
    Set<GameLevel> levels,
    PlayerInfo playerInfo,
    SoundContainer soundContainer
) {
    public GameLevel currentLevel() {
        return currentLevelRef.getElement();
    }

    public OrthographicCamera camera() {
        return (OrthographicCamera) viewport.getCamera();
    }
}

package com.banew.other.records;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.banew.containers.game.EffectAnimationsContainer;
import com.banew.containers.game.GameLevel;
import com.banew.containers.SoundContainer;
import com.banew.entities.alive.MainHeroEntity;
import com.banew.other.dto.PlayerInfo;
import com.banew.utilites.Reference;

import java.util.Set;

public record GameContext(
    MainHeroEntity mainHeroEntity,
    Viewport viewport,
    Reference<GameLevel> currentLevelRef,
    Set<GameLevel> levels,
    SoundContainer soundContainer,
    EffectAnimationsContainer effectAnimationsContainer) {

    public PlayerInfo playerInfo() {
        return mainHeroEntity.getPlayerInfo();
    }

    public GameLevel currentLevel() {
        return currentLevelRef.getElement();
    }

    public OrthographicCamera camera() {
        return (OrthographicCamera) viewport.getCamera();
    }
}

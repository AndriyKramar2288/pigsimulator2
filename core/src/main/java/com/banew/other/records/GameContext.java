package com.banew.other.records;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.banew.containers.GlobalGameContext;
import com.banew.containers.SoundContainer;
import com.banew.containers.game.EffectAnimationsContainer;
import com.banew.containers.game.GameLevel;
import com.banew.ecs.components.MainHeroComponent;
import com.banew.other.dto.PlayerInfo;
import com.banew.utilites.Reference;

import java.util.Set;

public record GameContext(
    GlobalGameContext globalGameContext,
    Entity mainHeroEntity,
    Viewport viewport,
    Reference<GameLevel> currentLevelRef,
    Set<GameLevel> levels,
    EffectAnimationsContainer effectAnimationsContainer) {

    public PlayerInfo playerInfo() {
        return mainHeroEntity.getComponent(MainHeroComponent.class).playerInfo;
    }

    public GameLevel currentLevel() {
        return currentLevelRef.getElement();
    }

    public OrthographicCamera camera() {
        return (OrthographicCamera) viewport.getCamera();
    }

    public SoundContainer soundContainer() {
        return globalGameContext.getSoundContainer();
    }
}

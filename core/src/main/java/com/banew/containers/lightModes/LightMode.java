package com.banew.containers.lightModes;

import com.badlogic.gdx.utils.Disposable;
import com.banew.other.records.GameContext;

public interface LightMode extends Disposable {
    void render(GameContext gameContext);
    void switchTo();
}

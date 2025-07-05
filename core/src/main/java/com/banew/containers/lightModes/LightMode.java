package com.banew.containers.lightModes;

import com.badlogic.gdx.utils.Disposable;
import com.banew.other.records.GameContext;

public interface LightMode extends Disposable {
    /**
     * Викликається кожен кадр для поточного ігрового рівня (після виклику {@link #step()})
     * @param gameContext ігровий контекст
     */
    void render(GameContext gameContext);

    /**
     * Викликається завжди для всіх {@link com.banew.containers.GameLevel}
     */
    void step();

    /**
     * Викликається при перемиканні поточного {@link com.banew.containers.GameLevel} на той, що містить цей LightMode
     */
    void switchTo();
}

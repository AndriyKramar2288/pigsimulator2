package com.banew.containers.lightModes;

import box2dLight.RayHandler;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.banew.containers.GameLevel;
import com.banew.other.records.GameContext;

public abstract class LightMode implements Disposable {

    protected final RayHandler rayHandler;
    protected final GameLevel gameLevel;

    public LightMode(GameLevel gameLevel) {
        this.gameLevel = gameLevel;
        this.rayHandler = new RayHandler(gameLevel.getWorld());
    }

    /**
     * Викликається кожен кадр для поточного ігрового рівня (після виклику {@link #step()})
     * @param gameContext ігровий контекст
     */
    public void render(GameContext gameContext) {
        updateViewport(gameContext);
    }

    private void updateViewport(GameContext gameContext) {
        Viewport vp = gameContext.viewport(); // або просто viewport
        rayHandler.useCustomViewport(
            vp.getLeftGutterWidth(),
            vp.getBottomGutterHeight(),
            vp.getScreenWidth(),
            vp.getScreenHeight()
        );
    }

    /**
     * Викликається завжди для всіх {@link com.banew.containers.GameLevel}
     */
    public abstract void step();

    /**
     * Викликається при перемиканні поточного {@link com.banew.containers.GameLevel} на той, що містить цей LightMode
     */
    public abstract void switchTo();
}

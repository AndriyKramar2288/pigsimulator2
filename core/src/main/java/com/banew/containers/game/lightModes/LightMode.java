package com.banew.containers.game.lightModes;

import box2dLight.RayHandler;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.banew.containers.game.GameLevel;
import com.banew.other.records.GameContext;

public abstract class LightMode implements Disposable {

    protected final RayHandler rayHandler;
    protected final GameLevel gameLevel;

    public LightMode(GameLevel gameLevel, World world) {
        this.gameLevel = gameLevel;
        this.rayHandler = new RayHandler(world);
    }

    /**
     * Викликається кожен кадр для поточного ігрового рівня (після виклику {@link #step(float)})
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
     * Одержати те, що буде міститись зліва-знизу екрану (аля годинник)
     * @return рядочок
     */
    public abstract String getGuiWatchText();

    /**
     * @return яскравість ігрового світу
     */
    public float getBrightness() {
        return 1;
    }

    /**
     * Викликається завжди для всіх {@link GameLevel}
     */
    public abstract void step(float deltaTime);

    /**
     * Викликається при перемиканні поточного {@link GameLevel} на той, що містить цей LightMode
     */
    public abstract void switchTo(GameContext gameContext);
}

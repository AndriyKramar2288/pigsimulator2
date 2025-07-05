package com.banew.containers.lightModes;

import box2dLight.RayHandler;
import com.banew.containers.GameLevel;
import com.banew.other.records.GameContext;

public class OblivionLightMode implements LightMode {

    private final RayHandler rayHandler;

    public OblivionLightMode(GameLevel gameLevel) {
        rayHandler = new RayHandler(gameLevel.getWorld());
        rayHandler.setAmbientLight(1, 0, 0, .7f);
    }

    @Override
    public void render(GameContext gameContext) {
        rayHandler.setCombinedMatrix(gameContext.camera()); // синхронізує з камерою
        rayHandler.updateAndRender();
    }

    @Override
    public void switchTo() {

    }

    @Override
    public void dispose() {

    }
}

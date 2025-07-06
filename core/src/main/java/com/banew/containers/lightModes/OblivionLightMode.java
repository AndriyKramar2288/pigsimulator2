package com.banew.containers.lightModes;

import com.banew.containers.GameLevel;
import com.banew.other.records.GameContext;

public class OblivionLightMode extends LightMode {

    public OblivionLightMode(GameLevel gameLevel) {
        super(gameLevel);
        rayHandler.setAmbientLight(1, 0, 0, .7f);
    }

    @Override
    public void render(GameContext gameContext) {
        super.render(gameContext);

        rayHandler.setCombinedMatrix(gameContext.camera()); // синхронізує з камерою
        rayHandler.updateAndRender();
    }

    @Override
    public void step() {

    }

    @Override
    public void switchTo() {

    }

    @Override
    public void dispose() {

    }
}

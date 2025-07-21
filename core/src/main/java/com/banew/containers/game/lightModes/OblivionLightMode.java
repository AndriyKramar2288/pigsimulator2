package com.banew.containers.game.lightModes;

import com.badlogic.gdx.physics.box2d.World;
import com.banew.containers.game.GameLevel;
import com.banew.other.records.GameContext;

public class OblivionLightMode extends LightMode {

    public OblivionLightMode(GameLevel gameLevel, World world) {
        super(gameLevel, world);
        rayHandler.setAmbientLight(1, 0, 0, .7f);
    }

    @Override
    public void render(GameContext gameContext) {
        super.render(gameContext);

        rayHandler.setCombinedMatrix(gameContext.camera()); // синхронізує з камерою
        rayHandler.updateAndRender();
    }

    @Override
    public String getGuiWatchText() {
        return "тобі пезда";
    }

    @Override
    public void step(float deltaTime) {

    }

    @Override
    public void switchTo(GameContext gameContext) {

    }

    @Override
    public void dispose() {

    }
}

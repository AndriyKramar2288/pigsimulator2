package com.banew.containers.lightModes;

import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.banew.containers.GameLevel;
import com.banew.entities.Torch;
import com.banew.entities.Zombie;
import com.banew.other.records.GameContext;

public class DayNightLightMode implements LightMode {

    private final RayHandler rayHandler;
    private final Light light;
    private float timer = 0f;

    private final GameLevel gameLevel;

    public DayNightLightMode(GameLevel gameLevel) {
        this.gameLevel = gameLevel;

        rayHandler = new RayHandler(gameLevel.getWorld());
        rayHandler.setAmbientLight(0.3f);

        light = new PointLight(
            rayHandler, 4096,
            new Color(1f, .3f, 0f, .2f), 3f, 0f, 0f
        );

        gameLevel.getEntitySet().forEach(e -> {
            if (e instanceof Torch) {
                Light torchLight = new PointLight(
                    rayHandler, 4096,
                    new Color(1f, .5f, 0f, .8f), 3f, 0f, 0f
                );

                torchLight.attachToBody(e.getBody());
            }
        });
    }

    @Override
    public void render(GameContext gameContext) {
        float currentStage = (MathUtils.cos(timer * MathUtils.PI2 / 20) + 1f) / 2f;

        Color color = new Color(
            .8f,
            MathUtils.lerp(0, .7f, currentStage),
            currentStage > .5f ? MathUtils.lerp(0, .35f, currentStage) : 0,
            .3f + MathUtils.lerp(0, .2f, currentStage)
        );

        color.mul(MathUtils.lerp(.4f, .9f, currentStage));
        rayHandler.setAmbientLight(color);


        light.setColor(new Color(1f, .3f, 0f, 1 - MathUtils.lerp(.3f, .8f, currentStage)));

        rayHandler.setCombinedMatrix(gameContext.camera()); // синхронізує з камерою
        rayHandler.updateAndRender();
    }

    @Override
    public void step() {
        timer += Gdx.graphics.getDeltaTime();
    }

    @Override
    public void switchTo() {
        light.attachToBody(gameLevel.getMainHeroEntity().getBody());
    }

    @Override
    public void dispose() {

    }
}

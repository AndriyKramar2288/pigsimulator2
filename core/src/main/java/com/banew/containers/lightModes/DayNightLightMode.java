package com.banew.containers.lightModes;

import box2dLight.Light;
import box2dLight.PointLight;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.banew.containers.GameLevel;
import com.banew.entities.Torch;
import com.banew.other.records.GameContext;

import java.util.HashSet;
import java.util.Set;

public class DayNightLightMode extends LightMode {

    private static final float CYCLE_LENGTH = 40;

    private final Light light;
    private float timer = 0f;

    private final Set<Light> torchLights = new HashSet<>();

    public DayNightLightMode(GameLevel gameLevel) {
        super(gameLevel);

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
                torchLights.add(torchLight);
            }
        });
    }

    @Override
    public void render(GameContext gameContext) {
        super.render(gameContext);

        float currentStage = (MathUtils.cos(timer * MathUtils.PI2 / CYCLE_LENGTH) + 1f) / 2f;

        Color color = new Color(
            .8f,
            MathUtils.lerp(0, .7f, currentStage),
            currentStage > .5f ? MathUtils.lerp(0, .25f, currentStage) : 0,
            .3f + MathUtils.lerp(0, .2f, currentStage)
        );

        color.mul(MathUtils.lerp(.4f, .9f, currentStage));
        rayHandler.setAmbientLight(color);

        torchLights.forEach(e -> e.setColor(
            new Color(1f, .3f, 0f, 1 - MathUtils.lerp(.3f, .75f, currentStage))
        ));

        light.setColor(new Color(1f, .3f, 0f, 1 - MathUtils.lerp(.6f, .95f, currentStage)));

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

package com.banew.containers.lightModes;

import box2dLight.DirectionalLight;
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

    private static final float CYCLE_LENGTH = 30; // в секундах

    private final Light playerLight;
    private final DirectionalLight jesusLight;
    private float timer = 0f;

    private final Set<Light> torchLights = new HashSet<>();

    public DayNightLightMode(GameLevel gameLevel) {
        super(gameLevel);

        rayHandler.setAmbientLight(0.3f);

        playerLight = new PointLight(
            rayHandler, 4096,
            new Color(1f, .3f, 0f, .2f), 3f, 0f, 0f
        );

        jesusLight = new DirectionalLight(
            rayHandler, 4046,
            new Color(1f, .3f, 0f, .5f), 30
        );
        jesusLight.setContactFilter((short) 0x0001, (short) 0, (short) 0x0001);
        jesusLight.setSoft(true);
        jesusLight.setSoftnessLength(3);

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

    /**
     * @return поточна стадія, від 0 до 1
     */
    private float currentStage() {
        return (MathUtils.cos(timer * MathUtils.PI2 / CYCLE_LENGTH) + 1f) / 2f;
    }

    @Override
    public void render(GameContext gameContext) {
        super.render(gameContext);
        // колір ВСЬОГО
        Color color = new Color(
            .8f,
            MathUtils.lerp(0, .7f, currentStage()),
            currentStage() > .75f ? MathUtils.lerp(0, .25f, currentStage()) : 0,
            .3f + MathUtils.lerp(0, .15f, currentStage())
        );
        // ініціалізуємо колір ВСЬОГО
        color.mul(MathUtils.lerp(.4f, .9f, currentStage()));
        rayHandler.setAmbientLight(color);
        // світло факелів
        torchLights.forEach(e -> e.setColor(
            new Color(1f, .3f, 0f, 1 - MathUtils.lerp(.3f, .75f, currentStage()))
        ));
        // лампочка в сраці гравця
        playerLight.setColor(new Color(1f, .3f, 0f, 1 - MathUtils.lerp(.6f, .95f, currentStage())));
        // світло тіпа від сонця
        jesusLight.setDirection(currentStage() * 180);
        jesusLight.setColor(
            1f, .4f, 0f,
            MathUtils.lerp(0, .25f, extractJesusFloat(currentStage()))
        );

        rayHandler.setCombinedMatrix(gameContext.camera()); // синхронізує з камерою
        rayHandler.updateAndRender();
    }

    @Override
    public float getBrightness() {
        return currentStage();
    }

    @Override
    public String getGuiWatchText() {
        float timeInDay = timer % CYCLE_LENGTH;
        float dayProgress = timeInDay / CYCLE_LENGTH;

        // Конвертуємо в хвилини (з урахуванням старту з 12:00)
        int totalMinutes = (int)((dayProgress * 1440 + 720) % 1440);
        int hours24 = totalMinutes / 60;

        // Конвертація в 12-годинний формат
        int displayHour = hours24 % 12;
        if (displayHour == 0) displayHour = 12;
        String meridiem = (hours24 < 12) ? "AM" : "PM";

        return String.format("%d %s", displayHour, meridiem);
    }

    private float extractJesusFloat(float currentStage) {
        float alpha = 0f;
        if (currentStage >= 0.25f && currentStage < 0.75f) {
            float t = (currentStage - 0.25f) / 0.25f; // [0..1]
            alpha = MathUtils.cos(t * MathUtils.PI) * -0.5f + 0.5f; // Плавний пік
        }
        if (currentStage >= .5f) {
            alpha = MathUtils.lerp(.45f, 1, alpha);
        }
        return alpha;
    }

    @Override
    public void step() {
        timer += Gdx.graphics.getDeltaTime();
    }

    @Override
    public void switchTo() {
        playerLight.attachToBody(gameLevel.getMainHeroEntity().getBody());
    }

    @Override
    public void dispose() {

    }
}

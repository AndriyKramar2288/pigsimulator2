package com.banew.containers.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.banew.other.records.GameContext;

import java.util.HashMap;
import java.util.Map;

public class DynamicLabelsContainer {
    private final Map<Label, Float> labels = new HashMap<>();
    private final Stage stage;

    public DynamicLabelsContainer(Stage stage) {
        this.stage = stage;
    }

    public void put(Label label, float scale) {
        labels.put(label, scale);
    }

    public void updateLabelSizes(GameContext context) {
        labels.forEach((k, v) -> {
            float scale = stage.getViewport().getWorldWidth() / 1920f; // нормалізований масштаб
            k.setFontScale(v * scale); // масштаб тексту

            float colorBrightness = 1 - context.currentLevel().getLightMode().getBrightness();
            k.getStyle().fontColor = new Color(
                colorBrightness, colorBrightness, colorBrightness, 1
            );
        });
    }

    public void remove(Label label) {
        labels.remove(label);
    }
}

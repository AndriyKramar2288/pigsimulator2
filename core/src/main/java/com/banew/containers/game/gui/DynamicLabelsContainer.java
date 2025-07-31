package com.banew.containers.game.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.HashMap;
import java.util.Map;

public class DynamicLabelsContainer {
    private final Map<Label, Float> labels = new HashMap<>();

    public void put(Label label, float scale) {
        labels.put(label, scale);
    }

    public void updateLabelSizes(Viewport viewport) {
        updateLabelSizes(viewport, 0);
    }

    public void updateLabelSizes(Viewport viewport, float colorBrightness) {
        labels.forEach((k, v) -> {
            float scale = viewport.getScreenWidth() / 1920f; // нормалізований масштаб
            k.setFontScale(v * scale); // масштаб тексту

            k.getStyle().fontColor = new Color(
                colorBrightness, colorBrightness, colorBrightness, 1
            );
        });
    }

    public void remove(Label label) {
        labels.remove(label);
    }
}

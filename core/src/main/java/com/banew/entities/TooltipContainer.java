package com.banew.entities;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.banew.containers.gui.DynamicLabelsContainer;
import com.banew.items.AbstractItem;

import java.util.HashMap;
import java.util.Map;

public class TooltipContainer {
    // висячі підказки
    private final TooltipManager tooltipManager;
    private final Map<Integer, Tooltip<Label>> slotTooltips = new HashMap<>();
    private final DynamicLabelsContainer dynamicLabelsContainer;
    private final Skin skin;

    public TooltipContainer(DynamicLabelsContainer dynamicLabelsContainer, Skin skin) {
        this.dynamicLabelsContainer = dynamicLabelsContainer;
        this.skin = skin;
        tooltipManager = TooltipManager.getInstance();
        tooltipManager.initialTime = 0.3f;
        tooltipManager.subsequentTime = 0.1f;
        tooltipManager.resetTime = 0.5f;
        tooltipManager.offsetX = 30;
        tooltipManager.offsetY = -50; // трохи нижче
        tooltipManager.hideAll();
    }

    public void reloadTooltip(AbstractItem item, ImageButton button, int index) {
        Tooltip<Label> tooltip = slotTooltips.get(index);

        if (tooltip == null) {
            // створюємо лише ОДИН раз
            Label label = new Label(item.getName(), skin);
            dynamicLabelsContainer.put(label, .4f);

            tooltip = new Tooltip<>(label, tooltipManager);
            tooltip.setInstant(true);
            button.addListener(tooltip);
            slotTooltips.put(index, tooltip);
        } else {
            // просто оновлюємо текст
            tooltip.getActor().setText(item.getName());
        }
    }

    public Tooltip<Label> deleteTooltip(int index) {
        return slotTooltips.remove(index);
    }
}

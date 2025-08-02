package com.banew.containers.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.banew.containers.GlobalGameContext;

public class FrontMainMenuContainer extends TogglingMenuContainer {
    public FrontMainMenuContainer(GlobalGameContext context, MenuContainer menuContainer) {
        super(context, menuContainer);

        var labels = context.getDynamicLabelsContainer();
        var skin = context.getMainSkin();

        setFillParent(true);
        left().pad(Value.percentWidth(.1f, this)); // центруємо все

        Table table = new Table(skin);
        add(table);

        addButton("Почати гру", context, () -> {
            toggleOff(menuContainer.viewport());
            menuContainer.toggleRace();
        });

        addButton("Налаштування", context, () -> {
            if (menuContainer.getSettingsWindow() != null) {
                menuContainer.getSettingsWindow().setVisible(true);
                menuContainer.getSettingsWindow().toFront();
            }
        });

        addButton("Вийти", context, () -> {
            Gdx.app.exit();
        });
    }

    private TextButton addButton(String text, GlobalGameContext context, Runnable action) {
        return addButton(.15f, .075f, .4f, text, context, action);
    }
}

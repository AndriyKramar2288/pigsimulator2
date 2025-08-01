package com.banew.containers.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.banew.containers.GlobalGameContext;
import com.banew.containers.game.gui.DynamicLabelsContainer;

public class FrontMainMenuContainer extends TogglingMenuContainer {
    public FrontMainMenuContainer(GlobalGameContext context, MenuContainer menuContainer) {
        var labels = context.getDynamicLabelsContainer();
        var skin = context.getMainSkin();

        setFillParent(true);
        left().pad(Value.percentWidth(.1f, this)); // центруємо все

        Table table = new Table(skin);
        add(table);

        // Кнопки
        TextButton playButton = new TextButton("Почати гру", skin);
        TextButton settingsButton = new TextButton("Налаштування", skin);
        TextButton exitButton = new TextButton("Вийти", skin);

        addButton(playButton, table, labels, context);
        addButton(settingsButton, table,  labels, context);
        addButton(exitButton, table, labels, context);

        // Додаємо слухачі на кнопки
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                toggleOff(menuContainer.viewport());
                menuContainer.getNewGameContainer().toggleOn(menuContainer.viewport());
            }
        });

        settingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (menuContainer.getSettingsWindow() != null) {
                    menuContainer.getSettingsWindow().setVisible(true);
                    menuContainer.getSettingsWindow().toFront();
                }
            }
        });

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit(); // Вихід з гри
            }
        });

        // Додати до сцени
        centerInViewport(menuContainer.viewport());
        menuContainer.getStage().addActor(this);
    }

    private void addButton(TextButton button,
                           Table table,
                           DynamicLabelsContainer labels,
                           GlobalGameContext context) {
        button.addListener(new MenuButtonsListener(context.getSoundContainer()));

        table.add(button)
            .width(Value.percentWidth(.15f, this))
            .height(Value.percentHeight(.075f, this))
            .pad(10);
        table.row();
        labels.put(button.getLabel(), .4f);
    }
}

package com.banew.containers.menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.banew.containers.GlobalGameContext;

public class SettingsWindow extends Window {
    private final Stage stage;
    private final GlobalGameContext globalGameContext;

    public SettingsWindow(Stage stage, GlobalGameContext globalGameContext) {
        super("Налаштування", globalGameContext.getMainSkin());
        this.stage = stage;
        this.globalGameContext = globalGameContext;

        Table table = new Table();
        table.setFillParent(true);

        Skin skin = globalGameContext.getMainSkin();

        setModal(true);
        setMovable(false);
        setVisible(false);
        pad(Value.percentWidth(.02f, table));

        // Повзунок гучності
        Label volumeLabel = new Label("Гучність", skin);
        volumeLabel.setColor(Color.WHITE);
        final Slider volumeSlider = new Slider(0f, 1f, 0.01f, false, skin);
        volumeSlider.setValue(1);
        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                globalGameContext.getGeneralSettings().setGeneralVolume(volumeSlider.getValue());
            }
        });

        // Кнопка назад
        TextButton backButton = new TextButton("Назад", skin);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setVisible(false);
            }
        });
        backButton.addListener(new MenuButtonsListener(globalGameContext.getSoundContainer()));

        // Layout
        row().padTop(Value.percentWidth(.02f, table));
        add(volumeLabel).center();
        row();
        add(volumeSlider).width(Value.percentWidth(.13f, table));

        row().padTop(Value.percentWidth(.02f, table));
        add(backButton)
            .width(Value.percentWidth(.1f, table))
            .height(Value.percentWidth(.03f, table));

        // Центруємо і додаємо до сцени
        pack();
        resize((int) stage.getWidth(), (int) stage.getHeight());

        table.add(this).size(Value.percentWidth(.2f, table));
        stage.addActor(table);

        globalGameContext.getDynamicLabelsContainer().put(volumeLabel, .5f);
        globalGameContext.getDynamicLabelsContainer().put(getTitleLabel(), .5f);
        globalGameContext.getDynamicLabelsContainer().put(backButton.getLabel(), .5f);
    }

    public void resize(int width, int height) {
        setPosition(
            (width - getWidth()) / 2f,
            (height - getHeight()) / 2f
        );
    }
}

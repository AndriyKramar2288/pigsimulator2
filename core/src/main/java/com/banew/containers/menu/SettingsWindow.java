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

        Skin skin = globalGameContext.getMainSkin();

        setModal(true);
        setMovable(false);
        setVisible(false);
        pad(60);

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

        // Layout
        row().pad(30);
        add(volumeLabel).left();
        row().pad(5);
        add(volumeSlider).width(400);
        row().pad(30);
        add(backButton).width(150).height(40);

        // Центруємо і додаємо до сцени
        pack();
        setPosition(
            (stage.getWidth() - getWidth()) / 2f,
            (stage.getHeight() - getHeight()) / 2f
        );

        stage.addActor(this);
    }

    public void resize(int width, int height) {
        setPosition(
            (stage.getWidth() - getWidth()) / 2f,
            (stage.getHeight() - getHeight()) / 2f
        );
    }
}

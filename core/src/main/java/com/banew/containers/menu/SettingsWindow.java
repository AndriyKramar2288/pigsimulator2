package com.banew.containers.menu;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.banew.external.GeneralSettings;

public class SettingsWindow extends Window {
    private final Viewport viewport;
    private final GeneralSettings generalSettings;

    public SettingsWindow(Skin skin, Stage stage, Viewport viewport, GeneralSettings generalSettings) {
        super("Налаштування", skin);
        this.viewport = viewport;
        this.generalSettings = generalSettings;

        setModal(true);
        setMovable(false);
        setVisible(false);
        pad(20);

        // Повзунок гучності
        Label volumeLabel = new Label("Гучність", skin);
        final Slider volumeSlider = new Slider(0f, 1f, 0.01f, false, skin);
        volumeSlider.setValue(1);
        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (generalSettings != null) {
                    generalSettings.setGeneralVolume(volumeSlider.getValue());
                }
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
        row().pad(10);
        add(volumeLabel).center();
        row().pad(10);
        add(volumeSlider).width(200);
        row().pad(20);
        add(backButton).width(150).height(40);

        // Центруємо і додаємо до сцени
        pack();
        setPosition(
            (viewport.getWorldWidth() - getWidth()) / 2f,
            (viewport.getWorldHeight() - getHeight()) / 2f
        );

        stage.addActor(this);
    }

    public void resize(int width, int height) {
        setPosition(
            (viewport.getWorldWidth() - getWidth()) / 2f,
            (viewport.getWorldHeight() - getHeight()) / 2f
        );
    }
}

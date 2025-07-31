package com.banew.containers.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.banew.containers.Container;
import com.banew.containers.GlobalGameContext;
import com.banew.containers.game.GameContainer;

import java.util.List;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class MenuContainer implements Container {
    private final Stage stage;
    private final Viewport viewport;

    private final SettingsWindow settingsWindow;

    private final Array<Image> backgroundImages = new Array<>();

    private int currentIndex = 0;
    private final GlobalGameContext context;

    public MenuContainer(GlobalGameContext context) {
        this.context = context;

        viewport = new ScreenViewport();
        stage = new Stage(viewport);

        setUpBackgroundPhotos(context.getGeneralSettings().getMenuPhotos(), context.getTextureAtlas());
        setUpButtons(context.getMainSkin(), context.getTextureAtlas());
        settingsWindow = new SettingsWindow(stage, context);
    }

    private void setUpButtons(Skin skin, TextureAtlas atlas) {
        Table table = new Table(skin);
        table.setFillParent(true);
        table.left().pad(Value.percentWidth(.1f, table)); // центруємо все

        // Кнопки
        TextButton playButton = new TextButton("Почати гру", skin);
        TextButton settingsButton = new TextButton("Налаштування", skin);
        TextButton exitButton = new TextButton("Вийти", skin);

        // Додаємо слухачі на кнопки
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                context.setContainer(new GameContainer(context));
            }
        });

        settingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (settingsWindow != null) {
                    settingsWindow.setVisible(true);
                    settingsWindow.toFront();
                }
            }
        });

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit(); // Вихід з гри
            }
        });

        addButton(playButton, table);
        addButton(settingsButton, table);
        addButton(exitButton, table);

        // Додати до сцени
        stage.addActor(table);

        Gdx.input.setInputProcessor(stage);
    }

    private void addButton(TextButton button, Table table) {
        button.addListener(new MenuButtonsListener(context.getSoundContainer()));

        // Верстка
        float pad = 20f;
        float buttonWidth = 300f;
        float buttonHeight = 60f;

        table.add(button).width(buttonWidth).height(buttonHeight).pad(pad);
        table.row();
    }

    private void setUpBackgroundPhotos(List<String> photos, TextureAtlas atlas) {
        // Додавання зображень як акторів
        photos.stream()
            .map(atlas::findRegion)
            .forEach(region -> {
                Image image = new Image(region);
                image.setFillParent(true); // займає весь екран
                image.getColor().a = 0f; // стартово прозорий
                backgroundImages.add(image);
                stage.addActor(image);
            });
        // Старт першого зображення
        showImage(0);
    }

    private void showImage(int index) {
        Image image = backgroundImages.get(index);
        image.getColor().a = 0f;
        image.setScale(1f);
        image.setOrigin(Align.center);
        image.setPosition(0, 0);
        image.addAction(sequence(
            parallel(
                fadeIn(2f),
                scaleTo(1.1f, 1.1f, 6f), // зум повільний
                moveBy(30, 20, 6f) // плавний рух
            ),
            parallel(
                fadeOut(2f),
                moveBy(60, 40, 6f),
                run(() -> {
                    currentIndex = (index + 1) % backgroundImages.size;
                    showImage(currentIndex);
                })
            )
        ));
    }

    public void render() {
        context.getDynamicLabelsContainer().updateLabelSizes(viewport);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        viewport.apply();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
        settingsWindow.resize(width, height);
    }

    @Override
    public Viewport viewport() {
        return viewport;
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}

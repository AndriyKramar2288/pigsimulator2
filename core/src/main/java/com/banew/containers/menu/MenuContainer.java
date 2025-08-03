package com.banew.containers.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.banew.containers.Container;
import com.banew.containers.GlobalGameContext;
import com.banew.containers.menu.character.creation.ConfirmationTable;
import com.banew.containers.menu.character.creation.NewCharacterProperties;
import com.banew.containers.menu.character.creation.RaceSelectTable;
import com.banew.containers.menu.character.creation.SkillSelectTable;
import lombok.Getter;

public class MenuContainer implements Container {
    private final Viewport viewport;

    private final GlobalGameContext context;

    @Getter
    private final NewCharacterProperties newCharacterProperties = new NewCharacterProperties();
    @Getter
    private final Stage stage;
    @Getter
    private final SettingsWindow settingsWindow;

    private final RaceSelectTable raceSelectContainer;
    private final FrontMainTable frontMainMenuContainer;
    private final SkillSelectTable skillSelectContainer;
    private final ConfirmationTable confirmationContainer;

    public MenuContainer(GlobalGameContext context) {
        this.context = context;

        viewport = new ScreenViewport();
        stage = new Stage(viewport);

        BackgroundPhotosContainer backgroundPhotosContainer = new BackgroundPhotosContainer(
            context.getGeneralSettings().getMenuPhotos(), context.getTextureAtlas(), stage
        );

        raceSelectContainer = new RaceSelectTable(context, this);
        frontMainMenuContainer = new FrontMainTable(context, this);
        skillSelectContainer = new SkillSelectTable(this, context);
        confirmationContainer = new ConfirmationTable(context, this);
        frontMainMenuContainer.toggleOn(viewport);

        settingsWindow = new SettingsWindow(stage, context);

        Gdx.input.setInputProcessor(stage);
    }

    public void render() {
        context.getDynamicLabelsContainer().updateLabelSizes(viewport, 1);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        viewport.apply();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
        settingsWindow.resize(width, height);

        frontMainMenuContainer.centerInViewport(viewport);
        raceSelectContainer.centerInViewport(viewport);
        skillSelectContainer.centerInViewport(viewport);
    }

    public void toggleMain() {
        frontMainMenuContainer.toggleOn(viewport);
    }

    public void toggleConfirmation() {
        confirmationContainer.toggleOn(viewport);
    }

    public void toggleRace() {
        raceSelectContainer.toggleOn(viewport);
    }

    public void toggleSkills() {
        skillSelectContainer.toggleOn(viewport);
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

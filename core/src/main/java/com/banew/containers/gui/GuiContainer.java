package com.banew.containers.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.banew.external.GeneralSettings;
import com.banew.other.records.GameContext;

import java.util.ArrayList;
import java.util.List;

public class GuiContainer implements Disposable {
    private final Stage stage;
    private final Viewport guiViewport;

    private final InventoryUI inventoryUI;

    private final List<GuiComponent> componentList = new ArrayList<>();

    public GuiContainer (GeneralSettings generalSettings) {
        guiViewport = new ScreenViewport();
        stage = new Stage(guiViewport);

        Skin ugly_skin = new Skin(Gdx.files.internal("skin/ugly/freezing-ui.json"));
        Skin freezing_skin = new Skin(Gdx.files.internal("skin/freezing/freezing-ui.json"));

        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(generalSettings.getMain_atlas_src()));
        inventoryUI = new InventoryUI(
            stage, freezing_skin, atlas
        );

        componentList.add(new GuiContainerLeftInfo(
            stage, freezing_skin, atlas, inventoryUI
        ));
        componentList.add(new GuiContainerItemsData(
            stage, atlas, inventoryUI
        ));
        componentList.add(new GuiContainerPlayerInfo(
            stage, ugly_skin, atlas
        ));

        Gdx.input.setInputProcessor(stage);
    }

    public static final float gui_scale = .75f;

    public void resize(int width, int height) {
        guiViewport.update(width, height, true);
    }

    public void render(GameContext context) {
        inventoryUI.update(context);
        componentList.forEach(e -> e.render(context));

        guiViewport.apply();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}

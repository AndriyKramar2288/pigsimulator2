package com.banew.containers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.banew.external.GeneralSettings;
import com.banew.other.dto.PlayerInfo;
import com.banew.other.records.GameContext;

public class GuiContainer implements Disposable {
    private final Stage stage;
    private final Viewport guiViewport;

    private final PlayerInfo playerInfo;
    private final ProgressBar staminaBar;
    private final ProgressBar hpBar;

    private final InventoryUI inventoryUI;

    public GuiContainer (PlayerInfo playerInfo, GeneralSettings generalSettings) {
        this.playerInfo = playerInfo;
        guiViewport = new ScreenViewport();
        stage = new Stage(guiViewport);

        Skin ugly_skin = new Skin(Gdx.files.internal("skin/ugly/freezing-ui.json"));
        Skin freezing_skin = new Skin(Gdx.files.internal("skin/freezing/freezing-ui.json"));

        Table table = new Table(freezing_skin);
        table.setFillParent(true);
        stage.addActor(table);

        inventoryUI = new InventoryUI(
            stage, freezing_skin, new TextureAtlas(Gdx.files.internal(generalSettings.getMain_atlas_src()))
        );

        staminaBar = new ProgressBar(0, playerInfo.getMaxPlayerStamina(), 1, false, ugly_skin);
        hpBar = new ProgressBar(0, playerInfo.getMaxPlayerHp(), 1, false, ugly_skin);
        hpBar.setColor(Color.RED);

        table.bottom().left();

        float widthPercent = 0.1f;
        float aspectRatio = 0.625f;

        table.add(staminaBar)
            .width(Value.percentWidth(0.1f, table))
            .height(Value.percentHeight(0.0625f, table))
            .padBottom(10)
            .padLeft(10)
            .padRight(10);

        table.add(hpBar)
            .width(Value.percentWidth(0.1f, table))
            .height(Value.percentHeight(0.0625f, table))
            .padBottom(10);

        Gdx.input.setInputProcessor(stage);
    }

    public void resize(int width, int height) {
        guiViewport.update(width, height, true);
    }

    public void render(GameContext context) {
        staminaBar.setValue(context.playerInfo().getPlayerStamina());
        hpBar.setValue(context.playerInfo().getPlayerHealth());

        inventoryUI.update(context);

        guiViewport.apply();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}

package com.banew.containers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.banew.external.GeneralSettings;
import com.banew.other.dto.PlayerInfo;
import com.banew.other.records.GameContext;

import java.util.ArrayList;
import java.util.List;

public class GuiContainer implements Disposable {
    private final Stage stage;
    private final Viewport guiViewport;

    private final PlayerInfo playerInfo;
    private ProgressBar staminaBar;
    private ProgressBar hpBar;
    private Label infoLabel;

    private final List<Image> smallHotKeys = new ArrayList<>();

    private final InventoryUI inventoryUI;

    public GuiContainer (PlayerInfo playerInfo, GeneralSettings generalSettings) {
        this.playerInfo = playerInfo;
        guiViewport = new ScreenViewport();
        stage = new Stage(guiViewport);

        Skin ugly_skin = new Skin(Gdx.files.internal("skin/ugly/freezing-ui.json"));
        Skin freezing_skin = new Skin(Gdx.files.internal("skin/freezing/freezing-ui.json"));

        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(generalSettings.getMain_atlas_src()));
        inventoryUI = new InventoryUI(
            stage, freezing_skin, atlas
        );

        initLeftInfo(freezing_skin, atlas);
        initCenterButtomData(ugly_skin, atlas, playerInfo);
        initItemsData(freezing_skin, atlas);

        Gdx.input.setInputProcessor(stage);
    }

    private void initLeftInfo(Skin freezingSkin, TextureAtlas atlas) {
        Table leftTable = new Table(freezingSkin);
        leftTable.setFillParent(true);
        leftTable.left().bottom().pad(10);
        stage.addActor(leftTable);

        infoLabel = new Label("", freezingSkin);
        infoLabel.getStyle().fontColor = new Color(.2f, .2f, .2f, 1);
        infoLabel.setFontScale(.65f);
        Table container = new Table(freezingSkin);
        container.setBackground(new TextureRegionDrawable(atlas.findRegion("gui/infoBack")));
        container.center().pad(0, 35, 20, 35);
        container.add(infoLabel);
        leftTable.add(container).center();
    }

    private void initItemsData(Skin freezing_skin, TextureAtlas atlas) {
        Table itemsTable = new Table(freezing_skin);
        itemsTable.setFillParent(true);
        itemsTable.right().bottom();
        itemsTable.pad(10);
        stage.addActor(itemsTable);

        Table hotKeysTable = new Table(freezing_skin);
        hotKeysTable.setBackground(new TextureRegionDrawable(atlas.findRegion("gui/hot_keys")));
        itemsTable.add(hotKeysTable);

        inventoryUI.extractHotKeyButtons().forEach(imageButton -> {
            Image smallHotKey = new Image(imageButton.getStyle().imageUp);
            smallHotKeys.add(smallHotKey);
            hotKeysTable.add(smallHotKey)
                .width(60)
                .height(60)
                .pad(10, 10, 20, 10);
        });
    }

    private void initCenterButtomData(Skin ugly_skin, TextureAtlas atlas, PlayerInfo playerInfo) {
        staminaBar = new ProgressBar(0, playerInfo.getMaxPlayerStamina(), 1, false, ugly_skin);
        hpBar = new ProgressBar(0, playerInfo.getMaxPlayerHp(), 1, false, ugly_skin);
        hpBar.setColor(Color.RED);

        Table centerTable = new Table(ugly_skin);
        centerTable.setFillParent(true);
        centerTable.center().bottom();
        stage.addActor(centerTable);

        Table playerInfoTable = new Table(ugly_skin);
        playerInfoTable.setBackground(new TextureRegionDrawable(atlas.findRegion("gui/long_back")));
        centerTable.add(playerInfoTable).pad(5);

        playerInfoTable.add(new Image(atlas.findRegion("gui/threeangle_white")))
            .width(Value.percentWidth(2))
            .height(Value.percentHeight(2))
            .padRight(5).padLeft(15);

        playerInfoTable.add(staminaBar)
            .width(Value.percentWidth(1.5f))
            .height(Value.percentHeight(1))
            .pad(10, 0, 10, 20);

        playerInfoTable.add(new Image(atlas.findRegion("gui/threeangle_black")))
            .width(Value.percentWidth(2))
            .height(Value.percentHeight(2))
            .pad(5);

        playerInfoTable.add(hpBar)
            .width(Value.percentWidth(1.5f))
            .height(Value.percentHeight(1))
            .pad(10, 0, 10, 15);
    }

    public void resize(int width, int height) {
        guiViewport.update(width, height, true);
    }

    public void render(GameContext context) {
        staminaBar.setValue(context.playerInfo().getPlayerStamina());
        hpBar.setValue(context.playerInfo().getPlayerHealth());

        inventoryUI.update(context);
        for (int i = 0; i < smallHotKeys.size(); i++) {
            smallHotKeys.get(i).setDrawable(inventoryUI.extractHotKeyButtons().get(i).getStyle().imageUp);
        }

        infoLabel.setText(context.currentLevel().getLightMode().getGuiWatchText());

        guiViewport.apply();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}

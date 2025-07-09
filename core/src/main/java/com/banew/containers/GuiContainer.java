package com.banew.containers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.banew.external.GeneralSettings;
import com.banew.other.dto.PlayerInfo;
import com.banew.other.records.GameContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class GuiContainer implements Disposable {
    private final Stage stage;
    private final Viewport guiViewport;

    private final PlayerInfo playerInfo;
    private ProgressBar staminaBar;
    private ProgressBar hpBar;
    private Label infoLabel;

    private final List<Cell<Image>> smallHotKeys = new ArrayList<>();

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
        initItemsData(freezing_skin, atlas);
        initCenterButtomData(ugly_skin, atlas, playerInfo);

        Gdx.input.setInputProcessor(stage);
    }

    private void initLeftInfo(Skin freezingSkin, TextureAtlas atlas) {
        Table leftTable = new Table(freezingSkin);
        leftTable.setFillParent(true);
        leftTable.left().bottom().pad(Value.percentWidth(.005f));
        stage.addActor(leftTable);

        infoLabel = new Label("", freezingSkin);
        infoLabel.getStyle().fontColor = new Color(.2f, .2f, .2f, 1);
        inventoryUI.getDynamicLabelsContainer().put(infoLabel,.65f);
        Table container = new Table(freezingSkin);
        container.setBackground(new TextureRegionDrawable(atlas.findRegion("gui/infoBack")));
        container.center().pad(
            Value.zero, Value.percentWidth(0.35f, infoLabel),
            Value.percentHeight(0.3f, infoLabel), Value.percentWidth(0.35f, infoLabel)
        );
        container.add(infoLabel);
        leftTable.add(container).center();
    }

    private void initItemsData(Skin freezing_skin, TextureAtlas atlas) {
        Table itemsTable = new Table(freezing_skin);
        itemsTable.setFillParent(true);
        itemsTable.right().bottom();
        itemsTable.pad(Value.percentWidth(.005f));
        stage.addActor(itemsTable);

        Table hotKeysTable = new Table(freezing_skin);
        hotKeysTable.setBackground(new TextureRegionDrawable(atlas.findRegion("gui/hot_keys")));
        itemsTable.add(hotKeysTable);

        inventoryUI.extractHotKeyButtons().forEach(imageButton -> {
            Image smallHotKey = new Image(imageButton.getStyle().imageUp);

            Cell<Image> smallHotKeyCell = hotKeysTable.add(smallHotKey)
                .size(Value.percentWidth(.04f, itemsTable))
                .pad(Value.percentWidth(.005f, itemsTable));

            smallHotKeys.add(smallHotKeyCell);
        });
    }

    private void initCenterButtomData(Skin ugly_skin, TextureAtlas atlas, PlayerInfo playerInfo) {
        staminaBar = new ProgressBar(0, playerInfo.getMaxPlayerStamina(), 1, false, ugly_skin);
        hpBar = new ProgressBar(0, playerInfo.getMaxPlayerHp(), 1, false, ugly_skin);
        hpBar.setColor(Color.RED);
        staminaBar.setSize(70, 10);
        hpBar.setSize(70, 10);

        Table centerTable = new Table(ugly_skin);
        centerTable.setFillParent(true);
        centerTable.center().bottom();
        stage.addActor(centerTable);

        Table playerInfoTable = new Table(ugly_skin);
        playerInfoTable.setBackground(new TextureRegionDrawable(atlas.findRegion("gui/long_back")));
        centerTable.add(playerInfoTable)
            .pad(Value.percentWidth(.01f, centerTable))
            .size(
                Value.percentWidth(.3f, centerTable),
                Value.percentHeight(.1f, centerTable)
            );

        Function<Cell<?>, Cell<?>> triangularPose = cell -> cell.size(
                Value.percentWidth(.07f, playerInfoTable),
                Value.percentHeight(.7f, playerInfoTable)
            ).padRight(5)
            .padLeft(15);

        Function<Cell<?>, Cell<?>> barPose = cell -> cell
            .size(Value.percentWidth(.35f, playerInfoTable))
            .pad(
                Value.percentWidth(.3f, playerInfoTable),
                Value.zero,
                Value.percentWidth(.3f, playerInfoTable),
                Value.percentWidth(.02f, playerInfoTable)
            );

        triangularPose.apply(
            playerInfoTable.add(new Image(atlas.findRegion("gui/threeangle_white")))
        );

        barPose.apply(
            playerInfoTable.add(staminaBar)
        );

        triangularPose.apply(
            playerInfoTable.add(new Image(atlas.findRegion("gui/threeangle_black")))
        );

        barPose.apply(
            playerInfoTable.add(hpBar)
        );
    }

    public void resize(int width, int height) {
        guiViewport.update(width, height, true);
    }

    public void render(GameContext context) {
        staminaBar.setValue(context.playerInfo().getPlayerStamina());
        hpBar.setValue(context.playerInfo().getPlayerHealth());

        inventoryUI.update(context);

        List<ImageButton> inventoryHotKeyButtons = inventoryUI.extractHotKeyButtons();
        for (int i = 0; i < smallHotKeys.size(); i++) {
            Image image = new Image(inventoryHotKeyButtons.get(i).getStyle().imageUp);
            smallHotKeys.get(i).clearActor();
            smallHotKeys.get(i).setActor(image);
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

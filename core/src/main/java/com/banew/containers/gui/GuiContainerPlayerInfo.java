package com.banew.containers.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.banew.other.records.GameContext;

import java.util.function.Function;

public class GuiContainerPlayerInfo implements GuiComponent {
    private final ProgressBar staminaBar;
    private final ProgressBar hpBar;

    public GuiContainerPlayerInfo(Stage stage, Skin ugly_skin, TextureAtlas atlas) {
        staminaBar = new ProgressBar(0, 100, 1, false, ugly_skin);
        hpBar = new ProgressBar(0, 100, 1, false, ugly_skin);
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

    public void render(GameContext context) {
        hpBar.setRange(0, context.playerInfo().getMaxPlayerHp());
        staminaBar.setRange(0, context.playerInfo().getMaxPlayerStamina());
        staminaBar.setValue(context.playerInfo().getPlayerStamina());
        hpBar.setValue(context.playerInfo().getPlayerHealth());
    }
}

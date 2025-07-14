package com.banew.containers.gui.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.banew.containers.Component;
import com.banew.other.records.GameContext;

import static com.banew.containers.gui.GuiContainer.gui_scale;

public class ContainerPlayerInfo implements Component {
    private final ProgressBar staminaBar;
    private final ProgressBar hpBar;

    private Cell<ProgressBar> updateBarSize(Cell<ProgressBar> bar, Table table) {
        return bar.size(
            Value.percentWidth(.3f * gui_scale, table),
            Value.percentHeight(.05f, table)
        ).padBottom(10);
    }

    public ContainerPlayerInfo(Stage stage, Skin skin, TextureAtlas atlas) {
        staminaBar = new ProgressBar(0, 100, 1, false, skin);
        hpBar = new ProgressBar(0, 100, 1, false, skin);

        staminaBar.setColor(Color.DARK_GRAY);

        Table centerTable = new Table(skin);
        centerTable.setFillParent(true);
        centerTable.center().bottom();
        stage.addActor(centerTable);

        updateBarSize(centerTable.add(staminaBar), centerTable)
            .padRight(Value.percentWidth(.005f * gui_scale, centerTable));

        updateBarSize(centerTable.add(hpBar), centerTable);
    }

    public void render(GameContext context) {
        hpBar.setRange(0, context.playerInfo().getMaxHp());
        staminaBar.setRange(0, context.playerInfo().getMaxStamina());
        staminaBar.setValue(context.playerInfo().getStamina());
        hpBar.setValue(context.playerInfo().getHealth());
    }
}

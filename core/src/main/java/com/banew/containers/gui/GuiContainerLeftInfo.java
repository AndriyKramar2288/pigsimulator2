package com.banew.containers.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.banew.other.records.GameContext;

import static com.banew.containers.gui.GuiContainer.gui_scale;

public class GuiContainerLeftInfo  implements GuiComponent {
    private final Label infoLabel;

    public GuiContainerLeftInfo (Stage stage, Skin freezingSkin, TextureAtlas atlas, InventoryUI inventoryUI) {
        Table leftTable = new Table();
        leftTable.setFillParent(true);
        leftTable.left().bottom().pad(Value.percentWidth(.005f));
        stage.addActor(leftTable);

        infoLabel = new Label("", freezingSkin);
        infoLabel.getStyle().fontColor = new Color(.2f, .2f, .2f, 1);
        inventoryUI.getDynamicLabelsContainer().put(infoLabel,.75f * gui_scale);
        Table container = new Table();
        container.setBackground(new TextureRegionDrawable(atlas.findRegion("gui/infoBack")));
        container.center().pad(
            Value.zero, Value.percentWidth(0.35f * gui_scale, infoLabel),
            Value.percentHeight(0.3f, infoLabel), Value.percentWidth(0.35f * gui_scale, infoLabel)
        );
        container.add(infoLabel);
        leftTable.add(container).center();
    }

    public void render(GameContext context) {
        infoLabel.setText(context.currentLevel().getLightMode().getGuiWatchText());
    }
}

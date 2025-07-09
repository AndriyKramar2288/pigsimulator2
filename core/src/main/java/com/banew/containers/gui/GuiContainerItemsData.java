package com.banew.containers.gui;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.banew.other.records.GameContext;

import java.util.ArrayList;
import java.util.List;

import static com.banew.containers.gui.GuiContainer.gui_scale;

public class GuiContainerItemsData implements GuiComponent {
    private final List<Cell<Image>> smallHotKeys = new ArrayList<>();
    private final List<Cell<Image>> smallHandKeys = new ArrayList<>();
    private final InventoryUI inventoryUI;

    public GuiContainerItemsData (Stage stage, TextureAtlas atlas, InventoryUI inventoryUI) {
        this.inventoryUI = inventoryUI;

        Table itemsTable = new Table();
        itemsTable.setFillParent(true);
        itemsTable.right().bottom();
        itemsTable.pad(Value.percentWidth(.005f));
        stage.addActor(itemsTable);

        // ліва / права
        Table handsTable = new Table();
        handsTable.setBackground(new TextureRegionDrawable(atlas.findRegion("gui/inv_hands")));
        itemsTable.add(handsTable).padRight(Value.percentWidth(.01f * gui_scale, itemsTable));
        inventoryUI.extractHandButtons().forEach(imageButton -> {
            Image smallHotKey = new Image(imageButton.getStyle().imageUp);

            Cell<Image> smallHotKeyCell = handsTable.add(smallHotKey)
                .size(Value.percentWidth(.04f * gui_scale, itemsTable))
                .pad(Value.percentWidth(.005f * gui_scale, itemsTable));

            smallHandKeys.add(smallHotKeyCell);
        });
        // гарячі клавіші
        Table hotKeysTable = new Table();
        hotKeysTable.setBackground(new TextureRegionDrawable(atlas.findRegion("gui/hot_keys")));
        itemsTable.add(hotKeysTable);
        inventoryUI.extractHotKeyButtons().forEach(imageButton -> {
            Image smallHotKey = new Image(imageButton.getStyle().imageUp);

            Cell<Image> smallHotKeyCell = hotKeysTable.add(smallHotKey)
                .size(Value.percentWidth(.04f * gui_scale, itemsTable))
                .pad(Value.percentWidth(.005f * gui_scale, itemsTable));

            smallHotKeys.add(smallHotKeyCell);
        });
    }

    public void render(GameContext context) {
        List<ImageButton> inventoryHotKeyButtons = inventoryUI.extractHotKeyButtons();
        for (int i = 0; i < smallHotKeys.size(); i++) {
            Image image = new Image(inventoryHotKeyButtons.get(i).getStyle().imageUp);
            smallHotKeys.get(i).clearActor();
            smallHotKeys.get(i).setActor(image);
        }

        List<ImageButton> inventoryHandButtons = inventoryUI.extractHandButtons();
        for (int i = 0; i < smallHandKeys.size(); i++) {
            Image image = new Image(inventoryHandButtons.get(i).getStyle().imageUp);
            smallHandKeys.get(i).clearActor();
            smallHandKeys.get(i).setActor(image);
        }
    }
}

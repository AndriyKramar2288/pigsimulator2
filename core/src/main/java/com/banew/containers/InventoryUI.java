package com.banew.containers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;
import java.util.List;

public class InventoryUI {
    private final List<Actor> actors = new ArrayList<>();

    private final int rows = 3;
    private final int cols = 7;
    private final List<ImageButton> slots = new ArrayList<>();

    private boolean visible = false;

    public static Texture makePixel(int width, int height, Color color) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    public InventoryUI(Stage stage, Skin skin, TextureAtlas atlas) {
        // блюр
        initBlur(stage);

        // табличка
        Table inventoryTable = new Table(skin);
        inventoryTable.setFillParent(true);
        inventoryTable.right().padRight(Value.percentWidth(.03f)).top();
        inventoryTable.setVisible(false);
        stage.addActor(inventoryTable);
        actors.add(inventoryTable);

        // напис "Інвентар" по центру
        addLabel("Інвентар", inventoryTable, skin);
        // слоти інвентарю
        addInventorySlots(inventoryTable, atlas);

    }

    private void addInventorySlots(Table inventoryTable, TextureAtlas atlas) {
        TextureRegionDrawable slotDrawable = new TextureRegionDrawable(atlas.findRegion("gui/transparent-inventory-for-pvp"));
        // Створюємо сітку слотів
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                ImageButton button = new ImageButton(slotDrawable.tint(new Color(.5f, .2f, .1f, .228f)));
                button.getStyle().over = slotDrawable.tint(new Color(.5f, .2f, .1f, .2f));
                slots.add(button);
                inventoryTable.add(button)
                    .size(Value.percentWidth(.05f, inventoryTable))
                    .pad(5);
            }
            inventoryTable.row();
        }
    }

    private void initBlur(Stage stage) {
        // Напівпрозорий "блюр" фон (імітація)
        Image blurOverlay = new Image(
            new TextureRegionDrawable(
                new TextureRegion(makePixel(1, 1, new Color(0, 0, 0, 0.2f)))
            )
        );
        blurOverlay.setFillParent(true);
        blurOverlay.setVisible(false);
        // Додати спочатку, щоб був позаду інвентаря
        stage.addActor(blurOverlay);
        actors.add(blurOverlay);
    }

    private void addLabel(String text, Table table, Skin skin) {
        Label topLabel = new Label(text, skin);
        topLabel.setAlignment(Align.center); // Вирівнювання тексту всередині Label
        topLabel.setColor(.8f, .8f, .8f, .4f);
        topLabel.setFontScale(.6f);
        table.add(topLabel)
            .colspan(cols)
            .padBottom(2f)
            .left()
            .padLeft(30f);
        table.row();
    }

    public void toggle() {
        visible = !visible;
        actors.forEach(e -> e.setVisible(visible));
    }

    public void update() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            toggle();
        }
    }

    public void setItem(int slotIndex, TextureRegion itemTexture) {
        if (slotIndex >= 0 && slotIndex < slots.size()) {
            ImageButton button = slots.get(slotIndex);
            button.getImage().setDrawable(new TextureRegionDrawable(itemTexture));
        }
    }

    public void clearSlot(int slotIndex) {
        if (slotIndex >= 0 && slotIndex < slots.size()) {
            ImageButton button = slots.get(slotIndex);
            button.getImage().setDrawable(null);
        }
    }
}

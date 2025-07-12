package com.banew.containers.gui;

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
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.banew.containers.gui.storage_displayers.OtherContainerDisplayer;
import com.banew.containers.gui.storage_displayers.SelfItemsDisplayer;
import com.banew.entities.TooltipContainer;
import com.banew.other.records.GameContext;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class InventoryUI {
    // ключові елементи
    private final List<Actor> actors = new ArrayList<>();
    private final SelfItemsDisplayer selfItemsDisplayer;
    private final OtherContainerDisplayer otherContainerDisplayer;
    @Getter
    private boolean visible = false;
    private GameContext context;

    // для динамічних Label
    @Getter
    private final DynamicLabelsContainer dynamicLabelsContainer;

    public static Texture makePixel(int width, int height, Color color) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    public InventoryUI(Stage stage, Skin skin, TextureAtlas atlas) {
        this.dynamicLabelsContainer = new DynamicLabelsContainer(stage);
        // блюр
        initBlur(stage);

        TooltipContainer tooltipContainer = new TooltipContainer(
            dynamicLabelsContainer, skin
        );

        // табличка
        Table inventoryTable = new Table();
        inventoryTable.setFillParent(true);
        inventoryTable.right().padRight(Value.percentWidth(.03f)).top();
        inventoryTable.setVisible(false);
        stage.addActor(inventoryTable);
        actors.add(inventoryTable);

        DragAndDrop dragAndDrop = new DragAndDrop();

        otherContainerDisplayer = new OtherContainerDisplayer(
            atlas, inventoryTable, dynamicLabelsContainer, dragAndDrop, skin
        );
        selfItemsDisplayer = new SelfItemsDisplayer(
            inventoryTable, skin, atlas, dynamicLabelsContainer, dragAndDrop, tooltipContainer
        );
    }

    private void initBlur(Stage stage) {
        // Напівпрозорий "блюр" фон (імітація)
        Image blurOverlay = new Image(
            new TextureRegionDrawable(
                new TextureRegion(makePixel(1, 1, new Color(0, 0, 0, 0.1f)))
            )
        );
        blurOverlay.setFillParent(true);
        blurOverlay.setVisible(false);
        // Додати спочатку, щоб був позаду інвентаря
        stage.addActor(blurOverlay);
        actors.add(blurOverlay);
    }

    public void toggle() {
        toggle(!visible);
    }

    public void toggle(boolean state) {
        visible = state;
        actors.forEach(e -> e.setVisible(state));
        if (!state) context.mainHeroEntity().setOpenedContainer(null); // забрати, якщо закрили інвентар
    }

    public void update(GameContext context) {
        this.context = context;
        // вкл / викл
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            toggle();
        }
        if (context.mainHeroEntity().getOpenedContainer() != null) {
            toggle(true);
        }
        selfItemsDisplayer.displayContainer(context);
        otherContainerDisplayer.displayContainer(context);
        // оновити кляті Label
        dynamicLabelsContainer.updateLabelSizes(context);
    }

    public List<ImageButton> extractHandButtons() {
        return selfItemsDisplayer.extractHandButtons();
    }

    public List<ImageButton> extractHotKeyButtons() {
        return selfItemsDisplayer.extractHotKeyButtons();
    }
}

package com.banew.containers.game.gui;

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
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.banew.containers.GlobalGameContext;
import com.banew.containers.game.gui.storage_displayers.AbstractItemsDisplayer;
import com.banew.containers.game.gui.storage_displayers.OtherContainerDisplayer;
import com.banew.containers.game.gui.storage_displayers.SelfItemsDisplayer;
import com.banew.containers.menu.SettingsWindow;
import com.banew.other.records.GameContext;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class InventoryUI {
    // ключові елементи
    private final List<Actor> actors = new ArrayList<>();
    private final SelfItemsDisplayer selfItemsDisplayer;
    private final OtherContainerDisplayer otherContainerDisplayer;
    private final DynamicLabelsContainer dynamicLabelsContainer;
    @Getter
    private boolean visible = false;
    private GameContext context;

    private SettingsWindow settingsWindow;

    public static Texture makePixel(int width, int height, Color color) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    public InventoryUI(Stage stage, GlobalGameContext globalGameContext) {
        // блюр
        initBlur(stage);

        Skin skin = globalGameContext.getMainSkin();
        TextureAtlas atlas = globalGameContext.getTextureAtlas();

        dynamicLabelsContainer = globalGameContext.getDynamicLabelsContainer();

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

        initLeftMenu(stage, skin, globalGameContext);
    }

    private void initLeftMenu(Stage stage, Skin skin, GlobalGameContext globalGameContext) {
        // зліва список кнопок
        Table leftButtons = new Table();
        stage.addActor(leftButtons);
        leftButtons.setVisible(false);
        actors.add(leftButtons);

        leftButtons.setFillParent(true);
        leftButtons.top().left().pad(Value.percentWidth(.03f));

        AbstractItemsDisplayer.addInventoryLabel(
            "Меню", skin, leftButtons, leftButtons, dynamicLabelsContainer, 1
        );

        TextButton pauseButton = new TextButton("Налаштування", skin);
        globalGameContext.initButton(pauseButton, .35f);
        leftButtons.add(pauseButton).size(
            Value.percentWidth(.1f, leftButtons),
            Value.percentWidth(.03f, leftButtons)
        );

        settingsWindow = new SettingsWindow(
            stage, globalGameContext
        );

        pauseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                settingsWindow.setVisible(true);
                settingsWindow.toFront();
            }
        });
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
        if (!state && context != null) {
            context.mainHeroEntity().setOpenedContainer(null); // забрати, якщо закрили інвентар
            settingsWindow.setVisible(false);
        }
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
        dynamicLabelsContainer.updateLabelSizes(context.viewport(), 1 - context.currentLevel().getBrightness());
    }

    public List<ImageButton> extractHandButtons() {
        return selfItemsDisplayer.extractHandButtons();
    }

    public List<ImageButton> extractHotKeyButtons() {
        return selfItemsDisplayer.extractHotKeyButtons();
    }

    public void resize(int width, int height) {
        settingsWindow.resize(width, height);
    }
}

package com.banew.containers.game.gui.storage_displayers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.banew.containers.game.ItemContainer;
import com.banew.containers.game.gui.DynamicLabelsContainer;
import com.banew.containers.game.gui.TooltipContainer;
import com.banew.items.AbstractItem;
import com.banew.other.records.GameContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class SelfItemsDisplayer extends AbstractItemsDisplayer {
    // розмір
    private final int rows = 3;
    private final int cols = 7;

    private final Map<Integer, Integer> activeSlots = new HashMap<>(); // клавіша -> слот
    private final Map<Integer, Integer> mouseSlots = new HashMap<>();

    @Override
    protected int getCols() {
        return cols;
    }

    @Override
    public ItemContainer getContainer() {
        if (context != null) {
            return context.mainHeroEntity().getInventory();
        }
        throw new RuntimeException("Контекст ще не засунули");
    }

    public SelfItemsDisplayer(Table inventoryTable,
                              Skin skin,
                              TextureAtlas atlas,
                              DynamicLabelsContainer labels,
                              DragAndDrop dragAndDrop,
                              TooltipContainer tooltipContainer) {
        super(atlas, inventoryTable, labels, dragAndDrop, skin);
        // напис "Інвентар" по центру
        addLabel("Інвентар", skin);
        // слоти інвентарю
        addInventorySlots(atlas);
        // напис
        addLabel("Гарячі клавіші", skin);
        // гарячі клавіші
        addHotKeySlots();
        // ліва / права кнопка
        addHandSlots(atlas);
    }

    private static final List<Integer> hotKeys = List.of(
        Input.Keys.NUM_1,
        Input.Keys.NUM_2,
        Input.Keys.NUM_3,
        Input.Keys.NUM_4,
        Input.Keys.NUM_5
    );

    public List<ImageButton> extractHotKeyButtons() {
        return hotKeys.stream()
            .map(activeSlots::get)
            .map(slots::get)
            .toList();
    }

    public List<ImageButton> extractHandButtons() {
        return Stream.of(
                Input.Buttons.LEFT,
                Input.Buttons.RIGHT
            )
            .map(mouseSlots::get)
            .map(slots::get)
            .toList();
    }

    private void addHandSlots(TextureAtlas atlas) {
        Table handTable = new Table();
        handTable.setBackground(new TextureRegionDrawable(
            atlas.findRegion("gui/hands_back")
        ));
        table.add(handTable)
            .colspan(2)
            .size(
                widthPercent(.06f),
                widthPercent(.03f)
            );

        List.of(
            Input.Buttons.LEFT,
            Input.Buttons.RIGHT
        ).forEach(key -> {
            ImageButton button = generateSlotButton();
            handTable.add(button)
                .size(widthPercent(.03f))
                .pad(Value.zero);

            int index = slots.indexOf(button);
            mouseSlots.put(key, index);
        });
    }

    private void addHotKeySlots() {
        hotKeys.forEach(key -> {
            ImageButton button = generateSlotButton();
            insertToTable(button);

            int index = slots.indexOf(button);
            activeSlots.put(key, index);
        });
    }

    private void addInventorySlots(TextureAtlas atlas) {
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                ImageButton button = generateSlotButton();

                if (x == cols - 2) {
                    Table buttonContainer = new Table();
                    buttonContainer.setBackground(new TextureRegionDrawable(
                        atlas.findRegion(switch (y) {
                            case 0 -> "gui/inv_back_helmet";
                            case 1 -> "gui/inv_back_armor";
                            default -> "gui/inv_back_pants";
                        })
                    ));
                    buttonContainer.add(button)
                        .size(widthPercent(.03f));

                    table.add(buttonContainer)
                        .size(
                            widthPercent(.06f),
                            widthPercent(.03f)
                        );
                    x += 1;
                }
                else {
                    insertToTable(button);
                }
            }
            table.row();
        }
    }

    @Override
    public void displayContainer(GameContext context) {
        super.displayContainer(context);

        // використати предмети
        activeSlots.forEach((key, value) -> {
            if (Gdx.input.isKeyJustPressed(key)) {
                AbstractItem item = getSlotItem(value);
                if (item != null) item.use(context, context.mainHeroEntity());
            }
        });
        mouseSlots.forEach((key, value) -> {
            if (Gdx.input.isButtonJustPressed(key)) {
                AbstractItem item = getSlotItem(value);
                if (item != null) item.use(context, context.mainHeroEntity());
            }
        });

    }
}

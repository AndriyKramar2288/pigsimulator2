package com.banew.containers.gui.storage_displayers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.banew.containers.ItemContainer;
import com.banew.containers.gui.DynamicLabelsContainer;
import com.banew.entities.TooltipContainer;
import com.banew.items.AbstractItem;
import com.banew.other.records.GameContext;
import com.banew.other.records.ItemId;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractItemsDisplayer {
    private final Table inventoryTable;
    private final DynamicLabelsContainer labels;
    private final DragAndDrop dragAndDrop;
    private final TooltipContainer tooltipContainer;
    private static float dragged_slot = -1;

    protected final TextureRegionDrawable slotDrawable;
    protected final Table table;
    protected final List<ImageButton> slots = new ArrayList<>();
    protected GameContext context;

    protected Value widthPercent(float percent) {
        return Value.percentWidth(percent, inventoryTable);
    }

    protected Value heightPercent(float percent) {
        return Value.percentWidth(percent, inventoryTable);
    }

    protected void clearData() {
        table.clearChildren();
        slots.clear();
    }

    public AbstractItemsDisplayer(TextureAtlas atlas,
                                  Table inventoryTable,
                                  DynamicLabelsContainer labels,
                                  DragAndDrop dragAndDrop,
                                  Skin skin) {
        this.labels = labels;
        this.tooltipContainer = new TooltipContainer(labels, skin);
        this.slotDrawable = new TextureRegionDrawable(atlas.findRegion("gui/transparent-inventory-for-pvp"));
        this.inventoryTable = inventoryTable;
        this.dragAndDrop = dragAndDrop;

        this.table = new Table();
        inventoryTable.add(table).top().right().padLeft(widthPercent(.01f));
    }

    public Label addLabel(String text, Skin skin) {
        Label topLabel = new Label(text, skin);
        topLabel.setColor(.8f, .8f, .8f, .4f);
        labels.put(topLabel, 0.5f);
        table.add(topLabel)
            .colspan(getCols())
            .padBottom(Value.percentHeight(.01f, inventoryTable))
            .left()
            .padLeft(Value.percentWidth(.01f, inventoryTable))
            .width(Value.percentWidth(0.03f * getCols(), inventoryTable));
        table.row();
        return topLabel;
    }

    /**
     * Отримати предмет по індексу в інвентарі
     * @param index індекс в інвентарі
     * @return предмет або null, якщо цей слот інвентарю пустий
     */
    protected AbstractItem getSlotItem(int index) {
        if (getContainer() != null) {
            return getContainer().get(index);
        }
        throw new RuntimeException("Контейнер ще не був проініціалізований!");
    }

    protected void setItem(int index, AbstractItem item) {
        if (getContainer() != null) {
            getContainer().put(index, item);
        }
        else {
            throw new RuntimeException("Контейнер ще не був проініціалізований!");
        }
    }

    protected ImageButton generateSlotButton() {
        ImageButton button = new ImageButton(slotDrawable.tint(new Color(.5f, .2f, .1f, .228f)));
        button.getStyle().over = slotDrawable.tint(new Color(.5f, .2f, .1f, .2f));
        slots.add(button);
        int index = slots.indexOf(button);
        setUpDragAndDrop(button, index);

        return button;
    }

    private void setUpDragAndDrop(Actor button, int index) {
        // drag source
        dragAndDrop.addSource(new DragAndDrop.Source(button) {
            public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                AbstractItem item = getSlotItem(index);
                if (item == null) return null;

                DragAndDrop.Payload payload = new DragAndDrop.Payload();

                Image dragImage = new Image(new TextureRegionDrawable(item.getTextureRegion()));
                dragImage.setSize(32, 32); // розмір іконки під час перетягування

                payload.setDragActor(dragImage);
                payload.setObject(new ItemId(index, getContainer())); // зберігаємо індекс джерела

                dragged_slot = index;

                successItemsChange();

                return payload;
            }

            public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
                if (target == null) {
                    dragged_slot = -1;
                }
            }
        });

        // drop target
        dragAndDrop.addTarget(new DragAndDrop.Target(button) {
            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                return true; // дозволити дроп
            }

            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                ItemId sourceData = (ItemId) payload.getObject();

                successItemsChange();
                // обмін слотами
                var fromRegion = sourceData.container().get(sourceData.index());
                var toRegion = getSlotItem(index);

                setItem(index, fromRegion);
                sourceData.container().put(sourceData.index(), toRegion);
                dragged_slot = -1;
            }
        });
    }

    private void clearDisplayedSlots(GameContext context) {
        for (ImageButton button : slots) {
            // відтінок слотів, щоб було видно
            float slotBrightness = 1 - context.currentLevel().getLightMode().getBrightness();
            Color slotColor = new Color(
                slotBrightness, slotBrightness, slotBrightness, .35f
            );
            button.getStyle().imageUp = slotDrawable.tint(slotColor);
        }
    }

    private void cleatTooltips(int index) {
        // чистка летючих підказок
        slots.get(index).getListeners().forEach(e -> {
            if (e instanceof Tooltip<?>) {
                Tooltip<Label> tooltip = tooltipContainer.deleteTooltip(index);
                labels.remove(tooltip.getActor());
                slots.get(index).removeListener(tooltip);
            }
        });
    }

    private void displayItem(int slotIndex, TextureRegion itemTexture) {
        if (slotIndex >= 0 && slotIndex < slots.size()) {
            ImageButton button = slots.get(slotIndex);

            TextureRegionDrawable drawable = new TextureRegionDrawable(itemTexture);
            Color dragged_tint_color = new Color(
                .3f, .3f, .3f, .5f
            );

            tooltipContainer.reloadTooltip(getSlotItem(slotIndex), slots.get(slotIndex), slotIndex);
            button.getStyle().imageUp = slotIndex != dragged_slot ? drawable : drawable.tint(dragged_tint_color);
        }
    }

    protected abstract int getCols();

    protected void insertToTable(ImageButton button) {
        table.add(button)
            .size(Value.percentWidth(0.03f, inventoryTable))
            .pad(Value.percentWidth(0.003f, inventoryTable));
    }

    public void displayContainer(GameContext context) {
        this.context = context;
        if (getContainer() == null) return;
        // відобразити елементи інвентарю
        clearDisplayedSlots(context);
        List<AbstractItem> items = getContainer().getList();
        for (int i = 0; i < items.size(); i++) {
            AbstractItem item = getContainer().get(i);
            if (item != null) {
                displayItem(i, item.getTextureRegion());
            }
            else {
                cleatTooltips(i);
            }
        }
    }

    private void successItemsChange() {
        if (context != null) {
            context.soundContainer().play("inv_drop");
        }
    }

    public abstract ItemContainer getContainer();
}

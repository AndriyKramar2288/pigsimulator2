package com.banew.containers.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.banew.items.AbstractItem;
import com.banew.other.records.GameContext;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class InventoryUI {
    // ключові елементи
    private final List<Actor> actors = new ArrayList<>();
    private final Skin skin;
    private boolean visible = false;
    private GameContext context;
    // розмір
    private final int rows = 3;
    private final int cols = 7;
    // інвентар
    private final List<ImageButton> slots = new ArrayList<>();
    private final Map<Integer, Integer> activeSlots = new HashMap<>(); // клавіша -> слот
    private final Map<Integer, Integer> mouseSlots = new HashMap<>();
    private int dragged_slot = -1; // індекс інвентарю, з якого ми 'витягнули', і ще нікуди не вставили
    private final DragAndDrop dragAndDrop = new DragAndDrop();
    private final TextureRegionDrawable slotDrawable; // текстура слота
    // висячі підказки
    private final TooltipManager tooltipManager;
    private final Map<Integer, Tooltip<Label>> slotTooltips = new HashMap<>();
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
        this.skin = skin;
        this.dynamicLabelsContainer = new DynamicLabelsContainer(stage);
        slotDrawable = new TextureRegionDrawable(atlas.findRegion("gui/transparent-inventory-for-pvp"));

        tooltipManager = TooltipManager.getInstance();
        tooltipManager.initialTime = 0.3f;
        tooltipManager.subsequentTime = 0.1f;
        tooltipManager.resetTime = 0.5f;
        tooltipManager.offsetX = 30;
        tooltipManager.offsetY = -50; // трохи нижче
        tooltipManager.hideAll();

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
        // напис
        addLabel("Гарячі клавіші", inventoryTable, skin);
        // гарячі клавіші
        addHotKeySlots(inventoryTable);
        // ліва / права кнопка
        addHandSlots(inventoryTable, atlas);
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

    private ImageButton generateSlotButton() {
        ImageButton button = new ImageButton(slotDrawable.tint(new Color(.5f, .2f, .1f, .228f)));
        button.getStyle().over = slotDrawable.tint(new Color(.5f, .2f, .1f, .2f));
        slots.add(button);
        int index = slots.indexOf(button);
        setUpDragAndDrop(button, index);

        return button;
    }

    private Cell<ImageButton> insertToInventory(Table ownTable, Table inventoryTable, ImageButton button) {
        return ownTable.add(button)
            .size(Value.percentWidth(0.03f, inventoryTable))
            .pad(Value.percentWidth(0.003f, inventoryTable));
    }

    private Cell<ImageButton> insertToInventory(Table inventoryTable, ImageButton button) {
        return insertToInventory(inventoryTable, inventoryTable, button);
    }

    private void addHandSlots(Table inventoryTable, TextureAtlas atlas) {
        Table handTable = new Table();
        handTable.setBackground(new TextureRegionDrawable(
            atlas.findRegion("gui/hands_back")
        ));
        inventoryTable.add(handTable)
            .colspan(2)
            .size(
                Value.percentWidth(0.06f, inventoryTable),
                Value.percentWidth(0.03f, inventoryTable)
            );

        List.of(
            Input.Buttons.LEFT,
            Input.Buttons.RIGHT
        ).forEach(key -> {
            ImageButton button = generateSlotButton();
            handTable.add(button)
                .size(Value.percentWidth(0.03f, inventoryTable))
                .pad(Value.zero);

            int index = slots.indexOf(button);
            mouseSlots.put(key, index);
        });
    }

    private void addHotKeySlots(Table inventoryTable) {
        hotKeys.forEach(key -> {
            ImageButton button = generateSlotButton();
            insertToInventory(inventoryTable, button);

            int index = slots.indexOf(button);
            activeSlots.put(key, index);
        });
    }

    private void addInventorySlots(Table inventoryTable, TextureAtlas atlas) {
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
                        .size(Value.percentWidth(0.03f, inventoryTable));

                    inventoryTable.add(buttonContainer)
                        .size(
                            Value.percentWidth(0.06f, inventoryTable),
                            Value.percentWidth(0.03f, inventoryTable)
                        );
                    x += 1;
                }
                else {
                    insertToInventory(inventoryTable, button);
                }
            }
            inventoryTable.row();
        }
    }

    private void reloadTooltip(int index) {
        AbstractItem item = getSlotItem(index);
        ImageButton button = slots.get(index);

        Tooltip<Label> tooltip = slotTooltips.get(index);
        if (tooltip == null) {
            // створюємо лише ОДИН раз
            Label label = new Label(item.getName(), skin);
            dynamicLabelsContainer.put(label, .4f);
            label.setColor(.88f, .88f, .88f, 1);

            tooltip = new Tooltip<>(label, tooltipManager);
            tooltip.setInstant(true);
            button.addListener(tooltip);
            slotTooltips.put(index, tooltip);
        } else {
            // просто оновлюємо текст
            tooltip.getActor().setText(item.getName());
        }
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
                payload.setObject(index); // зберігаємо індекс джерела

                dragged_slot = index;

                context.soundContainer().play("inv_drop");

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
                int fromIndex = (Integer) payload.getObject();
                int toIndex = index;

                context.soundContainer().play("inv_drop");
                // обмін слотами
                var fromRegion = getSlotItem(fromIndex);
                var toRegion = getSlotItem(toIndex);

                setItem(toIndex, fromRegion);
                setItem(fromIndex, toRegion);
                dragged_slot = -1;
            }
        });
    }

    /**
     * Отримати предмет по індексу в інвентарі
     * @param index індекс в інвентарі
     * @return предмет або null, якщо цей слот інвентарю пустий
     */
    private AbstractItem getSlotItem(int index) {
        if (context != null) {
            return context.mainHeroEntity().getInventory().get(index);
        }
        throw new RuntimeException("Смерть!");
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

    private void addLabel(String text, Table table, Skin skin) {
        Label topLabel = new Label(text, skin);
        topLabel.setColor(.8f, .8f, .8f, .4f);
        dynamicLabelsContainer.put(topLabel, 0.5f);
        table.add(topLabel)
            .colspan(cols)
            .padBottom(Value.percentHeight(.01f, table))
            .left()
            .padLeft(Value.percentWidth(.01f, table))
            .width(Value.percentWidth(0.05f * rows, table));
        table.row();
    }

    private void toggle() {
        visible = !visible;
        actors.forEach(e -> e.setVisible(visible));
    }

    public void update(GameContext context) {
        this.context = context;
        // вкл / викл
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            toggle();
        }
        // використати предмети
        activeSlots.forEach((key, value) -> {
            if (Gdx.input.isKeyJustPressed(key)) {
                AbstractItem item = getSlotItem(value);
                if (item != null) item.use(context);
            }
        });
        mouseSlots.forEach((key, value) -> {
            if (Gdx.input.isButtonJustPressed(key)) {
                AbstractItem item = getSlotItem(value);
                if (item != null && !visible) item.use(context);
            }
        });
        // відобразити елементи інвентарю
        clearDisplayedSlots();
        context.mainHeroEntity().getInventory().forEach(
            (k, v) -> {
                displayItem(k, v.getTextureRegion());
            }
        );
        // оновити кляті Label
        dynamicLabelsContainer.updateLabelSizes(context);
    }

    private void setItem(int index, AbstractItem item) {
        if (context != null) {
            if (item != null) {
                context.mainHeroEntity().getInventory().put(index, item);
            }
            else {
                context.mainHeroEntity().getInventory().remove(index);
            }
        }
    }

    private void displayItem(int slotIndex, TextureRegion itemTexture) {
        if (slotIndex >= 0 && slotIndex < slots.size()) {
            ImageButton button = slots.get(slotIndex);

            TextureRegionDrawable drawable = new TextureRegionDrawable(itemTexture);
            Color dragged_tint_color = new Color(
                .3f, .3f, .3f, .5f
            );

            reloadTooltip(slotIndex);
            button.getStyle().imageUp = slotIndex != dragged_slot ? drawable : drawable.tint(dragged_tint_color);
        }
    }

    private void clearDisplayedSlots() {
        for (int i = 0; i < slots.size(); i++) {
            if (context == null) break;
            if (context.mainHeroEntity().getInventory().get(i) == null) {
                ImageButton button = slots.get(i);
                // відтінок слотів, щоб було видно
                float slotBrightness = 1 - context.currentLevel().getLightMode().getBrightness();
                Color slotColor = new Color(
                    slotBrightness, slotBrightness, slotBrightness, .35f
                );
                button.getStyle().imageUp = slotDrawable.tint(slotColor);
                // чистка летючих підказок
                int finalI = i;
                button.getListeners().forEach(e -> {
                    if (e instanceof Tooltip<?>) {
                        Tooltip<Label> tooltip = slotTooltips.remove(finalI);
                        dynamicLabelsContainer.remove(tooltip.getActor());
                        button.removeListener(e);
                    }
                });
            }
        }
    }
}

package com.banew.containers.gui;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.banew.entities.ContainerEntity;
import com.banew.other.records.GameContext;

import java.util.List;

public class OtherInventory {
//    private final Table table;
//    private final Label label;
//    private final List<ImageButton> slots;
//    private final InventoryUI inventoryUI;
//    private boolean visible = false;
//    private int cols = 5;
//    private ContainerEntity currentContainer;
//
//    public OtherInventory(Table inventoryTable, Skin skin, TextureAtlas atlas, List<ImageButton> slots, InventoryUI inventoryUI) {
//        this.slots = slots;
//        this.inventoryUI = inventoryUI;
//        table = new Table();
//        label = inventoryUI.addLabel("Поки ніц", inventoryTable, table, cols);
//        inventoryTable.add(table);
//    }
//
//    private boolean isOpenedContainer(GameContext context) {
//        if (context.mainHeroEntity().getOpenedContainer() != null) {
//            if (context.mainHeroEntity().getCenterCoordinates().sub(
//                context.mainHeroEntity().getOpenedContainer().getCenterCoordinates()
//            ).len2() < ContainerEntity.CRITICAL_DISTANCE) {
//                return true;
//            }
//        }
//        context.mainHeroEntity().setOpenedContainer(null); // забрати, якщо відійшли від контейнера
//        return false;
//    }
//
//    private void toggle(boolean state) {
//        visible = state;
//        table.setVisible(state);
//    }
//
//
//    public void update(GameContext context, InventoryUI inventoryUI) {
//        // обробити всякі скині
//        if (isOpenedContainer(context)) {
//            currentContainer = context.mainHeroEntity().getOpenedContainer();
//
//            if (!visible) {
//                label.setText(currentContainer.getName());
//                int i = 0;
//                while (i < currentContainer.getSize()) {
//                    inventoryUI.insertToInventory(table, inventoryUI.generateSlotButton());
//                    i++;
//                    if (i % cols == 0) table.row();
//                }
//            }
//
//            toggle(true);
//            inventoryUI.toggle(true);
//            System.out.println("Показується гей");
//        }
//        else {
//            toggle(false);
//            if (currentContainer != null) {
//                table.clearChildren();
//                currentContainer.getItemMap().keySet().forEach(e -> slots.remove((int) e));
//                currentContainer = null;
//            }
//        }
//    }
}

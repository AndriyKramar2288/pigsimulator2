package com.banew.containers.gui;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.banew.containers.ItemContainer;
import com.banew.entities.ContainerEntity;
import com.banew.other.records.GameContext;

public class OtherContainerDisplayer extends AbstractItemsDisplayer {
    private final Skin skin;
    private boolean visible = false;
    private ItemContainer itemContainer;

    public OtherContainerDisplayer(TextureAtlas atlas, Table inventoryTable, DynamicLabelsContainer labels, DragAndDrop dragAndDrop, Skin skin) {
        super(atlas, inventoryTable, labels, dragAndDrop, skin);
        table.setVisible(visible);
        this.skin = skin;
    }

    public void setVisible(boolean visible) {
        if (context != null && visible != this.visible && !visible) {
            context.soundContainer().play("chest_close");
        }

        this.visible = visible;
        table.setVisible(visible);
        if (!visible) {
            clearData();
            itemContainer = null;
        }
    }

    @Override
    public void displayContainer(GameContext context) {
        super.displayContainer(context);

        ContainerEntity containerEntity = context.mainHeroEntity().getOpenedContainer();
        if (containerEntity != null) { // якщо в контексті тепер є відкритий контейнер
            if (context.mainHeroEntity().getCenterCoordinates().sub(containerEntity.getCenterCoordinates()).len2()
                > ContainerEntity.CRITICAL_DISTANCE) { // якщо відстань до цього контейнера завелика - закрити
                setVisible(false);
                context.mainHeroEntity().setOpenedContainer(null);
            }
            else if (!visible || !containerEntity.getContainer().equals(itemContainer)) { // оновити, якщо було закрито,
                setVisible(true);                                                         // або відкрили інший контейнер
                clearData();
                itemContainer = containerEntity.getContainer();
                addContainerActors(containerEntity.getContainer().size(), containerEntity.getName());
            }
        }
        else {
            setVisible(false);
        }
    }

    @Override
    protected int getCols() {
        return 3;
    }

    @Override
    public ItemContainer getContainer() {
        return itemContainer;
    }

    private void addContainerActors(int size, String labelText) {
        addLabel(labelText, skin);

        int i = 0;
        while (i < size) {
            ImageButton button = generateSlotButton();
            insertToTable(button);
            i++;
            if (i % getCols() == 0) {
                table.row();
            }
        }
    }
}

package com.banew.containers.game.gui.storage_displayers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.banew.containers.game.ItemContainer;
import com.banew.containers.game.gui.DynamicLabelsContainer;
import com.banew.ecs.components.InventoryComponent;
import com.banew.ecs.components.MainHeroComponent;
import com.banew.ecs.components.SpriteComponent;
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

        var mainHeroComponent = context.mainHeroEntity().getComponent(MainHeroComponent.class);
        Entity containerEntity = mainHeroComponent.openedContainer;

        if (containerEntity != null) { // якщо в контексті тепер є відкритий контейнер

            var containerComponent = containerEntity.getComponent(InventoryComponent.class);

            if (context.mainHeroEntity().getComponent(SpriteComponent.class).getCenterCoordinates()
                .sub(containerEntity.getComponent(SpriteComponent.class).getCenterCoordinates()).len2()
                > 2) { // якщо відстань до цього контейнера завелика - закрити
                setVisible(false);
                mainHeroComponent.openedContainer = null;
            }
            else if (!visible || !containerComponent.container.equals(itemContainer)) { // оновити, якщо було закрито,
                setVisible(true);                                                         // або відкрили інший контейнер
                clearData();
                itemContainer = containerComponent.container;
                addContainerActors(containerComponent.container.size(), "ПЕНІС"); // TODO
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

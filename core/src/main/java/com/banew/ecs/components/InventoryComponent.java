package com.banew.ecs.components;

import com.badlogic.ashley.core.Component;
import com.banew.containers.game.ItemContainer;

public class InventoryComponent implements Component {
    public ItemContainer container;

    public InventoryComponent init(int size) {
        this.container = new ItemContainer(size);
        return this;
    }
}

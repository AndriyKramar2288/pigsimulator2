package com.banew.ecs.components;

import com.badlogic.ashley.core.Component;
import com.banew.other.records.CursorPair;

public class InteractableComponent implements Component {
    public String name;
    public CursorPair cursors;
    public boolean isHovered = false;

    public InteractableComponent init(String name, CursorPair cursors) {
        this.name = name;
        this.cursors = cursors;
        this.isHovered = false;
        return this;
    }
}

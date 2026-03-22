package com.banew.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.banew.ecs.components.SpriteComponent;

public class DrawSystem extends IteratingSystem {
    public DrawSystem() {
        super(Family.all(SpriteComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float v) {

    }
}

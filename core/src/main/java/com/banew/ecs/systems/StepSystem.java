package com.banew.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.banew.ecs.components.SpriteComponent;

public class StepSystem extends IteratingSystem {
    public StepSystem() {
        super(Family.all(SpriteComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float v) {
        var c = entity.getComponent(SpriteComponent.class);
        var body = c.body;
        var sprite = c.sprite;

        if (body != null) {
            sprite.setPosition(
                body.getPosition().x - sprite.getWidth() / 2f,
                body.getPosition().y - sprite.getHeight() / 2f
            );
        }
    }
}

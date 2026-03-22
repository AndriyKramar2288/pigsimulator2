package com.banew.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.banew.ecs.components.SpriteComponent;

public class StepSystem extends IteratingSystem {
    private final ComponentMapper<SpriteComponent> sm = ComponentMapper.getFor(SpriteComponent.class);

    public StepSystem() {
        super(Family.all(SpriteComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float v) {
        SpriteComponent sc = sm.get(entity);

        // Якщо в сутності є тіло Box2D, спрайт має слідувати за ним!
        if (sc.body != null) {
            // Отримуємо позицію тіла (центр) і віднімаємо половину розміру спрайта, щоб правильно відмалювати
            Vector2 pos = sc.body.getPosition();
            sc.sprite.setPosition(
                pos.x - sc.sprite.getWidth() / 2f,
                pos.y - sc.sprite.getHeight() / 2f
            );
        }
    }
}

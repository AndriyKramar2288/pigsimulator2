package com.banew.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.banew.ecs.components.SpriteComponent;

import java.util.Comparator;

public class DrawSystem extends SortedIteratingSystem {
    private final SpriteBatch batch;
    private final ComponentMapper<SpriteComponent> sm = ComponentMapper.getFor(SpriteComponent.class);

    public DrawSystem(SpriteBatch batch) {
        super(Family.all(SpriteComponent.class).get(), new ZComparator());
        this.batch = batch;
    }

    @Override
    public void update(float deltaTime) {
        batch.begin();
        super.update(deltaTime); // Тут викличеться processEntity для всіх
        batch.end();
    }

    @Override
    protected void processEntity(Entity entity, float v) {
        SpriteComponent sc = sm.get(entity);
        if (sc.sprite != null) {
            sc.sprite.draw(batch);
        }
    }

    private static class ZComparator implements Comparator<Entity> {
        private final ComponentMapper<SpriteComponent> sm = ComponentMapper.getFor(SpriteComponent.class);
        @Override
        public int compare(Entity e1, Entity e2) {
            return Integer.compare(sm.get(e1).priority, sm.get(e2).priority);
        }
    }
}

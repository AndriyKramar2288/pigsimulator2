package com.banew.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.banew.ecs.components.AnimatedComponent;
import com.banew.ecs.components.SpriteComponent;

import java.util.concurrent.ThreadLocalRandom;

public class AnimatedSystem extends IteratingSystem {
    public AnimatedSystem() {
        super(Family.all(AnimatedComponent.class, SpriteComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float v) {

        var c = entity.getComponent(AnimatedComponent.class);
        var spriteComponent = entity.getComponent(SpriteComponent.class);

        c.timer += v;

        if (!c.isAnimating && c.timer >= c.delayBetween) {
            c.currentAnimation = c.animations.get(ThreadLocalRandom.current().nextInt(c.animations.size()));
            c.timer = 0f;
            c.isAnimating = true;
        }

        if (c.isAnimating && c.currentAnimation != null) {
            TextureRegion frame = c.currentAnimation.getKeyFrame(c.timer, false);
            if (frame != null) {
                spriteComponent.sprite.setRegion(frame);
            }

            if (c.currentAnimation.isAnimationFinished(c.timer)) {
                c.isAnimating = false;
                c.timer = 0f;
                spriteComponent.sprite.setRegion(c.waitingRegion);
            }
        }
    }
}

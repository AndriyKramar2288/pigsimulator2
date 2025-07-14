package com.banew.containers;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.banew.external.InitialEffectAnimation;
import com.banew.utilites.TextureExtractorDeep;

import java.util.*;

public class EffectAnimationsContainer {
    private final Map<String, Animation<TextureRegion>> animationMap = new HashMap<>();
    private final List<ActiveEffect> activeEffects = new ArrayList<>();

    public EffectAnimationsContainer(TextureAtlas atlas, List<InitialEffectAnimation> initialAnimations) {
        initialAnimations.forEach(init -> {
            List<List<TextureRegion>> lists = TextureExtractorDeep.fromOneSubtexture(
                init.getRegion(), init.getWidth(), init.getHeight(), atlas
            );

            for (int i = 0; i < init.getHeight(); i++) {
                String animationName = init.getRegion() + "_" + (i + 1);

                Animation<TextureRegion> animation = new Animation<>(
                    init.getSpeed(), lists.get(i).toArray(new TextureRegion[0])
                );

                animationMap.put(animationName, animation);
            }
        });
    }

    public void playAnimation(String animationName, Vector2 position, float scale) {
        Animation<TextureRegion> animation = animationMap.get(animationName);
        if (animation != null) {
            activeEffects.add(new ActiveEffect(animationName, position.cpy(), scale));
        }
    }

    public void clear() {
        activeEffects.clear();
    }

    public void render(SpriteBatch batch, float deltaTime) {
        Iterator<ActiveEffect> iterator = activeEffects.iterator();

        while (iterator.hasNext()) {
            ActiveEffect effect = iterator.next();
            Animation<TextureRegion> animation = animationMap.get(effect.name);

            if (animation == null) continue;

            effect.stateTime += deltaTime;

            if (animation.isAnimationFinished(effect.stateTime)) {
                iterator.remove(); // прибираємо завершену анімацію
            } else {
                TextureRegion frame = animation.getKeyFrame(effect.stateTime, false);

                float width = 1 * effect.scale;
                float height = 1 * effect.scale;

                batch.draw(frame, effect.position.x - width / 2, effect.position.y - height / 2, width, height);
            }
        }
    }

    private static class ActiveEffect {
        final String name;
        final Vector2 position;
        float stateTime;
        final float scale;

        ActiveEffect(String name, Vector2 position, float scale) {
            this.name = name;
            this.position = position;
            this.stateTime = 0f;
            this.scale = scale;
        }
    }
}

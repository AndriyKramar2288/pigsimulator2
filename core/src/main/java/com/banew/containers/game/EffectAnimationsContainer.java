package com.banew.containers.game;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.banew.external.InitialEffectAnimation;
import com.banew.utilites.TextureExtractorDeep;

import java.util.*;
import java.util.function.Supplier;

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
        playAnimation(animationName, position::cpy, scale);
    }

    public void playAnimation(String animationName, Supplier<Vector2> positionSource, float scale) {
        Animation<TextureRegion> animation = animationMap.get(animationName);
        if (animation != null) {
            activeEffects.add(new ActiveEffect(animationName, positionSource, scale));
        }
    }

    public void clear() {
        activeEffects.clear();
    }

    public void render(SpriteBatch batch, float deltaTime) {
        Iterator<ActiveEffect> iterator = activeEffects.iterator();

        batch.begin();
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

                Vector2 position = effect.positionSource.get();

                batch.draw(frame, position.x - width / 2, position.y - height / 2, width, height);
            }
        }
        batch.end();
    }

    private static class ActiveEffect {
        final String name;
        final Supplier<Vector2> positionSource;
        float stateTime;
        final float scale;

        ActiveEffect(String name, Supplier<Vector2> positionSource, float scale) {
            this.name = name;
            this.positionSource = positionSource;
            this.stateTime = 0f;
            this.scale = scale;
        }
    }
}

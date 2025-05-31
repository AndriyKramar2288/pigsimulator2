package com.banew.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.List;
import java.util.Random;

public class AnimatedEntity extends SpriteEntity {
    private float timer = 0f;
    private final float delayBetween;

    private final TextureRegion waitingRegion;
    private final List<Animation<TextureRegion>> waitingAnimations;

    private Animation<TextureRegion> currentAnimation = null;
    private boolean isAnimating = false;

    private final Random random = new Random();


    public AnimatedEntity(
        Sprite sprite,
        TextureRegion waitingRegion,
        Float delayBetween,
        List<List<TextureRegion>> regionsList
    ) {
        super(sprite);

        this.delayBetween = delayBetween;
        this.waitingRegion = waitingRegion;

        this.waitingAnimations = regionsList.stream()
            .map(regions -> {
                Animation<TextureRegion> anim = new Animation<>(
                    1f / regions.size(), // Проста рівна швидкість для кожного кадра
                    regions.toArray(new TextureRegion[0])
                );
                anim.setPlayMode(Animation.PlayMode.NORMAL);
                return anim;
            })
            .toList();
    }

    private void update(float delta) {
        timer += delta;

        if (!isAnimating && timer >= delayBetween) {
            currentAnimation = waitingAnimations.get(random.nextInt(waitingAnimations.size()));
            timer = 0f;
            isAnimating = true;
        }

        if (isAnimating && currentAnimation != null) {
            TextureRegion frame = currentAnimation.getKeyFrame(timer, false);
            if (frame != null) {
                getSprite().setRegion(frame);
            }

            if (currentAnimation.isAnimationFinished(timer)) {
                isAnimating = false;
                timer = 0f;
                getSprite().setRegion(waitingRegion);
            }
        }
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        update(Gdx.graphics.getDeltaTime());
        getSprite().draw(spriteBatch);
    }
}

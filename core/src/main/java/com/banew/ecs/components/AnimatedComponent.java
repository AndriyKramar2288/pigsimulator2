package com.banew.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.List;

public class AnimatedComponent implements Component {
    public TextureRegion waitingRegion;
    public Float delayBetween;

    public float timer = 0f;
    public List<Animation<TextureRegion>> animations = null;
    public Animation<TextureRegion> currentAnimation = null;
    public boolean isAnimating = false;

    public void init(TextureRegion waitingRegion, Float delayBetween, List<List<TextureRegion>> regionsList) {
        this.waitingRegion = waitingRegion;
        this.delayBetween = delayBetween;

        animations = regionsList.stream()
            .map(regions -> {
                Animation<TextureRegion> anim = new Animation<>(
                    .25f, // Проста рівна швидкість для кожного кадра
                    regions.toArray(new TextureRegion[0])
                );
                anim.setPlayMode(Animation.PlayMode.NORMAL);
                return anim;
            })
            .toList();
    }
}

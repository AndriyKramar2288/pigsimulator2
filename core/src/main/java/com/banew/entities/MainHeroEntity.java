package com.banew.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.banew.containers.ItemContainer;
import com.banew.other.dto.PlayerInfo;
import com.banew.other.records.GameContext;
import com.banew.other.records.MovingEntityTexturesPerDirectionPack;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.Random;

public class MainHeroEntity extends AliveEntity {
    @Setter
    @Getter
    private boolean isRunning;
    @Getter
    private final ItemContainer inventory = new ItemContainer(0);
    @Setter
    @Getter
    private ContainerEntity openedContainer;

    public MainHeroEntity(Sprite sprite, Body body, Map<String, MovingEntityTexturesPerDirectionPack> animations) {
        super(sprite, body, animations, null, new PlayerInfo());
    }

    public PlayerInfo getPlayerInfo() {
        return (PlayerInfo) getInfo();
    }

    @Override
    public void attack(AliveEntity target, GameContext gameContext) {
        gameContext.soundContainer().play("metal_punch");

        target.getBody().applyLinearImpulse(
            getCenterCoordinates().sub(target.getCenterCoordinates()).nor().scl(-.5f),
            target.getCenterCoordinates(),
            true
        );
        target.getInfo().changeHealth(-new Random().nextFloat(5, 10));
        gameContext.effectAnimationsContainer()
            .playAnimation("effect_animations/blood_1", target.getCenterCoordinates(), .2f);
    }

    @Override
    public void move(float stepX, float stepY) {
        animationList.get(movingSide).setFrameDuration(isRunning ? .15f : .25f);
        super.move(stepX, stepY);
    }

    @Override
    public void render(GameContext context) {

    }
}

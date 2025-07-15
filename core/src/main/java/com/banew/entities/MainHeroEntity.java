package com.banew.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.banew.containers.ItemContainer;
import com.banew.items.AbstractWeapon;
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
    public void attack(GameContext context, AbstractWeapon optionalWeapon) {
        context.currentLevel().getFocusEntity(context)
            .ifPresent(e -> {
                boolean near = e.getCenterCoordinates()
                    .sub(context.mainHeroEntity().getCenterCoordinates())
                    .len2() < (optionalWeapon != null ? optionalWeapon.getAttackDistance() : getInfo().getAttackDistance());

                if (near) {
                    e.injured();

                    if (optionalWeapon != null) {
                        optionalWeapon.attack(this, e, context);
                    }
                    else {
                        selfAttack(e, context);
                    }
                }
            });
    }

    @Override
    protected float getReloadHpTime() {
        return 3.33f;
    }

    @Override
    protected float getReloadHpSpeed() {
        return 20;
    }

    @Override
    public void move(float stepX, float stepY) {
        animationList.get(movingSide).setFrameDuration(isRunning ? .15f : .25f);
        super.move(stepX, stepY);
    }

    @Override
    public void render(GameContext context) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            attack(context, null);
        }
    }

    private void selfAttack(AliveEntity target, GameContext context) {
        target.getInfo().changeHealth(-new Random().nextFloat(5, 10));
        context.soundContainer().play("classic_punch");

        target.getBody().applyLinearImpulse(
            getCenterCoordinates().sub(target.getCenterCoordinates()).nor().scl(-.1f),
            target.getCenterCoordinates(),
            true
        );
    }
}

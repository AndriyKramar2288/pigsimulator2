package com.banew.entities.alive;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.banew.containers.game.ItemContainer;
import com.banew.entities.containers.ContainerEntity;
import com.banew.items.weapon.AbstractWeapon;
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
    protected void reloadStats() {
        if (isRunning()) {
            reloadStaminaTimer = 0;
        }

        super.reloadStats();
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
                    reloadStaminaTimer = 0;

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
        animationList.get(movingSide).setFrameDuration(isRunning ? .1f : .15f);
        super.move(stepX, stepY);
    }

    @Override
    public void render(GameContext context) {
        super.render(context);

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            attack(context, null);
        }
    }

    @Override
    public void die(GameContext context) {
        System.out.println("Я здох");
    }

    private void selfAttack(AliveEntity target, GameContext context) {
        if (getPlayerInfo().getStamina() < 10) return;
        getPlayerInfo().changeStamina(-10);

        target.getInfo().changeHealth(-new Random().nextFloat(5, 10));
        context.soundContainer().play("classic_punch");

        target.getBody().applyLinearImpulse(
            getCenterCoordinates().sub(target.getCenterCoordinates()).nor().scl(-.1f),
            target.getCenterCoordinates(),
            true
        );
    }
}

package com.banew.entities.alive;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.banew.entities.MovingEntity;
import com.banew.items.weapon.AbstractWeapon;
import com.banew.other.dto.AliveEntityInfo;
import com.banew.other.records.CursorPair;
import com.banew.other.records.GameContext;
import com.banew.other.records.MovingEntityTexturesPerDirectionPack;
import lombok.Getter;

import java.util.Map;

public abstract class AliveEntity extends MovingEntity {
    private final CursorPair attackCursor;
    @Getter
    private final AliveEntityInfo info;
    protected float reloadHpTimer = 0;
    protected float reloadStaminaTimer = 0;

    public AliveEntity(Sprite sprite,
                       Body body,
                       Map<String, MovingEntityTexturesPerDirectionPack> animations,
                       CursorPair attackCursor, AliveEntityInfo info) {
        super(sprite, body, animations);
        this.attackCursor = attackCursor;
        this.info = info;
    }

    /**
     * @param context ігровий контекст
     * @param optionalWeapon зброя (якщо без зброї - передавати null)
     */
    public abstract void attack(GameContext context, AbstractWeapon optionalWeapon);

    public void injured() {
        reloadHpTimer = 0;
    }

    protected abstract float getReloadHpTime();
    protected abstract float getReloadHpSpeed();
    protected float getReloadStaminaTime() {
        return 3;
    }
    protected float getReloadStaminaSpeed() {
        return 10;
    }

    public void дрочити() {
        System.out.println("дрочу");
    }

    protected void reloadStats() {
        reloadHpTimer += Gdx.graphics.getDeltaTime();
        reloadStaminaTimer += Gdx.graphics.getDeltaTime();

        if (reloadStaminaTimer > getReloadStaminaTime()) {
            info.changeStamina(getReloadStaminaSpeed() * Gdx.graphics.getDeltaTime());
        }

        if (reloadHpTimer > getReloadHpTime() && info.getHealth() > 0) {
            info.changeHealth(getReloadHpSpeed() * Gdx.graphics.getDeltaTime());
        }
    }

    public void die(GameContext context) {
        context.effectAnimationsContainer()
            .playAnimation("effect_animations/blood_1", getCenterCoordinates(), 1f);
        context.soundContainer().play("babah");
        context.currentLevel().killAliveEntity(this);
    }

    @Override
    public void render(GameContext context) {
        reloadStats();

        if (cursorTouchDown(context) && !(this instanceof MainHeroEntity)) {
            boolean near = getCenterCoordinates().sub(context.mainHeroEntity().getCenterCoordinates()).len2() <
                context.playerInfo().getAttackDistance();
            attackCursor.use(near);
        }

        if (info.getHealth() == 0) {
            die(context);
        }
    }
}

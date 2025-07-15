package com.banew.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.banew.items.AbstractWeapon;
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
    private float reloadHpTimer;

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

    public void дрочити() {
        System.out.println("дрочу");
    }

    @Override
    public void render(GameContext context) {
        reloadHpTimer += Gdx.graphics.getDeltaTime();

        if (cursorTouchDown(context)) {
            boolean near = getCenterCoordinates().sub(context.mainHeroEntity().getCenterCoordinates()).len2() <
                context.playerInfo().getAttackDistance();
            attackCursor.use(near);
        }

        if (info.getHealth() == 0) {
            context.effectAnimationsContainer()
                .playAnimation("effect_animations/blood_1", getCenterCoordinates(), 1f);
            context.soundContainer().play("babah");

            context.currentLevel().killAliveEntity(this);
        }
        else if (reloadHpTimer > getReloadHpTime()) {
            info.changeHealth(getReloadHpSpeed() * Gdx.graphics.getDeltaTime());
        }
    }
}

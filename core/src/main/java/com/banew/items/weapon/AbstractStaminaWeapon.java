package com.banew.items.weapon;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.banew.ecs.components.AliveParamsComponent;
import com.banew.other.records.GameContext;

import java.util.Random;

public abstract class AbstractStaminaWeapon extends AbstractWeapon {
    public AbstractStaminaWeapon(TextureRegion textureRegion, String name) {
        super(textureRegion, name);
    }

    protected abstract float getMaxStaminaDebuff();

    @Override
    public void attack(Entity attacker, Entity victim, GameContext context) {
        var attackerAlive = attacker.getComponent(AliveParamsComponent.class);

        if (attackerAlive.info.getStamina() < getMaxStaminaDebuff() * .25f) return;

        attackerAlive.info.changeStamina(
            -new Random().nextFloat(getMaxStaminaDebuff() * 0.5f, getMaxStaminaDebuff())
        );
        super.attack(attacker, victim, context);
    }
}

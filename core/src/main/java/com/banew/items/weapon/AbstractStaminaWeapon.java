package com.banew.items.weapon;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.banew.entities.alive.AliveEntity;
import com.banew.other.dto.PlayerInfo;
import com.banew.other.records.GameContext;

import java.util.Random;

public abstract class AbstractStaminaWeapon extends AbstractWeapon {
    public AbstractStaminaWeapon(TextureRegion textureRegion, String name) {
        super(textureRegion, name);
    }

    /**
     * @return скільки {@link AliveEntity} attacker ({@link #attack(AliveEntity, AliveEntity, GameContext)})
     * втратить {@link PlayerInfo#getStamina()} після вдалої атаки (рахується від x * 0.5 до x)
     */
    protected abstract float getMaxStaminaDebuff();

    @Override
    public void attack(AliveEntity attacker, AliveEntity victim, GameContext context) {
        if (attacker.getInfo().getStamina() < getMaxStaminaDebuff() * .25f) return;

        attacker.getInfo().changeStamina(
            -new Random().nextFloat(getMaxStaminaDebuff() * 0.5f, getMaxStaminaDebuff())
        );
        super.attack(attacker, victim, context);
    }
}

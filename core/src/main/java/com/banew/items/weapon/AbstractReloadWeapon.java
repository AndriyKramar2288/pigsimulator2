package com.banew.items.weapon;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.banew.entities.alive.AliveEntity;
import com.banew.other.records.GameContext;

import java.time.Instant;

public abstract class AbstractReloadWeapon extends AbstractStaminaWeapon {

    private Instant lastUse;

    public AbstractReloadWeapon(TextureRegion textureRegion, String name) {
        super(textureRegion, name);
        lastUse = Instant.now();
    }

    protected abstract float getReloadTime();

    @Override
    public void attack(AliveEntity attacker, AliveEntity victim, GameContext context) {
        if (lastUse.plusMillis((long) (getReloadTime() * 1000f)).isBefore(Instant.now())) {
            lastUse = Instant.now();
            super.attack(attacker, victim, context);
        }
    }
}

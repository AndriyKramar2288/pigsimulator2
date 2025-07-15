package com.banew.items;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.banew.entities.AliveEntity;
import com.banew.other.records.GameContext;

import java.util.Random;

public class Sword extends AbstractReloadWeapon {

    public Sword(TextureRegion textureRegion, String name) {
        super(textureRegion, name);
    }

    @Override
    protected float getMaxStaminaDebuff() {
        return 10;
    }

    @Override
    protected float getReloadTime() {
        return 1f;
    }

    @Override
    public float getAttackDistance() {
        return .5f;
    }

    @Override
    public void successfulAttack(AliveEntity attacker, AliveEntity victim, GameContext context) {
        super.successfulAttack(attacker, victim, context);

        context.soundContainer().play("metal_punch");

        victim.getBody().applyLinearImpulse(
            attacker.getCenterCoordinates().sub(victim.getCenterCoordinates()).nor().scl(-.5f),
            victim.getCenterCoordinates(),
            true
        );
        victim.getInfo().changeHealth(-new Random().nextFloat(10, 20));
        context.effectAnimationsContainer()
            .playAnimation("effect_animations/blood_1", victim.getCenterCoordinates(), .4f);
    }
}

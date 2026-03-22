package com.banew.items.weapon;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.banew.ecs.components.AliveParamsComponent;
import com.banew.ecs.components.SpriteComponent;
import com.banew.other.records.GameContext;

import java.util.Random;

public class Sword extends AbstractReloadWeapon {

    public Sword(TextureRegion textureRegion, String name) {
        super(textureRegion, name);
    }

    @Override
    protected float getMaxStaminaDebuff() {
        return 20;
    }

    @Override
    protected float getReloadTime() {
        return .3f;
    }

    @Override
    public float getAttackDistance() {
        return .5f;
    }

    @Override
    public void successfulAttack(Entity attacker, Entity victim, GameContext context) {
        super.successfulAttack(attacker, victim, context);

        var attackerSprite = attacker.getComponent(SpriteComponent.class);
        var victimSprite = victim.getComponent(SpriteComponent.class);
        var victimAlive = victim.getComponent(AliveParamsComponent.class);

        context.soundContainer().play("metal_punch");
        context.effectAnimationsContainer().playAnimation(
            "Objects/swords_1", attackerSprite::getCenterCoordinates, .5f
        );

        victimSprite.body.applyLinearImpulse(
            attackerSprite.getCenterCoordinates().sub(victimSprite.getCenterCoordinates()).nor().scl(-.5f),
            victimSprite.getCenterCoordinates(),
            true
        );
        victimAlive.info.changeHealth(-new Random().nextFloat(10, 20));
        context.effectAnimationsContainer()
            .playAnimation("effect_animations/blood_1", victimSprite.getCenterCoordinates(), .4f);
    }
}

package com.banew.items.weapon;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.banew.ecs.components.AliveParamsComponent;
import com.banew.ecs.components.SpriteComponent;
import com.banew.items.AbstractItem;
import com.banew.other.records.GameContext;

public abstract class AbstractWeapon extends AbstractItem {

    public AbstractWeapon(TextureRegion textureRegion, String name) {
        super(textureRegion, name);
    }

    @Override
    public void use(GameContext gameContext, Entity user) {
        // Логіка з твого старого MainHeroEntity.attack()
        gameContext.currentLevel().getFocusEntity(gameContext).ifPresent(victim -> {
            var userSprite = user.getComponent(SpriteComponent.class);
            var victimSprite = victim.getComponent(SpriteComponent.class);

            boolean near = victimSprite.getCenterCoordinates().sub(userSprite.getCenterCoordinates()).len2() < getAttackDistance();

            if (near) {
                var victimAlive = victim.getComponent(AliveParamsComponent.class);
                var userAlive = user.getComponent(AliveParamsComponent.class);

                victimAlive.reloadHpTimer = 0; // victim.injured()
                userAlive.reloadStaminaTimer = 0;

                attack(user, victim, gameContext);
            }
        });
    }

    public abstract float getAttackDistance();

    public void attack(Entity attacker, Entity victim, GameContext context) {
        successfulAttack(attacker, victim, context);
    }

    protected void successfulAttack(Entity attacker, Entity victim, GameContext context) {
        victim.getComponent(AliveParamsComponent.class).reloadHpTimer = 0; // injured
    }
}

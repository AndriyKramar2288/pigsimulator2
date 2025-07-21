package com.banew.items.weapon;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.banew.entities.alive.AliveEntity;
import com.banew.items.AbstractItem;
import com.banew.other.records.GameContext;

public abstract class AbstractWeapon extends AbstractItem {

    public AbstractWeapon(TextureRegion textureRegion, String name) {
        super(textureRegion, name);
    }

    /**
     * <h3>Порядок викликів:</h3>
     * <ol>
     *     <li>Деякий {@link AliveEntity} викликає цей метод</li>
     *     <li>
     *         Цей метод викликає {@link AliveEntity#attack(GameContext, AbstractWeapon)},
     *         де конкретна реалізація вирішує,
     *         де і стосовно кого виконати {@link #attack(AliveEntity, AliveEntity, GameContext)}
     *     </li>
     * </ol>
     *
     * @param gameContext контекст
     * @param user користувач зброї
     */
    @Override
    public void use(GameContext gameContext, AliveEntity user) {
        user.attack(gameContext, this);
    }

    public abstract float getAttackDistance();

    public void attack(AliveEntity attacker, AliveEntity victim, GameContext context) {
          successfulAttack(attacker, victim, context);
    }

    protected void successfulAttack(AliveEntity attacker, AliveEntity victim, GameContext context) {
        victim.injured();
    }
}

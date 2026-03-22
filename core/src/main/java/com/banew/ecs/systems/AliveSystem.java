package com.banew.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.banew.containers.game.GameLevel;
import com.banew.ecs.components.AliveParamsComponent;
import com.banew.ecs.components.MainHeroComponent;
import com.banew.ecs.components.SpriteComponent;
import com.banew.other.records.CursorPair;
import com.banew.other.records.GameContext;

public class AliveSystem extends IteratingSystem {

    private final ComponentMapper<AliveParamsComponent> amMapper = ComponentMapper.getFor(AliveParamsComponent.class);
    private final ComponentMapper<SpriteComponent> scMapper = ComponentMapper.getFor(SpriteComponent.class);

    private final CursorPair cursorPair;
    private final GameContext gameContext;

    public AliveSystem(CursorPair cursorPair, GameContext gameContext) {
        super(Family.all(AliveParamsComponent.class, SpriteComponent.class).get());
        this.cursorPair = cursorPair;
        this.gameContext = gameContext;
    }

    @Override
    protected void processEntity(Entity entity, float v) {

        var am = amMapper.get(entity);
        var sc = scMapper.get(entity);

        am.reloadHpTimer += Gdx.graphics.getDeltaTime();
        am.reloadStaminaTimer += Gdx.graphics.getDeltaTime();

        if (am.reloadStaminaTimer > am.reloadStaminaTime) {
            am.info.changeStamina(am.getReloadStaminaSpeed * Gdx.graphics.getDeltaTime());
        }

        if (am.reloadHpTimer > am.getReloadHpTime && am.info.getHealth() > 0) {
            am.info.changeHealth(am.getReloadHpSpeed * Gdx.graphics.getDeltaTime());
        }

        if (sc.cursorTouchDown(gameContext) && (entity.getComponent(MainHeroComponent.class) == null)) {
            boolean near = sc.getCenterCoordinates().sub(gameContext.mainHeroEntity().getComponent(SpriteComponent.class).getCenterCoordinates()).len2() <
                gameContext.playerInfo().getAttackDistance();
            cursorPair.use(near);
        }

        if (am.info.getHealth() == 0) {
            die(gameContext, sc, entity);
        }
    }

    private void die(GameContext context, SpriteComponent sc, Entity entity) {
        context.effectAnimationsContainer()
            .playAnimation("effect_animations/blood_1", sc.getCenterCoordinates(), 1f);
        context.soundContainer().play("babah");
        context.currentLevel().killAliveEntity(entity);
    }
}

package com.banew.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.banew.containers.GameLevel;
import com.banew.other.dto.AliveEntityInfo;
import com.banew.other.records.CursorPair;
import com.banew.other.records.GameContext;
import com.banew.other.records.MovingEntityTexturesPerDirectionPack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Zombie extends AliveEntity {

    private List<Vector2> wayToPlayer = new ArrayList<>();
    private float resetTimer = 0;
    private float attackTimer = 0;

    public Zombie(Sprite sprite,
                  Body body,
                  Map<String, MovingEntityTexturesPerDirectionPack> animations,
                  CursorPair attackCursor,
                  AliveEntityInfo info) {
        super(sprite, body, animations, attackCursor, info);
    }

    @Override
    public void attack(AliveEntity target, GameContext context) {
        boolean near = getCenterCoordinates().sub(target.getCenterCoordinates()).len2() < getInfo().getAttackDistance();
        boolean cooldown = attackTimer > .4f;

        if (near && cooldown) {
            attackTimer = 0;

            context.effectAnimationsContainer()
                .playAnimation("effect_animations/blood_1", target.getCenterCoordinates(), .2f);

            context.playerInfo().changeHealth(-new Random().nextFloat(40, 70));
            context.soundContainer().play("stons");

            target.getBody().applyLinearImpulse(
                getCenterCoordinates().sub(target.getCenterCoordinates()).nor().scl(-.3f),
                target.getCenterCoordinates(),
                true
            );
        }
    }

    @Override
    public void render(GameContext context) {
        super.render(context);
        checkPlayer(context);

        resetTimer += Gdx.graphics.getDeltaTime();
        attackTimer += Gdx.graphics.getDeltaTime();

        if (wayToPlayer.isEmpty() || resetTimer > 1) {
            resetTimer = 0;
            wayToPlayer = context.currentLevel().findPath(
                getCenterCoordinates(),
                context.mainHeroEntity().getCenterCoordinates()
            );
        }
    }

    @Override
    public void step(GameContext context, GameLevel entityLevel) {
        super.step(context, entityLevel);
        float speed = 1f;

        wayToPlayer.removeIf(v -> new Vector2(v).sub(getCenterCoordinates()).len2() < .15f);

        if (!wayToPlayer.isEmpty()) {
            Vector2 stepToTarget = followTarget(
                getCenterCoordinates(),
                wayToPlayer.get(0),
                speed
            );

            doNotMove();
            move(stepToTarget.x, stepToTarget.y);
        }
        else if (context.currentLevel() == entityLevel) {
            Vector2 step = new Vector2(speed, 0).rotateDeg(new Random().nextFloat(-180, 180));
            doNotMove();
            move(step.x, step.y);
        }
    }

    private void checkPlayer(GameContext context) {
        attack(context.mainHeroEntity(), context);
    }

    private Vector2 followTarget(Vector2 myPos, Vector2 playerPos, float speed) {
        Vector2 direction = new Vector2(playerPos).sub(myPos);

        if (getBody().getLinearVelocity().len2() < 0.05f) {
            return direction.nor().scl(speed * 2).rotateDeg(new Random().nextFloat(-180, 180));
        }

        return direction.nor().scl(speed);
    }
}

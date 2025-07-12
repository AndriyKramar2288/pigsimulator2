package com.banew.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.banew.other.records.GameContext;
import com.banew.other.records.MovingEntityTexturesPerDirectionPack;

import java.util.*;

public class Zombie extends MovingEntity {

    public Zombie(Sprite sprite,
                  Body body,
                  Map<String, MovingEntityTexturesPerDirectionPack> animations,
                  TextureAtlas textureAtlas,
                  Set<Rectangle> collisions) {
        super(sprite, body, animations, textureAtlas);
    }

    private List<Vector2> wayToPlayer = new ArrayList<>();
    private float resetTimer = 0;

    @Override
    public void render(GameContext context) {
        super.render(context);
        checkPlayer(context);

        resetTimer += Gdx.graphics.getDeltaTime();
        wayToPlayer.removeIf(v -> new Vector2(v).sub(getCenterCoordinates()).len2() < .5f);

        if (wayToPlayer.isEmpty() || resetTimer > 1) {
            resetTimer = 0;
            wayToPlayer = context.currentLevel().getPathFinder().findPath(
                getCenterCoordinates(),
                context.mainHeroEntity().getCenterCoordinates()
            );
        }
        else {
            Vector2 stepToTarget = followTarget(
                getCenterCoordinates(),
                wayToPlayer.get(0),
                1f, context
            );

            doNotMove();
            move(stepToTarget.x, stepToTarget.y);
        }
    }

    private void checkPlayer(GameContext context) {
        if (getCenterCoordinates().sub(context.mainHeroEntity().getCenterCoordinates()).len2() < .2f) {

            context.playerInfo().setPlayerHealth(context.playerInfo().getPlayerHealth() - 3f);

            context.soundContainer().play("stons");

            context.mainHeroEntity().getBody().applyLinearImpulse(
                getCenterCoordinates().sub(context.mainHeroEntity().getCenterCoordinates()).scl(-.04f),
                context.mainHeroEntity().getCenterCoordinates(),
                true
            );
        }
    }

    private Vector2 followTarget(Vector2 myPos, Vector2 playerPos, float speed, GameContext context) {
        Vector2 direction = new Vector2(playerPos).sub(myPos);

        if (getBody().getLinearVelocity().len2() < 0.05f) {
            return direction.nor().scl(speed).rotateDeg(new Random().nextFloat(-180, 180));
        }

        return direction.nor().scl(speed);
    }
}

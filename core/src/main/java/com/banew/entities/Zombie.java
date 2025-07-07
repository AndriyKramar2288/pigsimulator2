package com.banew.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.banew.other.records.GameContext;
import com.banew.other.records.MovingEntityTexturesPerDirectionPack;

import java.util.Map;
import java.util.Set;

public class Zombie extends MovingEntity {
    private final Set<Rectangle> collisions;

    public Zombie(Sprite sprite,
                  Body body,
                  Map<String, MovingEntityTexturesPerDirectionPack> animations,
                  TextureAtlas textureAtlas,
                  Set<Rectangle> collisions) {
        super(sprite, body, animations, textureAtlas);
        this.collisions = collisions;
    }


    @Override
    public void render(GameContext context) {
        super.render(context);

        Vector2 stepToTarget = followTarget(
            getBody().getPosition(),
            context.currentLevel().getMainHeroEntity().getCenterCoordinates(),
            .01f, context
        );

        move(stepToTarget.x, stepToTarget.y);
    }

    private Vector2 followTarget(Vector2 myPos, Vector2 playerPos, float speed, GameContext context) {
        Vector2 direction = new Vector2(playerPos).sub(myPos);
        if (direction.len2() < .2f) {

            context.playerInfo().setPlayerHealth(context.playerInfo().getPlayerHealth() - 3f);

            context.mainHeroEntity().getBody().applyLinearImpulse(
                getCenterCoordinates().sub(context.mainHeroEntity().getCenterCoordinates()).scl(-.04f),
                context.mainHeroEntity().getCenterCoordinates(),
                true
            );

            return new Vector2();
        }

        direction.nor().scl(speed);

        // Припустимо, у тебе є метод, що повертає bounding rectangle тіла в певній позиції
        Rectangle futureRect = getBoundingRectangleAtPosition(myPos.x + direction.x * 2, myPos.y + direction.y * 2);

        // Перевірка колізії
        boolean collision = collisions.stream().anyMatch(rect -> rect.overlaps(futureRect));

        if (!collision) {
            doNotMove();
            return new Vector2(direction.x, direction.y);
        } else {
            // Спробуємо рух по X
            Rectangle futureRectX = getBoundingRectangleAtPosition(myPos.x + direction.x * 2, myPos.y);
            boolean collisionX = collisions.stream().anyMatch(rect -> rect.overlaps(futureRectX));

            // Спробуємо рух по Y
            Rectangle futureRectY = getBoundingRectangleAtPosition(myPos.x, myPos.y + direction.y * 2);
            boolean collisionY = collisions.stream().anyMatch(rect -> rect.overlaps(futureRectY));

            if (!collisionX) {
                doNotMove();
                return new Vector2(direction.x > 0 ? speed : -speed, 0);
            } else if (!collisionY) {
                doNotMove();
                return new Vector2(0, direction.y > 0 ? speed : -speed);
            }
           return new Vector2();
        }
    }

    // Приклад методу, який отримує bounding box тіла в певній позиції
    private Rectangle getBoundingRectangleAtPosition(float x, float y) {
        // Припустимо, ти знаєш розміри тіла (ширина, висота)
        float width = animationsScales.get(movingSide).x * getSprite().getWidth();  // заміни на реальні значення
        float height = animationsScales.get(movingSide).y * getSprite().getHeight();

        return new Rectangle(x - width / 2, y - height / 2, width, height);
    }
}

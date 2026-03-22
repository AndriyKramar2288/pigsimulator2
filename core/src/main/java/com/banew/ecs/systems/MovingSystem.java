package com.banew.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.banew.ecs.components.MovingComponent;
import com.banew.ecs.components.SpriteComponent;

public class MovingSystem extends IteratingSystem {
    private final ComponentMapper<SpriteComponent> sm = ComponentMapper.getFor(SpriteComponent.class);
    private final ComponentMapper<MovingComponent> mm = ComponentMapper.getFor(MovingComponent.class);

    public MovingSystem() {
        super(Family.all(SpriteComponent.class, MovingComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float v) {
        SpriteComponent cs = sm.get(entity);
        MovingComponent mcs = mm.get(entity);

        mcs.timer += v;

        // Встановлюємо правильний кадр анімації
        if (!mcs.isMoving()) {
            cs.sprite.setRegion(mcs.waitingRegions.get(mcs.movingSide));
        } else {
            cs.sprite.setRegion(mcs.animationList.get(mcs.movingSide).getKeyFrame(mcs.timer, true));
        }

        // РУХ!
        if (cs.body != null) {
            Vector2 currentVelocity = cs.body.getLinearVelocity();

            // Віднімаємо наш власний минулий рух, щоб залишити тільки зовнішні сили (напр. відштовхування)
            Vector2 externalVelocity = new Vector2(currentVelocity).sub(mcs.selfMoving);

            // Оновлюємо наш власний рух новим кроком (наприклад, від клавіатури чи ШІ)
            mcs.selfMoving.set(mcs.movingStep);

            // Додаємо наш рух до згасаючих зовнішніх сил
            Vector2 finalVelocity = new Vector2(mcs.selfMoving).add(externalVelocity.scl(0.95f));
            cs.body.setLinearVelocity(finalVelocity);

            // Перевірка зміни напрямку для анімації (але НЕ для перестворення тіла Box2D щокадру!)
            int newSide = computeMovingSide(mcs.movingStep.x, mcs.movingStep.y, mcs.movingSide);
            if (newSide != mcs.movingSide) {
                mcs.movingSide = newSide;
                // ТУТ МИ БІЛЬШЕ НЕ РОБИМО resetBody()! Це вбивало Box2D і ламало фізику.
                // Хітбокс тепер симетричний (або квадратний) і не залежить від повороту.
            }
        }

        // Скидаємо наказ на рух для наступного кадру
        mcs.movingStep.setZero();
    }

    private int computeMovingSide(float stepX, float stepY, int currentSide) {
        if (stepX == 0 && stepY == 0) return currentSide;

        if (Math.abs(stepX) > Math.abs(stepY)) {
            return stepX > 0 ? 3 : 1; // 3 → вправо, 1 → вліво
        } else {
            return stepY > 0 ? 0 : 2; // 0 → вгору, 2 → вниз
        }
    }
}

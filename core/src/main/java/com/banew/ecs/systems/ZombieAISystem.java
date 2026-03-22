package com.banew.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.banew.ecs.components.*;
import com.banew.other.records.GameContext;
import java.util.Random;

public class ZombieAISystem extends IteratingSystem {
    private final ComponentMapper<ZombieComponent> zm = ComponentMapper.getFor(ZombieComponent.class);
    private final ComponentMapper<SpriteComponent> sm = ComponentMapper.getFor(SpriteComponent.class);
    private final ComponentMapper<MovingComponent> mm = ComponentMapper.getFor(MovingComponent.class);
    private final ComponentMapper<AliveParamsComponent> am = ComponentMapper.getFor(AliveParamsComponent.class);

    private final Random random = new Random();
    private final GameContext context; // Передаємо через конструктор!

    public ZombieAISystem(GameContext context) {
        super(Family.all(ZombieComponent.class, SpriteComponent.class, MovingComponent.class, AliveParamsComponent.class).get());
        this.context = context;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        var zombie = zm.get(entity);
        var sprite = sm.get(entity);
        var move = mm.get(entity);
        var alive = am.get(entity);

        zombie.resetTimer += deltaTime;
        zombie.attackTimer += deltaTime;

        // Отримуємо ціль (Гравця)
        Entity player = context.mainHeroEntity();
        var playerSprite = player.getComponent(SpriteComponent.class);
        var playerAlive = player.getComponent(AliveParamsComponent.class);

        // 1. ЛОГІКА АТАКИ (з твого attack() і checkPlayer())
        float distToPlayer2 = sprite.getCenterCoordinates().sub(playerSprite.getCenterCoordinates()).len2();
        boolean near = distToPlayer2 < alive.info.getAttackDistance();

        if (near && zombie.attackTimer > 0.4f) {
            zombie.attackTimer = 0;

            // Наносимо шкоду гравцю
            playerAlive.info.changeHealth(-random.nextFloat(40, 70));
            playerAlive.reloadHpTimer = 0; // target.injured()

            // Відкидаємо гравця (фізика)
            Vector2 impulse = sprite.getCenterCoordinates().sub(playerSprite.getCenterCoordinates()).nor().scl(-0.3f);
            playerSprite.body.applyLinearImpulse(impulse, playerSprite.getCenterCoordinates(), true);

            // ВІЗУАЛЬНІ ЕФЕКТИ (Кров, звуки).
            // Робимо їх тільки якщо рівень активний, щоб фонові зомбі не кричали нам у вуха!
            if (context.currentLevel() == context.globalGameContext().currentContainer()) {
                context.effectAnimationsContainer().playAnimation("effect_animations/blood_1", playerSprite.getCenterCoordinates(), .2f);
                // context.soundContainer().play("stons");
            }
        }

        // 2. ЛОГІКА ПОШУКУ ШЛЯХУ (з твого старого render)
        if (zombie.wayToPlayer.isEmpty() || zombie.resetTimer > 1) {
            zombie.resetTimer = 0;
            zombie.wayToPlayer = context.currentLevel().findPath(
                sprite.getCenterCoordinates(),
                playerSprite.getCenterCoordinates()
            );
        }

        // 3. ЛОГІКА РУХУ (з твого старого step)
        float speed = 1f;
        zombie.wayToPlayer.removeIf(v -> new Vector2(v).sub(sprite.getCenterCoordinates()).len2() < .15f);

        if (!zombie.wayToPlayer.isEmpty()) {
            Vector2 targetNode = zombie.wayToPlayer.get(0);
            Vector2 direction = new Vector2(targetNode).sub(sprite.getCenterCoordinates());

            // Твій followTarget
            if (sprite.body.getLinearVelocity().len2() < 0.05f) {
                direction.nor().scl(speed * 2).rotateDeg(random.nextFloat(-180, 180));
            } else {
                direction.nor().scl(speed);
            }

            // Передаємо наказ на рух! MovingSystem це підхопить.
            move.movingStep.set(direction.x, direction.y);
        }
    }
}

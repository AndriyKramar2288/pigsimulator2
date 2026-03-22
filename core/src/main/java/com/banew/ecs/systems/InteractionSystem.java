package com.banew.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;
import com.banew.ecs.components.*;
import com.banew.other.records.GameContext;

public class InteractionSystem extends IteratingSystem {
    private final ComponentMapper<SpriteComponent> sm = ComponentMapper.getFor(SpriteComponent.class);
    private final ComponentMapper<InteractableComponent> im = ComponentMapper.getFor(InteractableComponent.class);
    private final ComponentMapper<ChestComponent> cm = ComponentMapper.getFor(ChestComponent.class);

    private final GameContext context;
    private final Vector3 mousePos = new Vector3();
    private static final float CRITICAL_DISTANCE = 0.2f;

    public InteractionSystem(GameContext context) {
        // Беремо всіх, у кого є Спрайт і на кого можна клікати
        super(Family.all(SpriteComponent.class, InteractableComponent.class).get());
        this.context = context;
    }

    @Override
    public void update(float deltaTime) {
        // 🔥 ОДИН РАЗ НА КАДР конвертуємо координати миші для ВСІХ сутностей! 🔥
        mousePos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        context.camera().unproject(mousePos);

        super.update(deltaTime); // Запускає processEntity
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        var spriteComp = sm.get(entity);
        var interactable = im.get(entity);
        var chest = cm.get(entity); // Може бути null, якщо це не скриня

        // 1. ПЕРЕВІРКА НАВЕДЕННЯ (Hover)
        float x = spriteComp.sprite.getX();
        float y = spriteComp.sprite.getY();
        float width = spriteComp.sprite.getWidth();
        float height = spriteComp.sprite.getHeight();

        interactable.isHovered = (mousePos.x >= x && mousePos.x <= x + width &&
            mousePos.y >= y && mousePos.y <= y + height);

        Entity player = context.mainHeroEntity();
        var playerMainComp = player.getComponent(MainHeroComponent.class);
        var playerSprite = player.getComponent(SpriteComponent.class);

        if (interactable.isHovered) {
            float dist2 = playerSprite.getCenterCoordinates().sub(spriteComp.getCenterCoordinates()).len2();
            boolean isNear = dist2 < CRITICAL_DISTANCE;

            // Змінюємо курсор
            interactable.cursors.use(isNear);

            // 2. ЛОГІКА КЛІКУ
            if (isNear && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                context.soundContainer().play("chest");
                playerMainComp.openedContainer = entity; // Запам'ятовуємо сутність скрині!
            }
        }

        // 3. ВІЗУАЛ СКРИНІ (Якщо у об'єкта є ChestComponent)
        if (chest != null) {
            boolean isOpen = playerMainComp.openedContainer == entity;
            spriteComp.sprite.setRegion(chest.chestsRegions.get(isOpen ? 1 : 0));
            //spriteComp.setTextureScale(2); // Твій старий скейл із Chest.render()
        }
    }
}

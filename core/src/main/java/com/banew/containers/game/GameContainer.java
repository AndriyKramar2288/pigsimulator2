package com.banew.containers.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.banew.containers.Container;
import com.banew.containers.GlobalGameContext;
import com.banew.containers.game.gui.GuiContainer;
import com.banew.containers.menu.character.creation.NewCharacterProperties;
import com.banew.ecs.EntityFactory;
import com.banew.ecs.components.AliveParamsComponent;
import com.banew.ecs.components.MainHeroComponent;
import com.banew.ecs.components.MovingComponent;
import com.banew.ecs.components.SpriteComponent;
import com.banew.external.GeneralSettings;
import com.banew.other.records.GameContext;
import com.banew.utilites.Reference;

import java.util.Map;
import java.util.function.Consumer;

public class GameContainer implements Container {
    private final SpriteBatch spriteBatch;
    private final Viewport viewport;
    private final GuiContainer guiContainer;
    private boolean isMoving = false;
    public static boolean isDebug = false;
    private final GameContext context;
    private final WalkingSoundResolver walkingSoundResolver = new WalkingSoundResolver();
    private static final float PLAYER_SPEED = .7f;
    private static final Texture WHITE_PIXEL;

    static {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA4444);
        pixmap.setColor(new Color(1, 1, 1, .5f));
        pixmap.fill();
        WHITE_PIXEL = new Texture(pixmap);
        pixmap.dispose();
    }

    public GameContainer(GlobalGameContext globalGameContext, NewCharacterProperties properties) {
        spriteBatch = new SpriteBatch();
        viewport = new FillViewport(8, 5);

        GeneralSettings generalSettings = globalGameContext.getGeneralSettings();
        guiContainer = new GuiContainer(globalGameContext);

        // 1. Створюємо рівні (Поки без систем, бо системам потрібен GameContext)
        EntityFactory entityFactory = new EntityFactory(null, generalSettings, globalGameContext.getTextureAtlas());
        var levels = generalSettings.getLevels(entityFactory);

        var currentLevel = levels.stream().filter(l -> l.getLevelName().equals("main")).findFirst().orElseThrow();

        // Шукаємо героя в Engine рівня
        Entity mainHeroEntity = null;
        for (Entity e : currentLevel.getEngine().getEntitiesFor(Family.all(MainHeroComponent.class).get())) {
            mainHeroEntity = e;
            break;
        }
        if (mainHeroEntity == null) throw new RuntimeException("Головного бандіта не найшли на поточному рівні!");

        // 2. Створюємо контекст
        context = new GameContext(
            globalGameContext, mainHeroEntity, viewport, new Reference<>(currentLevel), levels,
            new EffectAnimationsContainer(globalGameContext.getTextureAtlas(), generalSettings.getEffectAnimations())
        );

        // 3. Ініціалізуємо системи на всіх рівнях, передаючи їм контекст!
        for (GameLevel level : levels) {
            level.initSystems(context, spriteBatch);
        }

        // Вмикаємо активний рівень
        currentLevel.switchTo(mainHeroEntity, mainHeroEntity.getComponent(SpriteComponent.class).body.getPosition(), context);
    }

    @Override
    public void render() {
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        isMoving = false;
        movingRender();

        float deltaTime = Gdx.graphics.getDeltaTime();

        // 1. Оновлюємо логіку на всіх рівнях
        context.levels().forEach(gameLevel -> gameLevel.step(deltaTime));

        // 2. Малюємо карту і світло тільки для АКТИВНОГО рівня
        context.currentLevel().renderVisuals(context);

        // 3. Малюємо спрайти активного рівня (це зробить DrawSystem всередині step() вище)
        // DrawSystem має викликати batch.begin() і batch.end() всередині свого update()!

        context.effectAnimationsContainer().render(spriteBatch, deltaTime);

        if (isDebug) {
            context.currentLevel().drawCollisions(spriteBatch, WHITE_PIXEL);
        }

        guiContainer.render(context);
    }

    public void resize(int width, int height) {
        viewport.update(width, height, false);
        guiContainer.resize(width, height);
    }

    @Override
    public Viewport viewport() { return viewport; }

    private void movingRender() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) isDebug = !isDebug;

        moveMainHeroRender();

        if (isMoving) walkingSoundResolver.play(context);
        else walkingSoundResolver.stop(context);

        context.camera().position.lerp(new Vector3(context.mainHeroEntity().getComponent(SpriteComponent.class).getCenterCoordinates(), 0f), .125f);
        context.camera().zoom = isMoving ? smoothZoom(1.05f) : smoothZoom(1f);
        context.camera().update();
    }

    private void moveMainHero(float x, float y) {
        // Тепер рух задається через вектор в MovingComponent!
        context.mainHeroEntity().getComponent(MovingComponent.class).movingStep.set(-x, -y);
    }

    private void moveMainHeroRender() {
        Map<Integer, Consumer<Float>> keysMovementAction = Map.of(
            Input.Keys.W, (speed) -> moveMainHero(0, -speed),
            Input.Keys.S, (speed) -> moveMainHero(0, speed),
            Input.Keys.A, (speed) -> moveMainHero(speed, 0),
            Input.Keys.D, (speed) -> moveMainHero(-speed, 0)
        );

        Entity hero = context.mainHeroEntity();
        var movingComp = hero.getComponent(MovingComponent.class);
        var aliveComp = hero.getComponent(AliveParamsComponent.class);

        // 1. Обробка РУХУ
        movingComp.movingStep.setZero(); // Завжди скидаємо перед новим кадром

        keysMovementAction.forEach((key, value) -> {
            if (Gdx.input.isKeyPressed(key)) {
                boolean isRunning = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && aliveComp.info.getStamina() > 0;

                hero.getComponent(MainHeroComponent.class).isRunning = isRunning;
                movingComp.animationList.get(movingComp.movingSide).setFrameDuration(isRunning ? .1f : .15f);

                if (isRunning) {
                    value.accept(PLAYER_SPEED * 1.5f);
                    aliveComp.info.changeStamina(-0.05f);
                } else {
                    value.accept(PLAYER_SPEED);
                }
                isMoving = true;
            }
        });

        // 2. Обробка АТАКИ (Пробіл)
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            // Дістаємо активну зброю (якщо вона є) або б'ємо кулаками
            // Для прикладу, просто шукаємо ціль і б'ємо її "з руки" (твоя стара логіка selfAttack)
            context.currentLevel().getFocusEntity(context).ifPresent(target -> {
                var targetSprite = target.getComponent(SpriteComponent.class);
                var targetAlive = target.getComponent(AliveParamsComponent.class);
                var heroSprite = hero.getComponent(SpriteComponent.class);

                float dist2 = targetSprite.getCenterCoordinates().sub(heroSprite.getCenterCoordinates()).len2();

                if (dist2 < aliveComp.info.getAttackDistance()) {
                    if (aliveComp.info.getStamina() >= 10) {
                        aliveComp.info.changeStamina(-10);
                        targetAlive.info.changeHealth(-15); // Шкода від кулака
                        targetAlive.reloadHpTimer = 0;

                        context.soundContainer().play("classic_punch");

                        // Відкидаємо ворога
                        Vector2 impulse = heroSprite.getCenterCoordinates().sub(targetSprite.getCenterCoordinates()).nor().scl(-0.1f);
                        targetSprite.body.applyLinearImpulse(impulse, targetSprite.getCenterCoordinates(), true);
                    }
                }
            });
        }
    }

    private float smoothZoom(float targetZoom) {
        float zoomSpeed = 4.5f;
        return context.camera().zoom + (targetZoom - context.camera().zoom) * zoomSpeed * Gdx.graphics.getDeltaTime();
    }

    @Override
    public void dispose() {
        context.levels().forEach(GameLevel::dispose);
        guiContainer.dispose();
        WHITE_PIXEL.dispose();
    }
}

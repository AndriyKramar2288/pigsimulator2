package com.banew.containers.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.banew.containers.Container;
import com.banew.containers.GlobalGameContext;
import com.banew.containers.game.gui.GuiContainer;
import com.banew.entities.alive.MainHeroEntity;
import com.banew.external.GeneralSettings;
import com.banew.factories.EntityFactory;
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

    public GameContainer(GlobalGameContext globalGameContext) {
        spriteBatch = new SpriteBatch();
        viewport = new FillViewport(8, 5);

        GeneralSettings generalSettings = globalGameContext.getGeneralSettings();

        guiContainer = new GuiContainer(globalGameContext);

        EntityFactory entityFactory = new EntityFactory(generalSettings, globalGameContext.getTextureAtlas());
        var levels = generalSettings.getLevels(entityFactory);

        var currentLevel = levels.stream()
            .filter(l -> l.getLevelName().equals("main"))
            .findFirst()
            .orElseThrow();

        var mainHeroEntity = (MainHeroEntity) currentLevel.getEntitySet().stream()
            .filter(e -> e instanceof MainHeroEntity)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Головного бандіта не найшли на поточному рівні!"));

        context = new GameContext(
            globalGameContext,
            mainHeroEntity,
            viewport,
            new Reference<>(currentLevel),
            levels,
            new EffectAnimationsContainer(globalGameContext.getTextureAtlas(), generalSettings.getEffectAnimations())
        );

        currentLevel.switchTo(mainHeroEntity, mainHeroEntity.getBody().getPosition(), context);
    }

    @Override
    public void render() {
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        isMoving = false;
        movingRender();

        context.levels().forEach(gameLevel -> gameLevel.step(context));

        context.currentLevel().render(context, spriteBatch);
        context.effectAnimationsContainer().render(spriteBatch, Gdx.graphics.getDeltaTime());

        // колізії
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
    public Viewport viewport() {
        return viewport;
    }

    private void movingRender() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            isDebug = !isDebug;
        }

        moveMainHeroRender();

        if (isMoving)
            walkingSoundResolver.play(context);
        else
            walkingSoundResolver.stop(context);

        context.camera().position.lerp(new Vector3(context.mainHeroEntity().getCenterCoordinates(), 0f), .125f);
        context.camera().zoom = isMoving ? smoothZoom(1.05f) : smoothZoom(1f);

        context.camera().update();
    }

    private void moveMainHero(float x, float y) {
        context.mainHeroEntity().move(-x, -y);
    }

    private void moveMainHeroRender() {
        Map<Integer, Consumer<Float>> keysMovementAction = Map.of(
            Input.Keys.W, (speed) -> moveMainHero(0, -speed),
            Input.Keys.S, (speed) -> moveMainHero(0, speed),
            Input.Keys.A, (speed) -> moveMainHero(speed, 0),
            Input.Keys.D, (speed) -> moveMainHero(-speed, 0)
        );

        context.mainHeroEntity().doNotMove();
        keysMovementAction.forEach((key, value) -> {
            if (Gdx.input.isKeyPressed(key)) {
                if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && context.playerInfo().getStamina() > 0) {
                    context.mainHeroEntity().setRunning(true);
                    value.accept(PLAYER_SPEED * 1.5f);
                    context.playerInfo().changeStamina(-0.05f);
                }
                else {
                    context.mainHeroEntity().setRunning(false);
                    value.accept(PLAYER_SPEED);
                }

                isMoving = true;
            }
        });
    }

    private float smoothZoom(float targetZoom) {
        // Швидкість наближення (чим менше, тим плавніше)
        float zoomSpeed = 4.5f; // одиниці за секунду

        // Поточний зум → поступово тягнемо до цілі
        return context.camera().zoom + (targetZoom - context.camera().zoom) * zoomSpeed * Gdx.graphics.getDeltaTime();
    }

    @Override
    public void dispose() {
        context.levels().forEach(GameLevel::dispose);
        guiContainer.dispose();
        WHITE_PIXEL.dispose();
    }
}

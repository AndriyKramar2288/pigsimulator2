package com.banew.containers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.banew.containers.gui.GuiContainer;
import com.banew.entities.MainHeroEntity;
import com.banew.external.GeneralSettings;
import com.banew.factories.EntityFactory;
import com.banew.other.records.GameContext;
import com.banew.utilites.Reference;

import java.util.HashSet;
import java.util.Map;
import java.util.function.Consumer;

public class GameContainer implements Disposable {
    private final SpriteBatch spriteBatch;
    private final Viewport viewport;

    private final GuiContainer guiContainer;

    private boolean isMoving = false;
    private float staminaReloadTimer = 0f;
    private float hpReloadTimer = 0f;
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

    public GameContainer(GeneralSettings generalSettings) {
        spriteBatch = new SpriteBatch();
        viewport = new FillViewport(8, 5);
        guiContainer = new GuiContainer(generalSettings);

        String COLLISION_LAYER_NAME = generalSettings.getCollision_level_name();

        String atlas_path = generalSettings.getMain_atlas_src();
        TextureAtlas textureAtlas = new TextureAtlas(Gdx.files.internal(atlas_path));
        EntityFactory entityFactory = new EntityFactory(generalSettings, textureAtlas);

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
            mainHeroEntity,
            viewport,
            new Reference<>(currentLevel),
            levels,
            new SoundContainer(generalSettings),
            new EffectAnimationsContainer(textureAtlas, generalSettings.getEffectAnimations())
        );

        currentLevel.switchTo(mainHeroEntity, mainHeroEntity.getBody().getPosition(), context);
    }

    public void render() {
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        renderScene();
        spriteBatch.begin();
        renderSprites(spriteBatch);
        spriteBatch.end();
        renderLight();
        guiContainer.render(context);
    }

    public void resize(int width, int height) {
        viewport.update(width, height, false);
        guiContainer.resize(width, height);
    }


    private void renderSprites(SpriteBatch spriteBatch) {
        isMoving = false;

        movingRender();

        context.levels().forEach(level -> {
            level.getWorld().step(Gdx.graphics.getDeltaTime(), 1, 1);
            new HashSet<>(level.getEntitySet()).forEach(
                entity -> entity.step(context, level)
            );
        });

        new HashSet<>(context.currentLevel().getEntitySet()).forEach(e -> {
            e.draw(spriteBatch);
            e.render(context);

            if (isDebug) {
                e.getCollisionSprite(WHITE_PIXEL).draw(spriteBatch);
            }
        });

        context.effectAnimationsContainer().render(spriteBatch, Gdx.graphics.getDeltaTime());

        if (isDebug) {
            context.currentLevel().getCollisions().forEach(r -> {
                spriteBatch.draw(WHITE_PIXEL, r.x, r.y, r.width, r.height);
            });
        }
    }

    private void renderScene() {
        if (context.currentLevel() != null) {
            context.currentLevel().renderMap(context.camera());
        }
    }

    private void renderLight() {
        context.levels().forEach(l -> l.getLightMode().step());
        context.currentLevel().getLightMode().render(context);
    }

    private void movingRender() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            isDebug = !isDebug;
        }

        moveMainHeroRender();
        reloadStats();

        if (isMoving)
            walkingSoundResolver.play(context);
        else
            walkingSoundResolver.stop(context);

        context.camera().position.lerp(new Vector3(context.mainHeroEntity().getCenterCoordinates(), 0f), .125f);
        context.camera().zoom = isMoving ? smoothZoom(1.05f) : smoothZoom(1f);

        context.camera().update();
    }

    private void reloadStats() {
        if (context.mainHeroEntity().isRunning()) {
            staminaReloadTimer = 0f;
        }

        hpReloadTimer += Gdx.graphics.getDeltaTime();
        staminaReloadTimer += Gdx.graphics.getDeltaTime();

        if (staminaReloadTimer > 3 && context.playerInfo().getStamina() < context.playerInfo().getMaxStamina()) {
            context.playerInfo().changeStamina(.7f);
        }

        if (context.playerInfo().getHealth() < context.playerInfo().getMaxHp()) {
            if ((hpReloadTimer > 5)) {
                context.playerInfo().changeHealth(.1f);
            }
        }
        else {
            hpReloadTimer = 0;
        }
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
